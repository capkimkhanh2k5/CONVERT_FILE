package com.convertfile.service.microService;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class pptx_to_pdf_service {
    public void convertPptxToPdf(String pptxPath, String pdfPath) throws Exception {
        XMLSlideShow ppt = new XMLSlideShow(new File(pptxPath).toURI().toURL().openStream());
        PDDocument pdfDoc = new PDDocument();

        for (XSLFSlide slide : ppt.getSlides()) {
            BufferedImage img = new BufferedImage(1024, 768, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = img.createGraphics();
            graphics.setPaint(Color.WHITE);
            graphics.fillRect(0, 0, 1024, 768);
            slide.draw(graphics);

            PDPage page = new PDPage();
            pdfDoc.addPage(page);
            PDPageContentStream content = new PDPageContentStream(pdfDoc, page);
            ImageIO.write(img, "PNG", new File("tmp.png"));
            content.drawImage(org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject.createFromFile("tmp.png", pdfDoc), 0, 0, 1024, 768);
            content.close();
        }

        pdfDoc.save(pdfPath);
        pdfDoc.close();
        ppt.close();
        new File("tmp.png").delete();
        System.out.println("PowerPoint converted to PDF.");
    }
}

