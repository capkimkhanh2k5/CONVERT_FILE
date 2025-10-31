package com.convertfile.service.microService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

public class docx_to_xml_service {
    public void convertDocxToXml(String inPath, String outPath) throws IOException{
        System.out.println("Start convert docx to xml: " + inPath);

        try{
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new File(inPath));
            String xml = wordMLPackage.getMainDocumentPart().getXML();

            Files.write(new File(outPath).toPath(), xml.getBytes());

        }catch(Docx4JException edocx){
            System.out.println("Error: " + edocx.getMessage());
            edocx.printStackTrace();
            throw new IOException("Error in convert docx to xml!");
        }
        catch(Exception ex){
            System.out.println("Error in process!");
            ex.printStackTrace();
            throw new IOException("Not found in Process!");
        }

        System.out.println("End convert docx to xml: " + outPath);
    }
}
