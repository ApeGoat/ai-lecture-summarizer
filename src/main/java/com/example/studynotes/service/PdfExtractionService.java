package com.example.studynotes.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class PdfExtractionService {

    public String extractText(Path pdfPath) {
        if (!Files.exists(pdfPath)) {
            throw new IllegalArgumentException("PDF file does not exist: " + pdfPath);
        }

        try (PDDocument document = PDDocument.load(pdfPath.toFile())) {
            String text = new PDFTextStripper().getText(document).trim();
            if (text.isBlank()) {
                throw new IllegalStateException("Extracted text is empty. The PDF may be image-only or blank.");
            }
            return text;
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read PDF. Ensure the file is a valid PDF.", e);
        }
    }
}
