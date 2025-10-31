package com.convertfile.test;

import java.io.File;
import java.io.IOException;

import com.convertfile.service.microService.docx_to_pdf_service;

public class test_convert {
    public static void main(String[] args) throws IOException{
        final String docxPath = "";
        final String pdfPath = "";

        try{
            docx_to_pdf_service convert = new docx_to_pdf_service();

            convert.convertDocxtoPdf(docxPath, pdfPath);
            System.out.println("Starting convert...");

            File pdf = new File(pdfPath);
            if(pdf.exists() && pdf.length() > 0){
                System.out.println("Success!");
            }
            else{
                System.out.println("Fall!");
            }
        }
        catch(Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
