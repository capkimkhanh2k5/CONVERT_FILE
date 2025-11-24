package com.convertfile.service;

import com.convertfile.service.ConvertService.image_to_pdf_service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for image_to_pdf_service
 * Tests image to PDF conversion functionality
 */
public class ImageToPdfServiceTest {

    @TempDir
    Path tempDir;

    /**
     * Test basic PNG to PDF conversion
     */
    @Test
    public void testValidPngToPdf() throws Exception {
        // Create test PNG image
        Path pngFile = tempDir.resolve("test.png");
        createTestImage(pngFile, "png", 200, 150, Color.BLUE);
        
        // Convert to PDF
        Path pdfFile = tempDir.resolve("output.pdf");
        image_to_pdf_service service = new image_to_pdf_service();
        service.convertImageToPdf(pngFile.toString(), pdfFile.toString());
        
        // Verify PDF file exists
        assertTrue(Files.exists(pdfFile), "PDF file should be created");
        assertTrue(Files.size(pdfFile) > 0, "PDF file should not be empty");
        
        // Verify PDF is valid
        try (PDDocument doc = PDDocument.load(pdfFile.toFile())) {
            assertEquals(1, doc.getNumberOfPages(), "PDF should have 1 page");
        }
    }

    /**
     * Test JPG to PDF conversion
     */
    @Test
    public void testValidJpgToPdf() throws Exception {
        Path jpgFile = tempDir.resolve("test.jpg");
        createTestImage(jpgFile, "jpg", 300, 200, Color.RED);
        
        Path pdfFile = tempDir.resolve("output.pdf");
        image_to_pdf_service service = new image_to_pdf_service();
        service.convertImageToPdf(jpgFile.toString(), pdfFile.toString());
        
        assertTrue(Files.exists(pdfFile), "PDF file should be created");
        
        // Verify PDF structure
        try (PDDocument doc = PDDocument.load(pdfFile.toFile())) {
            assertEquals(1, doc.getNumberOfPages(), "PDF should have 1 page");
        }
    }

    /**
     * Test large image conversion
     */
    @Test
    public void testLargeImage() throws Exception {
        Path imageFile = tempDir.resolve("large.png");
        createTestImage(imageFile, "png", 2000, 1500, Color.GREEN);
        
        Path pdfFile = tempDir.resolve("large.pdf");
        image_to_pdf_service service = new image_to_pdf_service();
        service.convertImageToPdf(imageFile.toString(), pdfFile.toString());
        
        assertTrue(Files.exists(pdfFile), "PDF file should be created");
        
        try (PDDocument doc = PDDocument.load(pdfFile.toFile())) {
            assertEquals(1, doc.getNumberOfPages(), "PDF should have 1 page");
        }
    }

    /**
     * Test small image conversion
     */
    @Test
    public void testSmallImage() throws Exception {
        Path imageFile = tempDir.resolve("small.png");
        createTestImage(imageFile, "png", 50, 50, Color.YELLOW);
        
        Path pdfFile = tempDir.resolve("small.pdf");
        image_to_pdf_service service = new image_to_pdf_service();
        service.convertImageToPdf(imageFile.toString(), pdfFile.toString());
        
        assertTrue(Files.exists(pdfFile), "PDF file should be created");
        
        try (PDDocument doc = PDDocument.load(pdfFile.toFile())) {
            assertEquals(1, doc.getNumberOfPages(), "PDF should have 1 page");
        }
    }

    /**
     * Test file not found error
     */
    @Test
    public void testFileNotFound() {
        Path imageFile = tempDir.resolve("nonexistent.png");
        Path pdfFile = tempDir.resolve("output.pdf");
        
        image_to_pdf_service service = new image_to_pdf_service();
        
        assertThrows(Exception.class, () -> {
            service.convertImageToPdf(imageFile.toString(), pdfFile.toString());
        }, "Should throw exception for missing file");
    }

    /**
     * Test null/empty path validation
     */
    @Test
    public void testNullPaths() {
        image_to_pdf_service service = new image_to_pdf_service();
        
        assertThrows(IllegalArgumentException.class, () -> {
            service.convertImageToPdf(null, "output.pdf");
        }, "Should throw exception for null image path");
        
        assertThrows(IllegalArgumentException.class, () -> {
            service.convertImageToPdf("image.png", null);
        }, "Should throw exception for null PDF path");
        
        assertThrows(IllegalArgumentException.class, () -> {
            service.convertImageToPdf("", "output.pdf");
        }, "Should throw exception for empty image path");
        
        assertThrows(IllegalArgumentException.class, () -> {
            service.convertImageToPdf("image.png", "");
        }, "Should throw exception for empty PDF path");
    }

