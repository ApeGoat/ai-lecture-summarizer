package com.example.studynotes.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfExtractionServiceTest {

    private final PdfExtractionService service = new PdfExtractionService();

    @TempDir
    Path tempDir;

    @Test
    void shouldThrowWhenFileIsMissing() {
        Path missing = tempDir.resolve("missing.pdf");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.extractText(missing));

        assertTrue(ex.getMessage().contains("does not exist"));
    }

    @Test
    void shouldThrowWhenPdfIsInvalid() throws IOException {
        Path invalid = tempDir.resolve("invalid.pdf");
        Files.writeString(invalid, "not-a-real-pdf");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.extractText(invalid));

        assertTrue(ex.getMessage().contains("valid PDF"));
    }

    @Test
    void shouldThrowWhenExtractedTextIsEmpty() throws IOException {
        Path emptyPdf = tempDir.resolve("empty.pdf");
        try (PDDocument doc = new PDDocument()) {
            doc.addPage(new PDPage());
            doc.save(emptyPdf.toFile());
        }

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.extractText(emptyPdf));

        assertTrue(ex.getMessage().contains("empty"));
    }

    @Test
    void shouldExtractTextFromValidPdf() throws IOException {
        Path pdf = tempDir.resolve("lecture.pdf");
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            try (PDPageContentStream stream = new PDPageContentStream(doc, page)) {
                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA, 12);
                stream.newLineAtOffset(50, 700);
                stream.showText("Key concept: photosynthesis");
                stream.endText();
            }
            doc.save(pdf.toFile());
        }

        String extracted = service.extractText(pdf);

        assertEquals("Key concept: photosynthesis", extracted);
    }
}
