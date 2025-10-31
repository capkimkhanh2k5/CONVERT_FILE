package com.convertfile.service.microService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Document;

public class xml_to_docx_service {
    public void convertXmlToDocx(String inPath, String outPath) throws IOException{
        System.out.println("Start convert xml to docx: " + inPath);
        try{
            String xml = new String(Files.readAllBytes(Paths.get(inPath)));

            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
            MainDocumentPart mainPart = wordMLPackage.getMainDocumentPart();

            org.docx4j.wml.Document xmlDocument = (Document) XmlUtils.unmarshalString(xml);

            mainPart.setJaxbElement(xmlDocument);

            wordMLPackage.save(new File(outPath));

        }catch(Docx4JException edocx){
            System.out.println("Error: " + edocx.getMessage());
            edocx.printStackTrace();
            throw new IOException("Error in convert xml to docx!");
        } catch (Exception e) {
            System.out.println("Error in process!");
            e.printStackTrace();
            throw new IOException("Not found in Process!");
        }

        System.out.println("End convert xml to docx: " + outPath);

    }
}
