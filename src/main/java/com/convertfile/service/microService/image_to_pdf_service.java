package com.convertfile.service.microService;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;

public class image_to_pdf_service {
    public void convertImageToPdf(String imagePath, String pdfPath) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
        document.open();
        Image img = Image.getInstance(imagePath);
        img.scaleToFit(document.getPageSize().getWidth(), document.getPageSize().getHeight());
        img.setAlignment(Image.ALIGN_CENTER);
        document.add(img);
        document.close();
        System.out.println("Image converted to PDF.");
    }
}

