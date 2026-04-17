package com.example.studynotes.service;

import com.example.studynotes.model.StudyNote;
import com.example.studynotes.model.ThemeSection;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileOutputService {

    public Path saveMarkdown(StudyNote studyNote, String inputFileName) {
        try {
            Path outputDir = Path.of("output");
            Files.createDirectories(outputDir);

            String safeName = inputFileName.replaceAll("\\.[Pp][Dd][Ff]$", "").replaceAll("[^a-zA-Z0-9._-]", "_");
            Path outputPath = outputDir.resolve(safeName + "-study-notes.md");

            StringBuilder content = new StringBuilder();
            content.append("# ").append(studyNote.getTitle()).append("\n\n");
            content.append("## Overall Summary\n").append(studyNote.getSummary()).append("\n\n");

            for (ThemeSection theme : studyNote.getThemes()) {
                content.append("## ").append(theme.getTitle()).append("\n");
                content.append(theme.getContent()).append("\n\n");
            }

            content.append("## Final Review\n").append(studyNote.getFinalReview()).append("\n");

            Files.writeString(outputPath, content.toString(), StandardCharsets.UTF_8);
            return outputPath;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save output Markdown file.", e);
        }
    }
}
