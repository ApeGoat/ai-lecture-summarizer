package com.example.studynotes.service;

import com.example.studynotes.model.StudyNote;
import com.example.studynotes.model.enums.OutputFormat;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StudyNotesServiceTest {

    @Test
    void shouldCreateStudyNoteFromAiMarkdown() {
        OpenAiService openAiService = mock(OpenAiService.class);
        when(openAiService.generateStudyNotesMarkdown("lecture text"))
                .thenReturn("## Theme\n- bullet");

        StudyNotesService service = new StudyNotesService(openAiService);
        StudyNote result = service.generate("lecture.pdf", "lecture text");

        assertEquals("lecture.pdf - Study Notes", result.getTitle());
        assertEquals(OutputFormat.MARKDOWN, result.getFormat());
        assertEquals(1, result.getThemes().size());
        assertEquals("## Theme\n- bullet", result.getThemes().get(0).getContent());
    }
}
