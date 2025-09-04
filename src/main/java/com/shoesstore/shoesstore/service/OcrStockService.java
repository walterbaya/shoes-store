package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.dto.OcrResultDTO;
import com.shoesstore.shoesstore.model.Product;
import com.shoesstore.shoesstore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.bytedeco.opencv.global.opencv_core;
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

    // ------------------- Regex -------------------
    private static final Pattern ART_P =
            Pattern.compile("Ar[t7]\\.?\\s*(\\d{3,6})", Pattern.CASE_INSENSITIVE);

    private static final Pattern SIZE_P =
            Pattern.compile("(N[°ºo]?|No)\\s*(\\d{2})", Pattern.CASE_INSENSITIVE);

    private static final Pattern COLOR_P =
            Pattern.compile("\\b(NEGRO|NEGR0|MARR[OÓ0]N|SUELA|BLANCO|AZUL|ROJO|GRIS|VERDE)\\b",
                    Pattern.CASE_INSENSITIVE);

    private static final Pattern DESC_P =
            Pattern.compile("(Bota.*|Zapato.*|Zapatilla.*|Mocasin.*|Sandalia.*)",
                    Pattern.CASE_INSENSITIVE);

    private static final Pattern TYPE_P =
            Pattern.compile("\\b(Bota.*|Zapato.*|Zapatilla.*|Mocasin.*|Sandalia.*)", Pattern.CASE_INSENSITIVE);


    // Marca: primeras palabras largas en mayúsculas
    private static final Pattern BRAND_P =
            Pattern.compile("^([A-ZÁÉÍÓÚÑ ]+?)\\s+(?=Art\\.|N°|\\d)", Pattern.MULTILINE);

    public List<OcrResultDTO> processImage(MultipartFile file, boolean dryRun) throws Exception {
        File tmp = Files.createTempFile("ocr-", ".png").toFile();
        file.transferTo(tmp);

        try {
            // 1. Correr OCR
            String text = runTesseract(tmp);
            System.out.println("=== OCR RESULT ===\n" + text);

            // 2. Parsear texto
            Product producto = parsearEtiqueta(text);
            
            // 3. Devolver DTO (si quisieras persistir, acá lo harías)
            OcrResultDTO dto = new OcrResultDTO(
            		producto.getName(),
            		"Palma Shoes" ,
            		producto.getMaterial(),
                    producto.getColor(),
                    producto.getType(),
                    producto.getDescription(),
                    " "
                    //producto.getSize().
            );

            return List.of(dto);

        } finally {
            tmp.delete();
        }
    }
    
    
    private File preprocessLabel(File imgFile) throws IOException {
        Mat src = opencv_imgcodecs.imread(imgFile.getAbsolutePath());
        if (src.empty()) throw new IOException("No se pudo abrir la imagen");

        // Escala de grises
        Mat gray = new Mat();
        opencv_imgproc.cvtColor(src, gray, opencv_imgproc.COLOR_BGR2GRAY);

        // Binarización (umbral Otsu)
        Mat binary = new Mat();
        opencv_imgproc.threshold(gray, binary, 0, 255,
                opencv_imgproc.THRESH_BINARY | opencv_imgproc.THRESH_OTSU);

        // Encontrar contornos
        MatVector contours = new MatVector();
        opencv_imgproc.findContours(binary.clone(), contours,
                opencv_imgproc.RETR_EXTERNAL, opencv_imgproc.CHAIN_APPROX_SIMPLE);

        Rect bestRect = null;
        double maxArea = 0;

        for (int i = 0; i < contours.size(); i++) {
            Rect rect = opencv_imgproc.boundingRect(contours.get(i));
            double aspectRatio = rect.width() / (double) rect.height();
            double area = rect.width() * rect.height();

            // Filtrar candidatos: etiquetas suelen ser rectángulos grandes y claros
            if (area > maxArea && aspectRatio > 1.5 && aspectRatio < 6.0) {
                maxArea = area;
                bestRect = rect;
            }
        }

        if (bestRect == null) {
            throw new IOException("No se pudo detectar la etiqueta en la imagen");
        }

        // Recortar la región de la etiqueta
        Mat cropped = new Mat(src, bestRect);

        File croppedFile = new File(imgFile.getParent(), "label_cropped.png");
        opencv_imgcodecs.imwrite(croppedFile.getAbsolutePath(), cropped);

        System.out.println("✅ Etiqueta detectada y recortada en: " + croppedFile.getAbsolutePath());
        return croppedFile;
    }

    
    private String runTesseract(File img) throws TesseractException, IOException {
        ITesseract t = new Tesseract();
        
        File label = preprocessLabel(img);

        t.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
        t.setLanguage("spa+eng");
        t.setPageSegMode(4); // párrafos

        t.setOcrEngineMode(1); // LSTM only

        return t.doOCR(label); // ✅ usar la etiqueta recortada
    }


    private Product parsearEtiqueta(String ocrText) {
        Product p = new Product();

        String[] lineas = ocrText.split("\\R+");

        // Línea 1 → Material + Color
        if (lineas.length > 0) {
            String[] parts = lineas[0].trim().split(" ");
            if (parts.length > 0) p.setMaterial(cap(parts[0]));
            if (parts.length > 1) p.setColor(cap(parts[1]));
        }

        // Línea 2 → Art. y N°
        if (lineas.length > 1) {
            //String l2 = lineas[1];
            //Matcher m = Pattern.compile("Art\\.?\\s*(\\d+)").matcher(l2);
            //if (m.find()) p.setName(m.group(1));

            //m = Pattern.compile("N[°ºo]?\\s*(\\d+)").matcher(l2);
            //if (m.find()) p.setSize(m.group(1));
        }

        // Línea 3 → Descripción y Tipo
        if (lineas.length > 2) {
            String desc = lineas[2].trim();
            p.setDescription(cap(desc));

            Matcher m = Pattern.compile("(Bota|Zapato|Zapatilla|Mocasin|Sandalia)", Pattern.CASE_INSENSITIVE).matcher(desc);
            if (m.find()) p.setType(cap(m.group(1)));
        }

        return p;
    }

    private String cap(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
    }

}
