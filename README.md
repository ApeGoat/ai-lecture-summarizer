# AI Lecture Summarizer (Spring Boot CLI)

A minimal Spring Boot + Maven command-line app that reads one lecture PDF, extracts text, calls the OpenAI API, and writes study notes as Markdown.

## Tech Stack
- Java 17
- Spring Boot
- Maven
- Apache PDFBox

## Project Structure
```text
src/main/java/com/example/studynotes
├── cli
│   └── StudyNotesCli.java
├── model
│   ├── StudyNote.java
│   ├── ThemeSection.java
│   └── enums
│       └── OutputFormat.java
└── service
    ├── FileOutputService.java
    ├── OpenAiService.java
    ├── PdfExtractionService.java
    └── StudyNotesService.java
```

## Prerequisites
- Java 17+
- Maven 3.9+
- OpenAI API key

## Configure API key
Option 1 (recommended):
```bash
export OPENAI_API_KEY="your_api_key_here"
```

Option 2: set directly in `src/main/resources/application.properties`.

## Run
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="/path/to/lecture.pdf"
```

Status output includes:
- reading PDF
- extracting text
- generating notes
- saving output
- done

## Output
Generated Markdown is saved under:
```text
output/<input-file-name>-study-notes.md
```

## Run tests (JUnit 5)
```bash
mvn test
```

Unit tests cover:
- PDF extraction scenarios (missing/invalid/empty/valid PDF)
- Markdown file output formatting
- Study note orchestration with mocked OpenAI service


## Error handling
The CLI handles:
- missing file
- invalid PDF
- empty extracted text
- API failures

## Notes
- The OpenAI prompt is designed to preserve source meaning and terminology while improving study readability.
