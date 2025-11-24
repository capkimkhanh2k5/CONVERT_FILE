package com.convertfile.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.OutputStream;

import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFonts;

/**
 * Service for converting DOCX files to PDF format
 */
public class docx_to_pdf_service {
    
    /**
     * Convert DOCX file to PDF
     * 
     * @param docxPath Path to input DOCX file
     * @param pdfPath Path to output PDF file
     * @throws IOException If conversion fails
     */
    public void convertDocxtoPdf(String docxPath, String pdfPath) throws IOException {
        System.out.println("Start convert docx to pdf: " + docxPath);
        try (OutputStream output = new FileOutputStream(new File(pdfPath))) {

            WordprocessingMLPackage wordPackage = WordprocessingMLPackage.load(new File(docxPath));
            
            // Check if document is empty and add minimal content
            if (wordPackage.getMainDocumentPart().getContent().isEmpty()) {
                System.out.println("Empty document detected - adding placeholder content");
                org.docx4j.wml.ObjectFactory factory = new org.docx4j.wml.ObjectFactory();
                org.docx4j.wml.P paragraph = factory.createP();
                org.docx4j.wml.R run = factory.createR();
                org.docx4j.wml.Text text = factory.createText();
                text.setValue(" "); // Single space as placeholder
                run.getContent().add(text);
                paragraph.getContent().add(run);
                wordPackage.getMainDocumentPart().getContent().add(paragraph);
            }
            
            // Font Set
            Mapper font = new IdentityPlusMapper();
            PhysicalFonts.discoverPhysicalFonts();

            wordPackage.setFontMapper(font);

            Docx4J.toPDF(wordPackage, output);
            
        } catch(Docx4JException edocx) {
            System.out.println("Error: " + edocx.getMessage());
            edocx.printStackTrace();
            throw new IOException("Error in convert docx to pdf!");
        } catch (Exception ex) {
            System.out.println("Error in process!");
            ex.printStackTrace();
            throw new IOException("Not found in Process!");
        }

        System.out.println("End convert docx to pdf: " + pdfPath);
    }
}