    /**
     * Test invalid image file
     */
    @Test
    public void testInvalidImageFile() throws Exception {
        // Create a non-image file
        Path invalidFile = tempDir.resolve("invalid.png");
        Files.writeString(invalidFile, "This is not a valid image file");
        
        Path pdfFile = tempDir.resolve("output.pdf");
        image_to_pdf_service service = new image_to_pdf_service();
        
        assertThrows(Exception.class, () -> {
            service.convertImageToPdf(invalidFile.toString(), pdfFile.toString());
        }, "Should throw exception for invalid image format");
    }

    /**
     * Test BMP to PDF conversion
     */
    @Test
    public void testBmpToPdf() throws Exception {
        Path bmpFile = tempDir.resolve("test.bmp");
        createTestImage(bmpFile, "bmp", 150, 100, Color.MAGENTA);
        
        Path pdfFile = tempDir.resolve("bmp.pdf");
        image_to_pdf_service service = new image_to_pdf_service();
        service.convertImageToPdf(bmpFile.toString(), pdfFile.toString());
        
        assertTrue(Files.exists(pdfFile), "PDF file should be created");
        
        try (PDDocument doc = PDDocument.load(pdfFile.toFile())) {
            assertEquals(1, doc.getNumberOfPages(), "PDF should have 1 page");
        }
    }

    /**
     * Test GIF to PDF conversion
     */
    @Test
    public void testGifToPdf() throws Exception {
        Path gifFile = tempDir.resolve("test.gif");
        createTestImage(gifFile, "gif", 120, 80, Color.CYAN);
        
        Path pdfFile = tempDir.resolve("gif.pdf");
        image_to_pdf_service service = new image_to_pdf_service();
        service.convertImageToPdf(gifFile.toString(), pdfFile.toString());
        
        assertTrue(Files.exists(pdfFile), "PDF file should be created");
        
        try (PDDocument doc = PDDocument.load(pdfFile.toFile())) {
            assertEquals(1, doc.getNumberOfPages(), "PDF should have 1 page");
        }
    }

    /**
     * Test PDF output file validation
     */
    @Test
    public void testPdfOutputValidation() throws Exception {
        Path imageFile = tempDir.resolve("test.png");
        createTestImage(imageFile, "png", 100, 100, Color.WHITE);
        
        Path pdfFile = tempDir.resolve("output.pdf");
        image_to_pdf_service service = new image_to_pdf_service();
        service.convertImageToPdf(imageFile.toString(), pdfFile.toString());
        
        // Verify PDF magic bytes
        byte[] header = Files.readAllBytes(pdfFile);
        assertTrue(header.length >= 4, "PDF should have header");
        assertEquals('%', header[0], "PDF should start with %");
        assertEquals('P', header[1], "PDF header signature");
        assertEquals('D', header[2], "PDF header signature");
        assertEquals('F', header[3], "PDF header signature");
    }

    /**
     * Test transparent PNG to PDF
     */
    @Test
    public void testTransparentPng() throws Exception {
        Path pngFile = tempDir.resolve("transparent.png");
        createTransparentImage(pngFile);
        
        Path pdfFile = tempDir.resolve("transparent.pdf");
        image_to_pdf_service service = new image_to_pdf_service();
        service.convertImageToPdf(pngFile.toString(), pdfFile.toString());
        
        assertTrue(Files.exists(pdfFile), "PDF file should be created");
        
        try (PDDocument doc = PDDocument.load(pdfFile.toFile())) {
            assertEquals(1, doc.getNumberOfPages(), "PDF should have 1 page");
        }
    }

    // ===== Helper Methods =====

    /**
     * Create test image file
     */
    private void createTestImage(Path path, String format, int width, int height, Color color) 
            throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        
        // Fill with color
        g.setColor(color);
        g.fillRect(0, 0, width, height);
        
        // Draw some text
        g.setColor(Color.WHITE);
        g.drawString("Test Image", 10, 20);
        
        g.dispose();
        
        ImageIO.write(image, format, path.toFile());
    }

    /**
     * Create transparent PNG image
     */
    private void createTransparentImage(Path path) throws IOException {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        
        // Create semi-transparent background
        g.setColor(new Color(255, 0, 0, 128)); // 50% transparent red
        g.fillRect(0, 0, 100, 100);
        
        g.dispose();
        
        ImageIO.write(image, "png", path.toFile());
    }
}
