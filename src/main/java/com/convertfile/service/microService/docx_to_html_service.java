package com.convertfile.service.microService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.stream.StreamResult;

import org.docx4j.Docx4J;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.convert.out.html.HtmlExporterNG2;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

public class docx_to_html_service {
    public void convertDocxtoHtml(String inPath, String outPath) throws IOException{
        System.out.println("Start convert docx to html: " + inPath);
        
        try{
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new File(inPath));

            String imageFolderPath = new File(outPath).getParent() + File.separator + "images"; // Tạo thư mục chứa ảnh

            File imageDir = new File(imageFolderPath);
            if(!imageDir.exists()){
                imageDir.mkdirs();
            }

            HTMLSettings htmlSettings = Docx4J.createHTMLSettings();

            htmlSettings.setWmlPackage(wordMLPackage);
            htmlSettings.setImageDirPath(imageFolderPath);
            htmlSettings.setImageTargetUri("images");

            OutputStream os = new FileOutputStream(new File(outPath));
            
            HtmlExporterNG2 htmlExporter = new HtmlExporterNG2();
            htmlExporter.html(wordMLPackage, new StreamResult(os), htmlSettings);

            

        } catch(Docx4JException edocx){
            System.out.println("Error: " + edocx.getMessage());
            edocx.printStackTrace();
            throw new IOException("Error in convert docx to html!");
        }
        catch(Exception ex){
            System.out.println("Error in process!");
            ex.printStackTrace();
            throw new IOException("Not found in Process!");
        }

        System.out.println("End convert docx to html: " + outPath);
    }
}
