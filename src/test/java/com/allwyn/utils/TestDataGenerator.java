package com.allwyn.utils;

import com.allwyn.models.Author;
import com.allwyn.models.Book;
import com.github.javafaker.Faker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for generating test data using JavaFaker.
 */
public final class TestDataGenerator {

    private static final Faker FAKER = new Faker();
    private static final DateTimeFormatter ISO_FORMAT = DateTimeFormatter.ISO_DATE_TIME;

    private TestDataGenerator() {
        // Utility class - prevent instantiation
    }

    // ==================== Book Test Data ====================

    /**
     * Creates a valid Book with all fields populated with realistic random data.
     */
    public static Book createValidBook() {
        return Book.builder()
                .id(FAKER.number().numberBetween(1000, 99999))
                .title(FAKER.book().title())
                .description(FAKER.lorem().paragraph())
                .pageCount(FAKER.number().numberBetween(50, 1500))
                .excerpt(FAKER.lorem().sentence(20))
                .publishDate(LocalDateTime.now().minusDays(FAKER.number().numberBetween(1, 3650))
                        .format(ISO_FORMAT))
                .build();
    }

    /**
     * Creates a valid Book with a specific ID.
     */
    public static Book createValidBookWithId(int id) {
        Book book = createValidBook();
        book.setId(id);
        return book;
    }

    /**
     * Creates a Book with minimal but valid field values.
     */
    public static Book createMinimalBook() {
        return Book.builder()
                .id(FAKER.number().numberBetween(1000, 99999))
                .title(FAKER.book().title())
                .description("")
                .pageCount(0)
                .excerpt("")
                .publishDate(LocalDateTime.now().format(ISO_FORMAT))
                .build();
    }

    /**
     * Creates a Book with empty string fields but valid numeric/date types.
     */
    public static Book createBookWithEmptyStrings() {
        return Book.builder()
                .id(FAKER.number().numberBetween(1000, 99999))
                .title("")
                .description("")
                .pageCount(0)
                .excerpt("")
                .publishDate(LocalDateTime.now().format(ISO_FORMAT))
                .build();
    }

    /**
     * Creates a Book with maximum boundary values.
     */
    public static Book createBookWithMaxValues() {
        return Book.builder()
                .id(Integer.MAX_VALUE)
                .title(FAKER.lorem().characters(500))
                .description(FAKER.lorem().characters(2000))
                .pageCount(Integer.MAX_VALUE)
                .excerpt(FAKER.lorem().characters(1000))
                .publishDate(LocalDateTime.now().format(ISO_FORMAT))
                .build();
    }

    /**
     * Creates a Book with negative page count (edge case).
     */
    public static Book createBookWithNegativePageCount() {
        Book book = createValidBook();
        book.setPageCount(-1);
        return book;
    }

    /**
     * Creates a Book with zero ID.
     */
    public static Book createBookWithZeroId() {
        Book book = createValidBook();
        book.setId(0);
        return book;
    }

    /**
     * Creates a Book with a very long title to test field length limits.
     */
    public static Book createBookWithLongTitle() {
        Book book = createValidBook();
        book.setTitle(FAKER.lorem().characters(5000));
        return book;
    }

    // ==================== Author Test Data ====================

    /**
     * Creates a valid Author with all fields populated with realistic random data.
     */
    public static Author createValidAuthor() {
        return Author.builder()
                .id(FAKER.number().numberBetween(1000, 99999))
                .idBook(FAKER.number().numberBetween(1, 200))
                .firstName(FAKER.name().firstName())
                .lastName(FAKER.name().lastName())
                .build();
    }

    /**
     * Creates a valid Author with a specific ID.
     */
    public static Author createValidAuthorWithId(int id) {
        Author author = createValidAuthor();
        author.setId(id);
        return author;
    }


    /**
     * Creates an Author with empty name fields.
     */
    public static Author createAuthorWithEmptyFields() {
        return Author.builder()
                .id(FAKER.number().numberBetween(1000, 99999))
                .idBook(1)
                .firstName("")
                .lastName("")
                .build();
    }

    /**
     * Creates an Author with special characters in names.
     */
    public static Author createAuthorWithSpecialCharacters() {
        return Author.builder()
                .id(FAKER.number().numberBetween(1000, 99999))
                .idBook(1)
                .firstName("O'Ξον-τσους\u00e9")
                .lastName("M\u00fcller-\u00d8stergaard")
                .build();
    }
}
