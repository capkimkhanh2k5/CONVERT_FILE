package com.convertfile.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PdfTool (PDF to DOCX conversion)
 */
class PdfToolTest {

    @TempDir
    Path tempDir;

    @Test
    void testGetPageCount_ValidPdf() throws Exception {
        // Arrange - Create a simple PDF with 1 page
        Path pdfFile = tempDir.resolve("test.pdf");
        
        // Create a simple PDF using PDFBox
        org.apache.pdfbox.pdmodel.PDDocument document = new org.apache.pdfbox.pdmodel.PDDocument();
        document.addPage(new org.apache.pdfbox.pdmodel.PDPage());
        document.addPage(new org.apache.pdfbox.pdmodel.PDPage());
        document.save(pdfFile.toFile());
        document.close();

        // Act
        int pageCount = PdfTool.getPageCount(pdfFile.toString());
        
        // Assert
        assertEquals(2, pageCount, "PDF should have 2 pages");
    }

    @Test
    void testGetPageCount_NonExistentFile() {
        // Arrange
        Path nonExistentPdf = tempDir.resolve("nonexistent.pdf");
        
        // Act
        int pageCount = PdfTool.getPageCount(nonExistentPdf.toString());
        
        // Assert
        assertEquals(0, pageCount, "Should return 0 for non-existent file");
    }

    @Test
    void testConvertPdfToDocx_ValidInput() throws Exception {
        // Arrange - Create a PDF with text content
        Path pdfFile = tempDir.resolve("test.pdf");
        Path docxOutput = tempDir.resolve("output.docx");
        
        // Create PDF with text
        org.apache.pdfbox.pdmodel.PDDocument document = new org.apache.pdfbox.pdmodel.PDDocument();
        org.apache.pdfbox.pdmodel.PDPage page = new org.apache.pdfbox.pdmodel.PDPage();
        document.addPage(page);
        
        org.apache.pdfbox.pdmodel.PDPageContentStream contentStream = 
            new org.apache.pdfbox.pdmodel.PDPageContentStream(document, page);
        
        contentStream.beginText();
        contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 12);
        contentStream.newLineAtOffset(100, 700);
        contentStream.showText("This is a test PDF document.");
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Second line of text.");
        contentStream.endText();
        contentStream.close();
        
        document.save(pdfFile.toFile());
        document.close();

        // Act
        PdfTool.convertPdfToDocx(pdfFile.toString(), docxOutput.toString());
        
        // Assert
        assertTrue(Files.exists(docxOutput), "DOCX output file should exist");
        assertTrue(Files.size(docxOutput) > 0, "DOCX file should not be empty");
        
        // Verify it's a valid DOCX by checking ZIP signature (DOCX is a ZIP file)
        byte[] docxHeader = new byte[4];
        try (java.io.FileInputStream fis = new java.io.FileInputStream(docxOutput.toFile())) {
            fis.read(docxHeader);
        }
        
        // DOCX files start with PK (ZIP signature)
        assertEquals('P', docxHeader[0], "DOCX should start with PK (ZIP signature)");
        assertEquals('K', docxHeader[1], "DOCX should have K in signature");
    }

    @Test
    void testConvertPdfToDocx_EmptyPdf() throws Exception {
        // Arrange - Create an empty PDF
        Path pdfFile = tempDir.resolve("empty.pdf");
        Path docxOutput = tempDir.resolve("output.docx");
        
        org.apache.pdfbox.pdmodel.PDDocument document = new org.apache.pdfbox.pdmodel.PDDocument();
        org.apache.pdfbox.pdmodel.PDPage page = new org.apache.pdfbox.pdmodel.PDPage();
        document.addPage(page);
        document.save(pdfFile.toFile());
        document.close();

        // Act
        PdfTool.convertPdfToDocx(pdfFile.toString(), docxOutput.toString());
        
        // Assert
        assertTrue(Files.exists(docxOutput), "DOCX should be created even for empty PDF");
        assertTrue(Files.size(docxOutput) > 0, "DOCX file should not be empty (has structure)");
    }

    @Test
    void testConvertPdfToDocx_FileNotFound() {
        // Arrange
        Path nonExistentPdf = tempDir.resolve("nonexistent.pdf");
        Path docxOutput = tempDir.resolve("output.docx");
        
        // Act & Assert
        assertThrows(Exception.class, () -> {
            PdfTool.convertPdfToDocx(nonExistentPdf.toString(), docxOutput.toString());
        }, "Should throw exception when input file doesn't exist");
    }

    @Test
    void testConvertPdfToDocx_InvalidPdfFile() throws Exception {
        // Arrange
        Path invalidPdf = tempDir.resolve("invalid.pdf");
        Path docxOutput = tempDir.resolve("output.docx");
        
        // Create a file that's not a valid PDF
        Files.writeString(invalidPdf, "This is not a valid PDF file");
        
        // Act & Assert
        assertThrows(Exception.class, () -> {
            PdfTool.convertPdfToDocx(invalidPdf.toString(), docxOutput.toString());
        }, "Should throw exception when input is not a valid PDF file");
    }

    @Test
    void testConvertPdfToDocx_MultiPagePdf() throws Exception {
        // Arrange - Create a multi-page PDF
        Path pdfFile = tempDir.resolve("multipage.pdf");
        Path docxOutput = tempDir.resolve("output.docx");
        
        org.apache.pdfbox.pdmodel.PDDocument document = new org.apache.pdfbox.pdmodel.PDDocument();
        
        // Create 3 pages with text
        for (int i = 1; i <= 3; i++) {
            org.apache.pdfbox.pdmodel.PDPage page = new org.apache.pdfbox.pdmodel.PDPage();
            document.addPage(page);
            
            org.apache.pdfbox.pdmodel.PDPageContentStream contentStream = 
                new org.apache.pdfbox.pdmodel.PDPageContentStream(document, page);
            
            contentStream.beginText();
            contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText("Page " + i + " content");
            contentStream.endText();
            contentStream.close();
        }
        
        document.save(pdfFile.toFile());
        document.close();

        // Act
        PdfTool.convertPdfToDocx(pdfFile.toString(), docxOutput.toString());
        
        // Assert
        assertTrue(Files.exists(docxOutput), "DOCX output file should exist");
        assertTrue(Files.size(docxOutput) > 0, "DOCX file should not be empty");
        
        // Read and verify DOCX contains text from all pages
        org.apache.poi.xwpf.usermodel.XWPFDocument docx = 
            new org.apache.poi.xwpf.usermodel.XWPFDocument(
                new java.io.FileInputStream(docxOutput.toFile())
            );
        
        String docxText = docx.getParagraphs().stream()
            .map(p -> p.getText())
            .reduce("", (a, b) -> a + b);
        
        assertTrue(docxText.contains("Page 1"), "Should contain page 1 content");
        assertTrue(docxText.contains("Page 2"), "Should contain page 2 content");
        assertTrue(docxText.contains("Page 3"), "Should contain page 3 content");
        
        docx.close();
    }
}
