package com.example.studynotes.cli;

import com.example.studynotes.model.StudyNote;
import com.example.studynotes.service.FileOutputService;
import com.example.studynotes.service.PdfExtractionService;
import com.example.studynotes.service.StudyNotesService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class StudyNotesCli implements CommandLineRunner {

    private final PdfExtractionService pdfExtractionService;
    private final StudyNotesService studyNotesService;
    private final FileOutputService fileOutputService;

    public StudyNotesCli(PdfExtractionService pdfExtractionService,
                         StudyNotesService studyNotesService,
                         FileOutputService fileOutputService) {
        this.pdfExtractionService = pdfExtractionService;
        this.studyNotesService = studyNotesService;
        this.fileOutputService = fileOutputService;
    }

    @Override
    public void run(String... args) {
        if (args.length == 0) {
            System.err.println("Usage: mvn spring-boot:run -Dspring-boot.run.arguments=\"<path-to-lecture.pdf>\"");
            return;
        }

        Path pdfPath = Path.of(args[0]);

        try {
            System.out.println("[1/5] reading PDF: " + pdfPath);
            System.out.println("[2/5] extracting text...");
            String lectureText = pdfExtractionService.extractText(pdfPath);

            System.out.println("[3/5] generating notes...");
            StudyNote studyNote = studyNotesService.generate(pdfPath.getFileName().toString(), lectureText);

            System.out.println("[4/5] saving output...");
            Path outputPath = fileOutputService.saveMarkdown(studyNote, pdfPath.getFileName().toString());

            System.out.println("[5/5] done: " + outputPath.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed: " + e.getMessage());
        }
    }
}
