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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OcrStockService {

    private final ProductRepository productRepository;

    // ------------------- Regex -------------------
    private static final Pattern ART_P =
            Pattern.compile("Ar[t1i]\\.?\\s*(\\d{2,6})", Pattern.CASE_INSENSITIVE);

    private static final Pattern SIZE_P =
            Pattern.compile("(N[°ºo0]?|No)\\s*(\\d{2})", Pattern.CASE_INSENSITIVE);

    private static final Pattern TYPE_P =
            Pattern.compile("(Bota|Zapato|Zapatilla|Mocasin|Sandalia)", Pattern.CASE_INSENSITIVE);
    
    private static final Pattern DESCRIPTION_P =
    	    Pattern.compile("Bota.*|Zapato.*|Zapatilla.*|Mocasin.*|Sandalia.*", Pattern.CASE_INSENSITIVE);


    // Colores posibles normalizados
    private static final Map<String, String> COLOR_MAP = Map.ofEntries(
            Map.entry("NEGRO", "Negro"),
            Map.entry("NEGR0", "Negro"),
            Map.entry("BLANCO", "Blanco"),
            Map.entry("BLANC0", "Blanco"),
            Map.entry("MARRON", "Marrón"),
            Map.entry("MARR0N", "Marrón"),
            Map.entry("SUELA", "Suela"),
            Map.entry("AZUL", "Azul"),
            Map.entry("ROJO", "Rojo"),
            Map.entry("GRIS", "Gris"),
            Map.entry("VERDE", "Verde")
    );

    // ------------------- OCR Pipeline -------------------
    public List<OcrResultDTO> processImage(MultipartFile file, boolean dryRun) throws Exception {
        File tmp = Files.createTempFile("ocr-", ".png").toFile();
        file.transferTo(tmp);

        try {
            // 1. Correr OCR
            String text = runTesseract(tmp);

            // 2. Parsear texto
            Product producto = parsearEtiqueta(text);

            // 3. Devolver DTO
            OcrResultDTO dto = new OcrResultDTO(
                    producto.getName(),
                    "Palma Shoes",
                    producto.getMaterial(),
                    producto.getColor(),
                    producto.getType(),
                    producto.getDescription(),
                    " "//producto.getSize()
            );

            return List.of(dto);

        } finally {
            tmp.delete();
        }
    }

    // ------------------- Preprocesado Bytedeco -------------------
    private File preprocessLabel(File imgFile) throws IOException {
        Mat src = opencv_imgcodecs.imread(imgFile.getAbsolutePath());
        if (src.empty()) throw new IOException("No se pudo abrir la imagen");

        // Escala de grises
        Mat gray = new Mat();
        opencv_imgproc.cvtColor(src, gray, opencv_imgproc.COLOR_BGR2GRAY);

        // Binarización (umbral adaptativo)
        Mat binary = new Mat();
        opencv_imgproc.adaptiveThreshold(
                gray, binary, 255,
                opencv_imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                opencv_imgproc.THRESH_BINARY, 35, 10
        );

        // Suavizado
        Mat denoised = new Mat();
        opencv_imgproc.medianBlur(binary, denoised, 3);

        // Guardar recortada
        File croppedFile = new File(imgFile.getParent(), "label_cropped.png");
        opencv_imgcodecs.imwrite(croppedFile.getAbsolutePath(), denoised);

        return croppedFile;
    }

    private String runTesseract(File img) throws TesseractException, IOException {
        ITesseract t = new Tesseract();

        File label = preprocessLabel(img);

        t.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
        t.setLanguage("spa");
        t.setPageSegMode(4); // texto en bloque
        t.setOcrEngineMode(1); // LSTM only

        return t.doOCR(label);
    }

    // ------------------- Parser -------------------
    private Product parsearEtiqueta(String ocrText) {
        Product p = new Product();

        String cleanText = ocrText.replaceAll("\\s+", " ").trim();
        String[] lineas = cleanText.split("\\R+");

        // ---- Material + Color
        for (String linea : lineas) {
            String[] matCol = extractMaterialColorFromLine(linea);
            if (matCol != null) {
                p.setMaterial(matCol[0]);
                p.setColor(matCol[1]);
                break;
            }
        }

        // ---- Artículo + Talle
        for (String linea : lineas) {
            Matcher m1 = ART_P.matcher(linea);
            if (m1.find()) p.setName(Long.parseLong(m1.group(1)));

            Matcher m2 = SIZE_P.matcher(linea);
            //if (m2.find()) p.setSize(m2.group(2));
        }

        String todasLineas = String.join(" ", lineas); // une con espacios
        
        for (String linea : lineas) {
            linea = linea.trim();
            if (linea.isEmpty()) continue;

            Matcher m = TYPE_P.matcher(linea);
            if (m.find()) {
                p.setType(cap(m.group(1)));
                continue;
            }

        }
        
        for (String linea : lineas) {
            linea = linea.trim();
            if (linea.isEmpty()) continue;

            Matcher ml = DESCRIPTION_P.matcher(linea);
            if (ml.find()) {
                p.setDescription(cap(ml.group(0)));
            }


        }




        return p;
    }

    // ------------------- Helpers -------------------
    private static String cleanToken(String token) {
        if (token == null) return "";
        return token.replaceAll("[^A-Za-zÁÉÍÓÚÑ0-9]", "")
                .replace('0', 'O')
                .replace('1', 'I')
                .replace('5', 'S')
                .toUpperCase();
    }

    private static boolean isColorWord(String token) {
        if (token == null) return false;
        return COLOR_MAP.containsKey(token.toUpperCase());
    }

    private static String canonicalColor(String token) {
        if (token == null) return null;
        return COLOR_MAP.getOrDefault(token.toUpperCase(), cap(token));
    }

    private static String cap(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
    }

    private static String capWords(String s) {
        if (s == null) return null;
        return Arrays.stream(s.split("\\s+"))
                .map(OcrStockService::cap)
                .collect(Collectors.joining(" "));
    }

    private static boolean isGoodMaterialToken(String token) {
        if (token == null) return false;
        String clean = cleanToken(token);
        if (clean.isEmpty()) return false;
        if (clean.length() >= 4) return true;
        if (clean.length() == 3) {
            return clean.matches(".*[BCDFGHJKLMNPQRSTVWXYZ].*"); // descarta solo-vocales
        }
        return false;
    }

    private static String[] extractMaterialColorFromLine(String line) {
        if (line == null) return null;
        String[] raw = line.trim().split("\\s+");
        if (raw.length == 0) return null;

        List<String> tokens = Arrays.asList(raw);
        List<String> clean = tokens.stream().map(OcrStockService::cleanToken).toList();

        int colorIdx = -1;
        String colorCanon = null;
        for (int i = 0; i < clean.size(); i++) {
            String t = clean.get(i);
            if (isColorWord(t)) {
                colorIdx = i;
                colorCanon = canonicalColor(t);
                break;
            }
            if (i + 1 < clean.size()) {
                String joined = clean.get(i) + clean.get(i+1);
                if (isColorWord(joined)) {
                    colorIdx = i;
                    colorCanon = canonicalColor(joined);
                    break;
                }
            }
        }

        if (colorIdx == -1) return null;

        int startIdx = -1;
        for (int i = colorIdx - 1; i >= 0; i--) {
            if (isGoodMaterialToken(tokens.get(i))) {
                startIdx = i;
                break;
            }
        }

        String material = null;
        if (startIdx != -1) {
            material = capWords(tokens.get(startIdx));
        }

        return new String[]{ material, colorCanon };
    }
}

