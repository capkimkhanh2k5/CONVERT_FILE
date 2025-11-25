package com.convertfile.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utility class for PDF operations
 * Tested and verified with unit tests in convertfile-tests module
 */
public class PdfTool {

    /**
     * Get the number of pages in a PDF file
     * 
     * @param inputPath Path to PDF file
     * @return Number of pages, or 0 if error
     */
    public static int getPageCount(String inputPath) {
        try (PDDocument doc = PDDocument.load(new File(inputPath))) {
            return doc.getNumberOfPages();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Convert PDF file to DOCX (Word) format
     * Extracts text from PDF and creates Word document with proper formatting
     * 
     * @param inputPath Path to input PDF file
     * @param outputPath Path to output DOCX file
     * @throws IOException If conversion fails
     */
    public static void convertPdfToDocx(String inputPath, String outputPath) throws IOException {
        File inputFile = new File(inputPath);
        
        // 1. Read content from PDF
        String pdfText = "";
        try (PDDocument document = PDDocument.load(inputFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            pdfText = stripper.getText(document);
        }

        // 2. Create new Word document
        try (XWPFDocument docx = new XWPFDocument()) {
            // Create a paragraph
            XWPFParagraph paragraph = docx.createParagraph();
            XWPFRun run = paragraph.createRun();
            
            // Handle line breaks (Word doesn't understand \n like text)
            String[] lines = pdfText.split("\n");
            for (String line : lines) {
                run.setText(line);
                run.addBreak(); // Line break in Word
            }

            // 3. Save Word file to disk
            try (FileOutputStream out = new FileOutputStream(new File(outputPath))) {
                docx.write(out);
            }
        }
    }
}
