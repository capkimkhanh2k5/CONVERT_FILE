package com.convertfile.service;

import com.convertfile.service.ConvertService.image_format_service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ImageFormatServiceTest {

    @TempDir
    Path tempDir;

    @Test
    public void testConvertPngToJpg() throws Exception {
        image_format_service service = new image_format_service();

        // Create PNG image
        Path pngPath = tempDir.resolve("test.png");
        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, 100, 100);
        g.dispose();
        ImageIO.write(img, "PNG", pngPath.toFile());

        Path jpgPath = tempDir.resolve("test.jpg");

        // Convert PNG to JPG
        service.convertImage(pngPath.toString(), jpgPath.toString(), "jpg");

        // Verify
        assertTrue(Files.exists(jpgPath));
        assertTrue(Files.size(jpgPath) > 0);
    }

    @Test
    public void testConvertJpgToPng() throws Exception {
        image_format_service service = new image_format_service();

        // Create JPG image
        Path jpgPath = tempDir.resolve("test.jpg");
        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, 100, 100);
        g.dispose();
        ImageIO.write(img, "JPG", jpgPath.toFile());

        Path pngPath = tempDir.resolve("test.png");

        // Convert JPG to PNG
        service.convertImage(jpgPath.toString(), pngPath.toString(), "png");

        // Verify
        assertTrue(Files.exists(pngPath));
        assertTrue(Files.size(pngPath) > 0);
    }

    @Test
    public void testConvertPngToGif() throws Exception {
        image_format_service service = new image_format_service();

        // Create PNG image
        Path pngPath = tempDir.resolve("test.png");
        BufferedImage img = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.GREEN);
        g.fillRect(0, 0, 50, 50);
        g.dispose();
        ImageIO.write(img, "PNG", pngPath.toFile());

        Path gifPath = tempDir.resolve("test.gif");

        // Convert PNG to GIF
        service.convertImage(pngPath.toString(), gifPath.toString(), "gif");

        // Verify
        assertTrue(Files.exists(gifPath));
        assertTrue(Files.size(gifPath) > 0);
    }

    @Test
    public void testConvertPngToBmp() throws Exception {
        image_format_service service = new image_format_service();

        // Create PNG image
        Path pngPath = tempDir.resolve("test.png");
        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.YELLOW);
        g.fillRect(0, 0, 100, 100);
        g.dispose();
        ImageIO.write(img, "PNG", pngPath.toFile());

        Path bmpPath = tempDir.resolve("test.bmp");

        // Convert PNG to BMP
        service.convertImage(pngPath.toString(), bmpPath.toString(), "bmp");

        // Verify
        assertTrue(Files.exists(bmpPath));
        assertTrue(Files.size(bmpPath) > 0);
    }

    @Test
    public void testConvertNonExistentImageThrowsException() {
        image_format_service service = new image_format_service();

        Path inputPath = tempDir.resolve("nonexistent.png");
        Path outputPath = tempDir.resolve("output.jpg");

        // Should throw exception
        assertThrows(Exception.class, () -> {
            service.convertImage(inputPath.toString(), outputPath.toString(), "jpg");
        });
    }

    @Test
    public void testConvertImageDifferentSizes() throws Exception {
        image_format_service service = new image_format_service();

        // Create large PNG image
        Path pngPath = tempDir.resolve("large.png");
        BufferedImage img = new BufferedImage(500, 300, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.MAGENTA);
        g.fillRect(0, 0, 500, 300);
        g.dispose();
        ImageIO.write(img, "PNG", pngPath.toFile());

        Path jpgPath = tempDir.resolve("large.jpg");

        // Convert
        service.convertImage(pngPath.toString(), jpgPath.toString(), "jpg");

        // Verify
        assertTrue(Files.exists(jpgPath));
        BufferedImage converted = ImageIO.read(jpgPath.toFile());
        assertEquals(500, converted.getWidth());
        assertEquals(300, converted.getHeight());
    }

    @Test
    public void testConvertMultipleFormats() throws Exception {
        image_format_service service = new image_format_service();

        // Create original PNG
        Path pngPath = tempDir.resolve("original.png");
        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.CYAN);
        g.fillRect(0, 0, 100, 100);
        g.dispose();
        ImageIO.write(img, "PNG", pngPath.toFile());

        // Convert to multiple formats
        Path jpgPath = tempDir.resolve("converted.jpg");
        Path gifPath = tempDir.resolve("converted.gif");
        Path bmpPath = tempDir.resolve("converted.bmp");

        service.convertImage(pngPath.toString(), jpgPath.toString(), "jpg");
        service.convertImage(pngPath.toString(), gifPath.toString(), "gif");
        service.convertImage(pngPath.toString(), bmpPath.toString(), "bmp");

        // Verify all conversions
        assertTrue(Files.exists(jpgPath));
        assertTrue(Files.exists(gifPath));
        assertTrue(Files.exists(bmpPath));
    }
}
