package com.convertfile.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DOCX to PDF conversion service
 */
class DocxToPdfServiceTest {

    @TempDir
    Path tempDir;
    
    private docx_to_pdf_service service;
    
    @BeforeEach
    void setUp() {
        service = new docx_to_pdf_service();
    }

    @Test
    void testConvertDocxtoPdf_ValidInput() throws Exception {
        // Arrange
        Path docxFile = tempDir.resolve("test.docx");
        Path pdfOutput = tempDir.resolve("output.pdf");
        
        // Create a simple DOCX file using POI
        org.apache.poi.xwpf.usermodel.XWPFDocument document = new org.apache.poi.xwpf.usermodel.XWPFDocument();
        org.apache.poi.xwpf.usermodel.XWPFParagraph paragraph = document.createParagraph();
        org.apache.poi.xwpf.usermodel.XWPFRun run = paragraph.createRun();
        run.setText("This is a test document for DOCX to PDF conversion.");
        run.addBreak();
        run.setText("Second line of text.");
        
        try (java.io.FileOutputStream out = new java.io.FileOutputStream(docxFile.toFile())) {
            document.write(out);
        }
        document.close();

        // Act
        service.convertDocxtoPdf(docxFile.toString(), pdfOutput.toString());
        
        // Assert
        assertTrue(Files.exists(pdfOutput), "PDF output file should exist");
        assertTrue(Files.size(pdfOutput) > 0, "PDF file should not be empty");
        
        // Verify it's a valid PDF by checking magic bytes
        byte[] pdfHeader = Files.readAllBytes(pdfOutput);
        assertTrue(pdfHeader.length >= 4, "PDF file should have header");
        assertEquals('%', pdfHeader[0], "PDF should start with %");
        assertEquals('P', pdfHeader[1], "PDF should have P");
        assertEquals('D', pdfHeader[2], "PDF should have D");
        assertEquals('F', pdfHeader[3], "PDF should have F");
    }

    @Test
    void testConvertDocxtoPdf_FileNotFound() {
        // Arrange
        Path nonExistentDocx = tempDir.resolve("nonexistent.docx");
        Path pdfOutput = tempDir.resolve("output.pdf");
        
        // Act & Assert
        assertThrows(Exception.class, () -> {
            service.convertDocxtoPdf(nonExistentDocx.toString(), pdfOutput.toString());
        }, "Should throw exception when input file doesn't exist");
    }

    @Test
    void testConvertDocxtoPdf_InvalidDocxFile() throws Exception {
        // Arrange
        Path invalidDocx = tempDir.resolve("invalid.docx");
        Path pdfOutput = tempDir.resolve("output.pdf");
        
        // Create a file that's not a valid DOCX
        Files.writeString(invalidDocx, "This is not a valid DOCX file");
        
        // Act & Assert
        assertThrows(Exception.class, () -> {
            service.convertDocxtoPdf(invalidDocx.toString(), pdfOutput.toString());
        }, "Should throw exception when input is not a valid DOCX file");
    }

    @Test
    void testConvertDocxtoPdf_EmptyDocument() throws Exception {
        // Arrange
        Path docxFile = tempDir.resolve("empty.docx");
        Path pdfOutput = tempDir.resolve("output.pdf");
        
        // Create an empty DOCX file
        org.apache.poi.xwpf.usermodel.XWPFDocument document = new org.apache.poi.xwpf.usermodel.XWPFDocument();
        try (java.io.FileOutputStream out = new java.io.FileOutputStream(docxFile.toFile())) {
            document.write(out);
        }
        document.close();

        // Act
        service.convertDocxtoPdf(docxFile.toString(), pdfOutput.toString());
        
        // Assert
        assertTrue(Files.exists(pdfOutput), "PDF should be created even for empty DOCX");
        assertTrue(Files.size(pdfOutput) > 0, "PDF file should not be empty (has structure)");
    }

    @Test
    void testConvertDocxtoPdf_MultiParagraphDocument() throws Exception {
        // Arrange
        Path docxFile = tempDir.resolve("multiparagraph.docx");
        Path pdfOutput = tempDir.resolve("output.pdf");
        
        // Create DOCX with multiple paragraphs
        org.apache.poi.xwpf.usermodel.XWPFDocument document = new org.apache.poi.xwpf.usermodel.XWPFDocument();
        
        for (int i = 1; i <= 5; i++) {
            org.apache.poi.xwpf.usermodel.XWPFParagraph paragraph = document.createParagraph();
            org.apache.poi.xwpf.usermodel.XWPFRun run = paragraph.createRun();
            run.setText("This is paragraph number " + i);
        }
        
        try (java.io.FileOutputStream out = new java.io.FileOutputStream(docxFile.toFile())) {
            document.write(out);
        }
        document.close();

        // Act
        service.convertDocxtoPdf(docxFile.toString(), pdfOutput.toString());
        
        // Assert
        assertTrue(Files.exists(pdfOutput), "PDF output file should exist");
        assertTrue(Files.size(pdfOutput) > 0, "PDF file should not be empty");
    }
}
