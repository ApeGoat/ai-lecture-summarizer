package com.example.studynotes.model;

public class ThemeSection {

    private final String title;
    private final String content;

    public ThemeSection(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
