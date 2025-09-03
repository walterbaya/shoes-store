package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.dto.OcrResultDTO;
import com.shoesstore.shoesstore.model.Product;
import com.shoesstore.shoesstore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Size;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class OcrStockService {

    private final ProductRepository productRepository;

    // Regex
    private static final Pattern ART_P =
            Pattern.compile("Ar[t7]\\.?\\s*(\\d{3,6})", Pattern.CASE_INSENSITIVE);

    private static final Pattern SIZE_P =
            Pattern.compile("(N[°ºo]?|No)\\s*(\\d{2})", Pattern.CASE_INSENSITIVE);

    private static final Pattern COLOR_P =
            Pattern.compile("\\b(NEGRO|NEGR0|MARR[OÓ0]N|SUELA|BLANCO|AZUL|ROJO|GRIS|VERDE)\\b",
                    Pattern.CASE_INSENSITIVE);

    private static final Pattern DESC_P =
            Pattern.compile("(Bota.*|Zapato.*|Zapatilla.*)", Pattern.CASE_INSENSITIVE);

    private static final Pattern TIPO =
            Pattern.compile("\\b(Bota|Zapato|Zapatilla)\\b", Pattern.CASE_INSENSITIVE);

    private static final Pattern BRAND =
            Pattern.compile("(PALMA)", Pattern.CASE_INSENSITIVE);


    public File preprocessImage(File input) throws IOException {
        Mat src = opencv_imgcodecs.imread(input.getAbsolutePath());
        if (src.empty()) {
            throw new IOException("No se pudo cargar la imagen: " + input.getAbsolutePath());
        }

        // Escala de grises
        Mat gray = new Mat();
        opencv_imgproc.cvtColor(src, gray, opencv_imgproc.COLOR_BGR2GRAY);

        // Suavizado
        opencv_imgproc.GaussianBlur(gray, gray, new Size(5, 5), 0);

        // Binarización adaptativa
        Mat thresh = new Mat();
        opencv_imgproc.adaptiveThreshold(gray, thresh, 255,
                opencv_imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                opencv_imgproc.THRESH_BINARY, 15, 10);

        // Guardar a archivo temporal
        File tmp = Files.createTempFile("clean_", ".png").toFile();
        opencv_imgcodecs.imwrite(tmp.getAbsolutePath(), thresh);

        return tmp;
    }

    // ------------------- Procesamiento OCR -------------------
    public List<OcrResultDTO> processImage(MultipartFile file, boolean dryRun) throws Exception {
        File tmp = Files.createTempFile("ocr-", Objects.requireNonNull(file.getOriginalFilename())).toFile();
        file.transferTo(tmp);

        File clean = preprocessImage(tmp);

        try {
            String fullText = runTesseract(clean);
            System.out.println("=== OCR RESULT ===\n" + fullText);

            List<String> blocks = splitIntoBlocks(fullText);
            Map<String, Product> grouped = new HashMap<>();

            for (String block : blocks) {
                Map<String, String> f = extractFields(block);
                if (!f.containsKey("name") || !f.containsKey("size")) continue;

                String key = f.get("name") + "-" + f.get("color") + "-" + f.get("size");
                if (!grouped.containsKey(key)) {
                    grouped.put(key, buildProductFromFields(f));
                } else {
                    grouped.get(key).setStock(grouped.get(key).getStock() + 1);
                }
            }

            List<OcrResultDTO> results = new ArrayList<>();
            for (Product p : grouped.values()) {
                Product saved = null;
                if (!dryRun) {
                    saved = productRepository.save(p);
                }
                results.add(new OcrResultDTO(
                        p.getDescription(),
                        String.valueOf(p.getName()),
                        p.getBrand(),
                        p.getMaterial(),
                        p.getColor(),
                        p.getType(),
                        p.getDescription(),
                        p.getSize().getDisplayValue(),
                        p.getStock(),
                        saved
                ));
            }

            return results;

        } finally {
            tmp.delete();
            clean.delete();
        }
    }
    private String runTesseract(File img) throws TesseractException {
        ITesseract t = new Tesseract();

        t.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
        t.setLanguage("spa+eng");

        // Modo más tolerante
        t.setPageSegMode(11); // Sparse text, mejor para fotos

        // Mejorar exactitud (aunque más lento)
        t.setOcrEngineMode(1); // LSTM only

        return t.doOCR(img);
    }


    private List<String> splitIntoBlocks(String text) {
        String[] lines = text.split("\\R+");
        List<String> blocks = new ArrayList<>();
        for (String ln : lines) {
            if (!ln.trim().isEmpty()) blocks.add(ln.trim());
        }
        return blocks;
    }

    private Map<String,String> extractFields(String block) {
        Map<String,String> map = new HashMap<>();
        Matcher m;

        m = ART_P.matcher(block);
        if (m.find()) map.put("name", m.group(1));

        m = SIZE_P.matcher(block);
        if (m.find()) map.put("size", m.group(1));

        m = COLOR_P.matcher(block);
        if (m.find()) map.put("color", capitalize(m.group(1)));


        m = BRAND.matcher(block);
        if (m.find()) map.put("brand", m.group(1));

        m = DESC_P.matcher(block);
        if (m.find()) map.put("description", capitalize(m.group(1)));

        m = TIPO.matcher(block);
        if (m.find()) map.put("type", capitalize(m.group(1)));

        return map;
    }

    private Product buildProductFromFields(Map<String,String> f) {
        Product p = new Product();

        p.setName(Long.parseLong(f.get("name")));
        p.setBrand(f.get("brand"));
        p.setMaterial(f.getOrDefault("material", null));
        p.setColor(f.getOrDefault("color", null));
        p.setDescription(f.getOrDefault("description", null));
        p.setType(null);
        p.setGender(Product.Gender.UNISEX);

        String sz = f.getOrDefault("size", "37");
        p.setSize(sizeFromNumber(sz));

        p.setPrice(0.0);
        p.setStock(1);
        p.setCreatedAt(LocalDateTime.now());

        return p;
    }

    private Product.ShoeSize sizeFromNumber(String num) {
        return switch (num) {
            case "35" -> Product.ShoeSize.S35;
            case "36" -> Product.ShoeSize.S36;
            case "37" -> Product.ShoeSize.S37;
            case "38" -> Product.ShoeSize.S38;
            case "39" -> Product.ShoeSize.S39;
            case "40" -> Product.ShoeSize.S40;
            case "41" -> Product.ShoeSize.S41;
            case "42" -> Product.ShoeSize.S42;
            case "43" -> Product.ShoeSize.S43;
            case "44" -> Product.ShoeSize.S44;
            case "45" -> Product.ShoeSize.S45;
            case "46" -> Product.ShoeSize.S46;
            default   -> Product.ShoeSize.S37;
        };
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
