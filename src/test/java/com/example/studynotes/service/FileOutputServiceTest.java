package com.example.studynotes.service;

import com.example.studynotes.model.StudyNote;
import com.example.studynotes.model.ThemeSection;
import com.example.studynotes.model.enums.OutputFormat;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FileOutputServiceTest {

    private final FileOutputService fileOutputService = new FileOutputService();

    @Test
    void shouldWriteMarkdownFileWithExpectedSections() throws IOException {
        StudyNote note = new StudyNote(
                "Biology 101",
                "Short summary",
                List.of(new ThemeSection("Theme A", "- Point 1\n- Point 2")),
                "Final review text",
                OutputFormat.MARKDOWN
        );

        Path output = fileOutputService.saveMarkdown(note, "lecture.pdf");
        assertTrue(Files.exists(output));

        String content = Files.readString(output);
        assertTrue(content.contains("# Biology 101"));
        assertTrue(content.contains("## Overall Summary"));
        assertTrue(content.contains("## Theme A"));
        assertTrue(content.contains("## Final Review"));

        Files.deleteIfExists(output);
    }
}
