package com.convertfile.service.microService;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class pdf_to_image_service {
    public void convertPdfToImage(String pdfPath, String outputFolder) throws IOException {
        PDDocument document = PDDocument.load(new File(pdfPath));
        PDFRenderer renderer = new PDFRenderer(document);

        for (int i = 0; i < document.getNumberOfPages(); i++) {
            BufferedImage image = renderer.renderImageWithDPI(i, 300); // 300 DPI
            File outFile = new File(outputFolder + "/page_" + (i + 1) + ".png");
            ImageIO.write(image, "PNG", outFile);
        }

        document.close();
        System.out.println("PDF converted to images.");
    }
}
