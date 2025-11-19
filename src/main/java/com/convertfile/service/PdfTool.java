package com.convertfile.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfTool {

    // Hàm đếm số trang
    public static int getPageCount(String inputPath) {
        try (PDDocument doc = PDDocument.load(new File(inputPath))) {
            return doc.getNumberOfPages();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Hàm Convert PDF sang DOCX (Word)
    public static void convertPdfToDocx(String inputPath, String outputPath) throws IOException {
        File inputFile = new File(inputPath);
        
        // 1. Đọc nội dung từ PDF
        String pdfText = "";
        try (PDDocument document = PDDocument.load(inputFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            pdfText = stripper.getText(document);
        }

        // 2. Tạo file Word mới
        try (XWPFDocument docx = new XWPFDocument()) {
            // Tạo một đoạn văn bản
            XWPFParagraph paragraph = docx.createParagraph();
            XWPFRun run = paragraph.createRun();
            
            // Xử lý xuống dòng (Word không hiểu \n như Text)
            String[] lines = pdfText.split("\n");
            for (String line : lines) {
                run.setText(line);
                run.addBreak(); // Xuống dòng trong Word
            }

            // 3. Lưu file Word ra ổ cứng
            try (FileOutputStream out = new FileOutputStream(new File(outputPath))) {
                docx.write(out);
            }
        }
    }
}