package com.convertfile.service;

import com.convertfile.service.ConvertService.pptx_to_pdf_service;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class PptxToPdfServiceTest {

    @TempDir
    Path tempDir;

    @Test
    public void testConvertSimplePptxToPdf() throws Exception {
        pptx_to_pdf_service service = new pptx_to_pdf_service();

        // Create a simple PPTX with one slide
        Path pptxPath = tempDir.resolve("simple.pptx");
        XMLSlideShow ppt = new XMLSlideShow();
        ppt.createSlide();
        try (FileOutputStream out = new FileOutputStream(pptxPath.toFile())) {
            ppt.write(out);
        }
        ppt.close();

        Path pdfPath = tempDir.resolve("simple.pdf");

        // Convert PPTX to PDF
        service.convertPptxToPdf(pptxPath.toString(), pdfPath.toString());

        // Verify PDF was created
        assertTrue(Files.exists(pdfPath));
        assertTrue(Files.size(pdfPath) > 0);
    }

    @Test
    public void testConvertMultipleSlidesPptxToPdf() throws Exception {
        pptx_to_pdf_service service = new pptx_to_pdf_service();

        // Create PPTX with multiple slides
        Path pptxPath = tempDir.resolve("multi.pptx");
        XMLSlideShow ppt = new XMLSlideShow();
        
        ppt.createSlide();
        ppt.createSlide();
        ppt.createSlide();
        
        try (FileOutputStream out = new FileOutputStream(pptxPath.toFile())) {
            ppt.write(out);
        }
        ppt.close();

        Path pdfPath = tempDir.resolve("multi.pdf");

        // Convert
        service.convertPptxToPdf(pptxPath.toString(), pdfPath.toString());

        // Verify
        assertTrue(Files.exists(pdfPath));
        assertTrue(Files.size(pdfPath) > 0);
    }

    @Test
    public void testConvertEmptyPptxToPdf() throws Exception {
        pptx_to_pdf_service service = new pptx_to_pdf_service();

        // Create empty PPTX (no slides)
        Path pptxPath = tempDir.resolve("empty.pptx");
        XMLSlideShow ppt = new XMLSlideShow();
        try (FileOutputStream out = new FileOutputStream(pptxPath.toFile())) {
            ppt.write(out);
        }
        ppt.close();

        Path pdfPath = tempDir.resolve("empty.pdf");

        // Convert
        service.convertPptxToPdf(pptxPath.toString(), pdfPath.toString());

        // Verify PDF was created (even if empty)
        assertTrue(Files.exists(pdfPath));
    }

    @Test
    public void testConvertNonExistentPptxThrowsException() {
        pptx_to_pdf_service service = new pptx_to_pdf_service();

        Path pptxPath = tempDir.resolve("nonexistent.pptx");
        Path pdfPath = tempDir.resolve("output.pdf");

        // Should throw exception for non-existent file
        assertThrows(Exception.class, () -> {
            service.convertPptxToPdf(pptxPath.toString(), pdfPath.toString());
        });
    }

    @Test
    public void testConvertPptxToPdfOutput() throws Exception {
        pptx_to_pdf_service service = new pptx_to_pdf_service();

        // Create PPTX
        Path pptxPath = tempDir.resolve("test.pptx");
        XMLSlideShow ppt = new XMLSlideShow();
        ppt.createSlide();
        try (FileOutputStream out = new FileOutputStream(pptxPath.toFile())) {
            ppt.write(out);
        }
        ppt.close();

        Path pdfPath = tempDir.resolve("test.pdf");

        // Convert
        service.convertPptxToPdf(pptxPath.toString(), pdfPath.toString());

        // Verify output
        assertTrue(Files.exists(pdfPath));
        long fileSize = Files.size(pdfPath);
        assertTrue(fileSize > 100, "PDF file should have meaningful content");
    }
}
