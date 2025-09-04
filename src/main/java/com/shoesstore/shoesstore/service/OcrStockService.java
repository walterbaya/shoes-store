package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.dto.OcrResultDTO;
import com.shoesstore.shoesstore.model.Product;
import com.shoesstore.shoesstore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import org.bytedeco.javacpp.FloatPointer;
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
	private static final Pattern ART_P = Pattern.compile("Ar[t7]\\.?\\s*(\\d{3,6})", Pattern.CASE_INSENSITIVE);

	private static final Pattern SIZE_P = Pattern.compile("(N[°ºo]?|No)\\s*(\\d{2})", Pattern.CASE_INSENSITIVE);

	private static final Pattern COLOR_P = Pattern
			.compile("\\b(NEGRO|NEGR0|MARR[OÓ0]N|SUELA|BLANCO|AZUL|ROJO|GRIS|VERDE)\\b", Pattern.CASE_INSENSITIVE);

	private static final Pattern DESC_P = Pattern.compile("(Bota.*|Zapato.*|Zapatilla.*|Mocasin.*|Sandalia.*)",
			Pattern.CASE_INSENSITIVE);

	private static final Pattern TYPE_P = Pattern.compile("\\b(Bota.*|Zapato.*|Zapatilla.*|Mocasin.*|Sandalia.*)",
			Pattern.CASE_INSENSITIVE);

	// Marca: primeras palabras largas en mayúsculas
	private static final Pattern BRAND_P = Pattern.compile("^([A-ZÁÉÍÓÚÑ ]+?)\\s+(?=Art\\.|N°|\\d)", Pattern.MULTILINE);

	public List<OcrResultDTO> processImage(MultipartFile file, boolean dryRun) throws Exception {
		File tmp = Files.createTempFile("ocr-", ".png").toFile();
		file.transferTo(tmp);

		try {
			// 1. Correr OCR
			String text = runTesseract(tmp);

			// 2. Parsear texto
			Product producto = parsearEtiqueta(text);

			// 3. Devolver DTO (si quisieras persistir, acá lo harías)
			OcrResultDTO dto = new OcrResultDTO(producto.getName(), "Palma Shoes", producto.getMaterial(),
					producto.getColor(), producto.getType(), producto.getDescription(), " "
			// producto.getSize().
			);

			return List.of(dto);

		} finally {
			tmp.delete();
		}
	}

	private File preprocessLabel(File imgFile) throws IOException {
		Mat src = opencv_imgcodecs.imread(imgFile.getAbsolutePath());
		if (src.empty())
			throw new IOException("No se pudo abrir la imagen");

		// Escala de grises
		Mat gray = new Mat();
		opencv_imgproc.cvtColor(src, gray, opencv_imgproc.COLOR_BGR2GRAY);

		// Reescalar (x3 para mejorar letras pequeñas)
		Mat resized = new Mat();
		opencv_imgproc.resize(gray, resized, new Size(gray.cols() * 3, gray.rows() * 3), 0, 0,
				opencv_imgproc.INTER_CUBIC);

		// Suavizar ruido con filtro de mediana
		Mat denoised = new Mat();
		opencv_imgproc.medianBlur(resized, denoised, 3);

		// Afilar (unsharp mask)
		Mat sharpened = new Mat();
		Mat kernel = new Mat(3, 3, opencv_core.CV_32F);
		FloatPointer kernelData = new FloatPointer(0, -1, 0, -1, 5, -1, 0, -1, 0);
		kernel.data().put(kernelData);
		opencv_imgproc.filter2D(denoised, sharpened, denoised.depth(), kernel);

		// Binarización adaptativa
		Mat binary = new Mat();
		opencv_imgproc.adaptiveThreshold(sharpened, binary, 255, opencv_imgproc.ADAPTIVE_THRESH_MEAN_C,
				opencv_imgproc.THRESH_BINARY, 35, 15);

		// Detectar contornos
		MatVector contours = new MatVector();
		opencv_imgproc.findContours(binary.clone(), contours, opencv_imgproc.RETR_EXTERNAL,
				opencv_imgproc.CHAIN_APPROX_SIMPLE);

		Rect bestRect = null;
		double maxArea = 0;

		for (int i = 0; i < contours.size(); i++) {
			Rect rect = opencv_imgproc.boundingRect(contours.get(i));
			double aspectRatio = rect.width() / (double) rect.height();
			double area = rect.width() * rect.height();

			if (area > maxArea && aspectRatio > 1.5 && aspectRatio < 6.0) {
				maxArea = area;
				bestRect = rect;
			}
		}

		if (bestRect == null) {
			throw new IOException("No se pudo detectar la etiqueta en la imagen");
		}

		// Recortar región de la etiqueta
		Mat cropped = new Mat(binary, bestRect);

		File croppedFile = new File(imgFile.getParent(), "label_cropped.png");
		opencv_imgcodecs.imwrite(croppedFile.getAbsolutePath(), cropped);

		System.out.println("✅ Etiqueta preprocesada y recortada en: " + croppedFile.getAbsolutePath());
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

// --- Parser principal ---
private Product parsearEtiqueta(String ocrText) {
    Product p = new Product();

    // Normalización básica del OCR: colapsar espacios y separar por líneas
    String[] rawLines = ocrText.replaceAll("\\s+", " ").trim().split("\\R+");

    // Limpiamos líneas vacías / ruido corto
    List<String> lines = new ArrayList<>();
    for (String r : rawLines) {
        String s = r == null ? "" : r.trim();
        if (s.isEmpty()) continue;
        // descartar ruidos muy cortos que no aportan (ej: "IAE")
        if (s.replaceAll("[^A-Za-zÁÉÍÓÚÑ0-9]", "").length() < 3 && s.split("\\s+").length <= 1) continue;
        lines.add(s);
    }

    // --- Artículo y Talle (buscar en cualquier línea)
    Pattern ART_P = Pattern.compile("Ar[t1i]\\.?\\s*[:\\.-]?\\s*(\\d{2,6})", Pattern.CASE_INSENSITIVE);
    Pattern SIZE_P = Pattern.compile("(?:N[°ºo0]?|No)\\s*(\\d{2})", Pattern.CASE_INSENSITIVE);
    for (String l : lines) {
        Matcher mA = ART_P.matcher(l);
        if (mA.find()) p.setName(Long.parseLong(mA.group(1)));

        Matcher mS = SIZE_P.matcher(l);
        //if (mS.find()) p.setSize(mS.group(1));
    }

    // --- Tipo + Descripción (línea que contiene el tipo)
    Pattern TYPE_P = Pattern.compile("(Bota|Zapato|Zapatilla|Mocasi[nn]|Mocasín|Sandalia)", Pattern.CASE_INSENSITIVE);
    for (String l : lines) {
        Matcher mT = TYPE_P.matcher(l);
        if (mT.find()) {
            String tipo = mT.group(1);
            // normalizar "Mocasin" vs "Mocasín"
            if (tipo.equalsIgnoreCase("mocasin") || tipo.equalsIgnoreCase("mocasín")) tipo = "Mocasin";
            String rest = l.substring(mT.end()).trim();
            p.setType(cap(tipo));
            String desc = rest.isEmpty() ? cap(tipo) : capWords(tipo + " " + rest);
            p.setDescription(desc);
            break;
        }
    }

    // --- Material + Color (línea que contenga un color)
    if (p.getColor() == null || p.getMaterial() == null) {
        for (String l : lines) {
            String[] mc = extractMaterialColorFromLine(l);
            if (mc != null) {
                if (p.getMaterial() == null && mc[0] != null) p.setMaterial(mc[0]);
                if (p.getColor() == null && mc[1] != null) p.setColor(mc[1]);
                if (p.getMaterial() != null && p.getColor() != null) break;
            }
        }
    }

    // --- Fallbacks / limpieza final
    if (p.getMaterial() != null) p.setMaterial(capWords(p.getMaterial()));
    if (p.getColor() != null)    p.setColor(p.getColor()); // ya canonizado
    if (p.getDescription() != null) p.setDescription(capWords(p.getDescription()));

    return p;
}


	// --- Helpers de color/material ---
	private static final Set<String> COLOR_LEXICON = new HashSet<>(Arrays.asList(
	    "negro","blanco","marron","marrón","suela","azul","rojo","gris","verde",
	    "beige","nude","bordo","bordó","camel","crema","tostado","hueso","celeste",
	    "fucsia","violeta","amarillo","naranja","lila"
	));

	private static String cleanToken(String s) {
	    if (s == null) return "";
	    s = s.trim().toLowerCase(Locale.ROOT);
	    // Arreglos típicos de OCR para palabras (no números)
	    s = s.replace('0', 'o')
	         .replace('1', 'i')
	         .replace('5', 's');
	    // quitar signos sueltos
	    s = s.replaceAll("[^a-záéíóúñ]", "");
	    return s;
	}

	private static boolean isColorWord(String w) {
	    if (w == null || w.isEmpty()) return false;
	    String c = w;
	    if (c.equals("marr0n")) c = "marron";
	    return COLOR_LEXICON.contains(c);
	}

	private static String canonicalColor(String w) {
	    if (w == null) return null;
	    String c = w.toLowerCase(Locale.ROOT);
	    c = c.replace('0','o');
	    if (c.equals("marron")) return "Marrón";
	    if (c.equals("bordo"))  return "Bordó";
	    return cap(c);
	}

	private static String cap(String s) {
	    if (s == null || s.isEmpty()) return s;
	    return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
	}

	private static String capWords(String s) {
	    if (s == null) return null;
	    String[] t = s.trim().split("\\s+");
	    StringBuilder sb = new StringBuilder();
	    for (String x : t) {
	        if (x.isEmpty()) continue;
	        if (sb.length() > 0) sb.append(' ');
	        sb.append(cap(x));
	    }
	    return sb.toString();
	}

	// Intenta detectar color en la línea, tolerando cortes ("blanc o" => "blanco").
	// Devuelve [material, color] o null si no hay color en la línea.
	private static String[] extractMaterialColorFromLine(String line) {
	    if (line == null) return null;
	    String[] raw = line.trim().split("\\s+");
	    if (raw.length == 0) return null;

	    // Limpio tokens para comparación, pero conservo originales para material.
	    List<String> tokens = new ArrayList<>(Arrays.asList(raw));
	    List<String> clean = new ArrayList<>();
	    for (String r : raw) clean.add(cleanToken(r));

	    // Buscar color exacto o color unido a token corto siguiente (p. ej., "blanc" + "o")
	    int colorIdx = -1;
	    String colorCanon = null;

	    for (int i = 0; i < clean.size(); i++) {
	        String t = clean.get(i);
	        if (t.isEmpty()) continue;

	        // 1) token exacto es color
	        if (isColorWord(t)) {
	            colorIdx = i;
	            colorCanon = canonicalColor(t);
	            break;
	        }
	        // 2) token + siguiente muy corto forman color (blanc + o)
	        if (i + 1 < clean.size()) {
	            String next = clean.get(i + 1);
	            if (next.length() <= 2) {
	                String joined = t + next; // ej: "blanc" + "o" = "blanco"
	                if (isColorWord(joined)) {
	                    colorIdx = i; // color empieza en i y usa i+1
	                    colorCanon = canonicalColor(joined);
	                    // eliminamos el token extra del material al armarlo
	                    // (saltaremos i+1 al construir material)
	                    break;
	                }
	            }
	        }
	    }

	    if (colorIdx == -1) return null; // no se detectó color en esta línea

	    // Material = tokens antes del color (saltando símbolos y cosas cortas)
	    StringBuilder mat = new StringBuilder();
	    for (int i = 0; i < colorIdx; i++) {
	        String tOrig = tokens.get(i).trim();
	        String tClean = clean.get(i);
	        if (tClean.isEmpty()) continue;
	        // evitamos palabras de 1 char sueltas (ruidos comunes)
	        if (tClean.length() == 1) continue;
	        if (mat.length() > 0) mat.append(' ');
	        mat.append(tOrig);
	    }
	    // Si el color se armó con el siguiente token (cortado), lo saltamos del material
	    if (colorIdx + 1 < tokens.size()) {
	        String t = clean.get(colorIdx) + clean.get(colorIdx + 1);
	        if (isColorWord(t)) {
	            // no agregar tokens[colorIdx] ni tokens[colorIdx+1] al material (ya hecho)
	        }
	    }

	    String material = capWords(mat.toString());
	    String color = colorCanon;
	    if (material.isEmpty()) material = null;
	    return new String[]{material, color};
	}



}
