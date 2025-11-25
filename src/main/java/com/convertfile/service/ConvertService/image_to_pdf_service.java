package com.convertfile.service.ConvertService;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;

public class image_to_pdf_service {
    public void convertImageToPdf(String imagePath, String pdfPath) throws Exception {
        File imageFile = new File(imagePath);
        
        // Debug: Check file magic bytes to detect actual format
        try (java.io.FileInputStream fis = new java.io.FileInputStream(imageFile)) {
            byte[] header = new byte[12];
            int bytesRead = fis.read(header);
            System.out.println("   📄 Image file path: " + imagePath);
            System.out.println("   📄 File size: " + imageFile.length() + " bytes");
            System.out.print("   📄 Magic bytes (hex): ");
            for (int i = 0; i < Math.min(bytesRead, 12); i++) {
                System.out.print(String.format("%02X ", header[i]));
            }
            System.out.println();
            
            // Detect and validate format
            String detectedFormat = "Unknown";
            if (bytesRead >= 12 && header[0] == 0x52 && header[1] == 0x49 && 
                header[2] == 0x46 && header[3] == 0x46 &&
                header[8] == 0x57 && header[9] == 0x45 && 
                header[10] == 0x42 && header[11] == 0x50) {
                detectedFormat = "WebP";
            } else if (bytesRead >= 12 && header[4] == 0x66 && header[5] == 0x74 && 
                header[6] == 0x79 && header[7] == 0x70 &&
                header[8] == 0x61 && header[9] == 0x76 && 
                header[10] == 0x69 && header[11] == 0x66) {
                detectedFormat = "AVIF";
                System.err.println("   ❌ AVIF format detected - not supported");
                throw new Exception("AVIF format is not supported. Please convert to PNG/JPG first (use online tools like CloudConvert).");
            } else if (bytesRead >= 2 && header[0] == (byte)0x89 && header[1] == 0x50) {
                detectedFormat = "PNG";
            } else if (bytesRead >= 2 && header[0] == (byte)0xFF && header[1] == (byte)0xD8) {
                detectedFormat = "JPEG";
            }
            System.out.println("   ℹ️  Detected format: " + detectedFormat);
        }
        
        // Read image using ImageIO (supports PNG, JPG, GIF, BMP, etc.)
        BufferedImage bufferedImage = ImageIO.read(imageFile);
        if (bufferedImage == null) {
            System.err.println("   ❌ ImageIO cannot read this file");
            throw new Exception("Cannot read image file. Unsupported or corrupted format: " + imagePath);
        }
        
        // Convert BufferedImage to byte array in PNG format
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        byte[] imageBytes = baos.toByteArray();
        
        // Create PDF
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
        document.open();
        
        Image img = Image.getInstance(imageBytes);
        img.scaleToFit(document.getPageSize().getWidth(), document.getPageSize().getHeight());
        img.setAlignment(Image.ALIGN_CENTER);
        document.add(img);
        document.close();
        
        System.out.println("Image converted to PDF.");
    }
}

