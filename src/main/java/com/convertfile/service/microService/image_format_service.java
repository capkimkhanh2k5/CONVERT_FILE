package com.convertfile.service.microService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class image_format_service {
    public void convertImage(String inputPath, String outputPath, String format) throws Exception {
        BufferedImage img = ImageIO.read(new File(inputPath));
        ImageIO.write(img, format, new File(outputPath));
        System.out.println("Image converted to " + format);
    }
}
