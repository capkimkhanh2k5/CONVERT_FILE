package com.convertfile.service.microService;

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

//Convert file from docx to pdf
public class docx_to_pdf_service {
    public void convertDocxtoPdf (String docxPath, String pdfPath) throws IOException{
        System.out.println("Start convert docx to pdf: " + docxPath);
        try {
            OutputStream output = new FileOutputStream(new File(pdfPath));

            WordprocessingMLPackage wordPackage = WordprocessingMLPackage.load(new File(docxPath));
            
            //Font Set
            Mapper font = new IdentityPlusMapper();
            PhysicalFonts.discoverPhysicalFonts();

            wordPackage.setFontMapper(font);

            Docx4J.toPDF(wordPackage, output);
            
        } catch(Docx4JException edocx){
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
