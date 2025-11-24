package com.convertfile.service.ConvertService;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.*;

/**
 * Service for converting DOCX files to TXT format
 * Uses Apache POI for simple and reliable conversion
 */
public class docx_to_txt_service {

    /**
     * Convert DOCX file to plain text (TXT)
     * 
     * @param docxPath Path to input DOCX file
     * @param txtPath Path to output TXT file
     * @throws IOException If conversion fails
     * @throws IllegalArgumentException If paths are null or empty
     */
    public void convertDocxToTxt(String docxPath, String txtPath) throws IOException {
        // Validate input
        if (docxPath == null || docxPath.isEmpty()) {
            throw new IllegalArgumentException("Input DOCX path cannot be null or empty");
        }
        if (txtPath == null || txtPath.isEmpty()) {
            throw new IllegalArgumentException("Output TXT path cannot be null or empty");
        }
        
        System.out.println("Start convert docx to txt: " + docxPath);
        
        StringBuilder text = new StringBuilder();
        
        try (FileInputStream fis = new FileInputStream(docxPath);
             XWPFDocument document = new XWPFDocument(fis)) {
            
            // Extract paragraphs
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                text.append(paragraph.getText()).append("\n");
            }
            
            // Extract tables
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        text.append(cell.getText()).append("\t");
                    }
                    text.append("\n");
                }
                text.append("\n"); // Empty line after table
            }
            
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Error converting DOCX to TXT: " + e.getMessage(), e);
        } catch (Exception e) {
            // Catch POI exceptions and wrap as IOException
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Error converting DOCX to TXT: " + e.getMessage(), e);
        }
        
        // Write to output file
        try (FileWriter writer = new FileWriter(txtPath)) {
            writer.write(text.toString());
        }
        
        System.out.println("End convert docx to txt: " + txtPath);
    }
}
