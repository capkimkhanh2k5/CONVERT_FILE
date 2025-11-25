package com.convertfile.service.ConvertService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.OutputStream;

import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFonts;

/**
 * Service for converting DOCX files to PDF format
 * Tested and verified with unit tests in convertfile-tests module
 */
public class docx_to_pdf_service {
    
    // Static initialization - discover fonts only once (huge performance boost)
    private static volatile boolean fontsDiscovered = false;
    private static final Object fontLock = new Object();
    
    static {
        // Reduce docx4j logging noise
        System.setProperty("org.docx4j.fonts.fop.fonts.autodetect.FontInfoFinder", "ERROR");
    }
    
    private static void ensureFontsDiscovered() {
        if (!fontsDiscovered) {
            synchronized (fontLock) {
                if (!fontsDiscovered) {
                    try {
                        System.out.println("🔤 Discovering system fonts (one-time operation)...");
                        long start = System.currentTimeMillis();
                        PhysicalFonts.discoverPhysicalFonts();
                        long elapsed = System.currentTimeMillis() - start;
                        System.out.println("✅ Fonts discovered in " + elapsed + "ms");
                        fontsDiscovered = true;
                    } catch (Exception e) {
                        System.err.println("⚠️ Font discovery failed, using defaults: " + e.getMessage());
                        fontsDiscovered = true; // Continue anyway
                    }
                }
            }
        }
    }
    
    /**
     * Convert DOCX file to PDF
     * Handles empty documents by adding placeholder content
     * 
     * @param docxPath Path to input DOCX file
     * @param pdfPath Path to output PDF file
     * @throws IOException If conversion fails
     */
    public void convertDocxtoPdf(String docxPath, String pdfPath) throws IOException {
        System.out.println("🔄 Converting DOCX to PDF: " + new File(docxPath).getName());
        try (OutputStream output = new FileOutputStream(new File(pdfPath))) {

            // Ensure fonts are discovered (cached after first call)
            ensureFontsDiscovered();
            
            WordprocessingMLPackage wordPackage = WordprocessingMLPackage.load(new File(docxPath));
            
            // Check if document is empty and add minimal content
            // This prevents conversion errors with empty DOCX files
            if (wordPackage.getMainDocumentPart().getContent().isEmpty()) {
                System.out.println("⚠️ Empty document detected - adding placeholder");
                org.docx4j.wml.ObjectFactory factory = new org.docx4j.wml.ObjectFactory();
                org.docx4j.wml.P paragraph = factory.createP();
                org.docx4j.wml.R run = factory.createR();
                org.docx4j.wml.Text text = factory.createText();
                text.setValue(" "); // Single space as placeholder
                run.getContent().add(text);
                paragraph.getContent().add(run);
                wordPackage.getMainDocumentPart().getContent().add(paragraph);
            }
            
            // Font mapper (fonts already discovered)
            Mapper font = new IdentityPlusMapper();
            wordPackage.setFontMapper(font);

            System.out.println("📝 Rendering PDF...");
            Docx4J.toPDF(wordPackage, output);
            System.out.println("✅ PDF conversion completed");
            
        } catch(Docx4JException edocx) {
            System.err.println("❌ Docx4J error: " + edocx.getMessage());
            throw new IOException("DOCX to PDF conversion failed: " + edocx.getMessage());
        } catch (Exception ex) {
            System.err.println("❌ Conversion error: " + ex.getMessage());
            ex.printStackTrace();
            throw new IOException("PDF conversion error: " + ex.getMessage());
        }
    }
}
