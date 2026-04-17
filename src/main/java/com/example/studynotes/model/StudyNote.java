package com.example.studynotes.model;

import com.example.studynotes.model.enums.OutputFormat;

import java.util.List;

public class StudyNote {

    private final String title;
    private final String summary;
    private final List<ThemeSection> themes;
    private final String finalReview;
    private final OutputFormat format;

    public StudyNote(String title, String summary, List<ThemeSection> themes, String finalReview, OutputFormat format) {
        this.title = title;
        this.summary = summary;
        this.themes = themes;
        this.finalReview = finalReview;
        this.format = format;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public List<ThemeSection> getThemes() {
        return themes;
    }

    public String getFinalReview() {
        return finalReview;
    }

    public OutputFormat getFormat() {
        return format;
    }
}
