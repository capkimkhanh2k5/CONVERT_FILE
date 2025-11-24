package com.convertfile.service.ConvertService;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Service for converting image files to PDF format
 */
public class image_to_pdf_service {
    
    /**
     * Convert image file to PDF
     * 
     * @param imagePath Path to input image file (JPG, PNG, etc.)
     * @param pdfPath Path to output PDF file
     * @throws Exception If conversion fails
     */
    public void convertImageToPdf(String imagePath, String pdfPath) throws Exception {
        if (imagePath == null || imagePath.isEmpty()) {
            throw new IllegalArgumentException("Image path cannot be null or empty");
        }
        if (pdfPath == null || pdfPath.isEmpty()) {
            throw new IllegalArgumentException("PDF path cannot be null or empty");
        }
        
        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            throw new java.io.FileNotFoundException("Image file not found: " + imagePath);
        }
        
        Document document = null;
        FileOutputStream fos = null;
        
        try {
            document = new Document();
            fos = new FileOutputStream(pdfPath);
            PdfWriter.getInstance(document, fos);
            document.open();
            
            Image img = Image.getInstance(imagePath);
            img.scaleToFit(document.getPageSize().getWidth(), document.getPageSize().getHeight());
            img.setAlignment(Image.ALIGN_CENTER);
            document.add(img);
            
            System.out.println("Image converted to PDF: " + pdfPath);
            
        } finally {
            if (document != null) {
                document.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }
}
