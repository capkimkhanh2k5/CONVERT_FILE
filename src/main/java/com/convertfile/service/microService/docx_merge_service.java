package com.convertfile.service.microService;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

//Gộp nhiều file docx6
public class docx_merge_service {
    public void mergeDocx(List<String> inPaths, String outPath) throws IOException{
        if(inPaths.isEmpty()) return;

        try
        {
            WordprocessingMLPackage mainPackage = WordprocessingMLPackage.load(new File(inPaths.get(0)));
            MainDocumentPart mainPart = mainPackage.getMainDocumentPart();

            for(int i = 1; i < inPaths.size(); i++){
                WordprocessingMLPackage subPackage = WordprocessingMLPackage.load(new File(inPaths.get(i)));
                mainPart.getContent().addAll(subPackage.getMainDocumentPart().getContent());
            }

            mainPackage.save(new File(outPath));
        } catch (Docx4JException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Error in merge docx!");
        } catch (Exception ex) {
            System.out.println("Error in process!");
            ex.printStackTrace();
            throw new IOException("Not found in Process!");
        }

        System.out.println("End merge docx: " + outPath);
    }
}
