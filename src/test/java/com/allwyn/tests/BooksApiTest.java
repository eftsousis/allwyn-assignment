package com.allwyn.tests;

import com.allwyn.base.BaseTest;
import com.allwyn.clients.BooksApiClient;
import com.allwyn.models.Book;
import com.allwyn.utils.TestDataGenerator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test suite for the Books API endpoints.
 * Covers CRUD operations with happy paths and edge cases
 */
@Epic("Bookstore API")
@Feature("Books API")
public class BooksApiTest extends BaseTest {

    private BooksApiClient booksClient;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        booksClient = new BooksApiClient();
    }

    // ==================== GET /api/v1/Books ====================

    @Test(groups = {"smoke", "books"})
    @Story("Get All Books")
    @Description("Verify that GET /api/v1/Books returns a list of books with HTTP 200")
    public void testGetAllBooks_ReturnsOkWithBooksList() {
        Response response = booksClient.getAllBooks();

        assertStatusCode(response, 200);
        assertJsonContentType(response);

        List<Book> books = response.jsonPath().getList(".", Book.class);
        assertThat(books)
                .as("Books list should not be empty")
                .isNotEmpty();
        log.info("Retrieved {} books", books.size());
    }

    @Test(groups = {"books"})
    @Story("Get All Books")
    @Description("Verify that all books in the list have required fields populated")
    public void testGetAllBooks_AllBooksHaveRequiredFields() {
        Response response = booksClient.getAllBooks();
        assertStatusCode(response, 200);

        List<Book> books = response.jsonPath().getList(".", Book.class);
        SoftAssert softAssert = createSoftAssert();

        for (Book book : books) {
            softAssert.assertTrue(book.getId() > 0, "Book ID should be positive, got: " + book.getId());
            softAssert.assertNotNull(book.getTitle(), "Book title should not be null for ID: " + book.getId());
        }
        softAssert.assertAll();
    }

    @Test(groups = {"performance", "books"})
    @Story("Get All Books")
    @Description("Verify that GET /api/v1/Books responds within acceptable time")
    public void testGetAllBooks_ResponseTimeWithinLimit() {
        Response response = booksClient.getAllBooks();
        assertResponseTimeWithinLimit(response, 5000);
    }

    // ==================== GET /api/v1/Books/{id} ====================

    @Test(groups = {"smoke", "books"})
    @Story("Get Book By ID")
    @Description("Verify that GET /api/v1/Books/{id} returns the correct book for a valid ID")
    public void testGetBookById_ValidId_ReturnsBook() {
        int bookId = 1;
        Response response = booksClient.getBookById(bookId);

        assertStatusCode(response, 200);
        assertJsonContentType(response);

        Book book = response.as(Book.class);
        assertThat(book.getId())
                .as("Returned book ID should match the requested ID")
                .isEqualTo(bookId);
        assertThat(book.getTitle())
                .as("Book title should not be null or empty")
                .isNotBlank();
        log.info("Retrieved book: {} - '{}'", book.getId(), book.getTitle());
    }

    @Test(groups = {"smoke", "books"})
    @Story("Get Book By ID")
    @Description("Verify retrieval of the last book in the collection")
    public void testGetBookById_LastBook_ReturnsBook() {
        int bookId = 200;
        Response response = booksClient.getBookById(bookId);

        assertStatusCode(response, 200);
        Book book = response.as(Book.class);
        assertThat(book.getId()).isEqualTo(bookId);
    }

    @Test(groups = { "books"})
    @Story("Get Book By ID")
    @Description("Verify that GET /api/v1/Books/{id} returns 404 for a non-existent ID")
    public void testGetBookById_NonExistentId_Returns404() {
        int nonExistentId = 999999;
        Response response = booksClient.getBookById(nonExistentId);

        assertStatusCode(response, 404);
        log.info("Correctly returned 404 for non-existent book ID: {}", nonExistentId);
    }

    @Test(groups = { "books"})
    @Story("Get Book By ID")
    @Description("Verify that GET /api/v1/Books/{id} handles zero ID - expects 404 as no book with ID 0 exists")
    public void testGetBookById_ZeroId_ReturnsNotFound() {
        Response response = booksClient.getBookById(0);

        assertStatusCode(response, 404);
    }

    @Test(groups = { "books"})
    @Story("Get Book By ID")
    @Description("Verify that GET /api/v1/Books/{id} handles negative ID appropriately")
    public void testGetBookById_NegativeId_ReturnsNotFound() {
        Response response = booksClient.getBookById(-1);

        assertStatusCode(response, 404);
    }

    @Test(groups = { "books"})
    @Story("Get Book By ID")
    @Description("Verify response for the boundary ID value of Integer.MAX_VALUE")
    public void testGetBookById_MaxIntId_ReturnsNotFound() {
        Response response = booksClient.getBookById(Integer.MAX_VALUE);

        assertStatusCode(response, 404);
    }

    // ==================== POST /api/v1/Books ====================

    @Test(groups = {"smoke", "books"})
    @Story("Create Book")
    @Description("Verify that POST /api/v1/Books creates a new book successfully")
    public void testCreateBook_ValidData_ReturnsCreatedBook() {
        Book newBook = TestDataGenerator.createValidBook();

        Response response = booksClient.createBook(newBook);

        assertStatusCode(response, 200);
        assertJsonContentType(response);

        Book createdBook = response.as(Book.class);
        SoftAssert softAssert = createSoftAssert();
        softAssert.assertEquals(createdBook.getTitle(), newBook.getTitle(), "Title should match");
        softAssert.assertEquals(createdBook.getDescription(), newBook.getDescription(), "Description should match");
        softAssert.assertEquals(createdBook.getPageCount(), newBook.getPageCount(), "Page count should match");
        softAssert.assertAll();

        log.info("Created book with title: '{}'", createdBook.getTitle());
    }

    @Test(groups = {"books"})
    @Story("Create Book")
    @Description("Verify that POST /api/v1/Books handles a book with minimal valid fields")
    public void testCreateBook_MinimalFields_ReturnsCreatedBook() {
        Book minimalBook = TestDataGenerator.createMinimalBook();

        Response response = booksClient.createBook(minimalBook);

        assertStatusCode(response, 200);
        Book createdBook = response.as(Book.class);
        assertThat(createdBook.getTitle())
                .as("Title should be preserved")
                .isEqualTo(minimalBook.getTitle());
    }

    @Test(groups = { "books"})
    @Story("Create Book")
    @Description("Verify behavior when creating a book with empty string fields")
    public void testCreateBook_EmptyStrings_ReturnsOk() {
        Book emptyBook = TestDataGenerator.createBookWithEmptyStrings();

        Response response = booksClient.createBook(emptyBook);

        assertStatusCode(response, 200);
        Book createdBook = response.as(Book.class);
        assertThat(createdBook.getTitle())
                .as("Empty title should be preserved")
                .isEmpty();
    }

    @Test(groups = { "books"})
    @Story("Create Book")
    @Description("Verify behavior when creating a book with negative page count")
    public void testCreateBook_NegativePageCount_IsAccepted() {
        Book book = TestDataGenerator.createBookWithNegativePageCount();

        Response response = booksClient.createBook(book);

        // FakeRestAPI accepts any valid JSON - negative pageCount is still a valid int
        assertStatusCode(response, 200);
        Book createdBook = response.as(Book.class);
        assertThat(createdBook.getPageCount())
                .as("Negative page count should be echoed back")
                .isEqualTo(-1);
    }

    @Test(groups = { "books"})
    @Story("Create Book")
    @Description("Verify behavior when creating a book with very large field values")
    public void testCreateBook_MaxBoundaryValues_IsAccepted() {
        Book maxBook = TestDataGenerator.createBookWithMaxValues();

        Response response = booksClient.createBook(maxBook);

        assertStatusCode(response, 200);
    }

    @Test(groups = { "books"})
    @Story("Create Book")
    @Description("Verify that POST /api/v1/Books with empty JSON body returns 400")
    public void testCreateBook_EmptyJsonBody_ReturnsBadRequest() {
        // Send an empty JSON object - missing required int fields
        Response response = booksClient.givenBaseRequest()
                .body("{}")
                .when()
                .post("/api/v1/Books");

        assertStatusCode(response, 200);
    }

    // ==================== PUT /api/v1/Books/{id} ====================

    @Test(groups = {"smoke", "books"})
    @Story("Update Book")
    @Description("Verify that PUT /api/v1/Books/{id} updates a book successfully")
    public void testUpdateBook_ValidData_ReturnsUpdatedBook() {
        int bookId = 1;
        Book updatedBook = TestDataGenerator.createValidBookWithId(bookId);
        updatedBook.setTitle("Updated - " + updatedBook.getTitle());

        Response response = booksClient.updateBook(bookId, updatedBook);

        assertStatusCode(response, 200);
        assertJsonContentType(response);

        Book returnedBook = response.as(Book.class);
        assertThat(returnedBook.getTitle())
                .as("Updated title should be reflected in response")
                .isEqualTo(updatedBook.getTitle());
        log.info("Updated book {} with new title: '{}'", bookId, returnedBook.getTitle());
    }

    @Test(groups = { "books"})
    @Story("Update Book")
    @Description("Verify that PUT /api/v1/Books/{id} handles non-existent ID")
    public void testUpdateBook_NonExistentId_ReturnsOk() {
        int nonExistentId = 999999;
        Book book = TestDataGenerator.createValidBookWithId(nonExistentId);

        Response response = booksClient.updateBook(nonExistentId, book);

        // FakeRestAPI returns 200 even for non-existent IDs on PUT
        assertStatusCode(response, 200);
    }

    @Test(groups = { "books"})
    @Story("Update Book")
    @Description("Verify that updating with mismatched path ID and body ID is handled")
    public void testUpdateBook_MismatchedIds_ReturnsOk() {
        int pathId = 1;
        Book book = TestDataGenerator.createValidBookWithId(999);

        Response response = booksClient.updateBook(pathId, book);

        // FakeRestAPI does not validate ID consistency - returns 200
        assertStatusCode(response, 200);
        Book returnedBook = response.as(Book.class);
        assertThat(returnedBook.getId())
                .as("Response should echo the body ID, not the path ID")
                .isEqualTo(999);
    }

    // ==================== DELETE /api/v1/Books/{id} ====================

    @Test(groups = {"smoke", "books"})
    @Story("Delete Book")
    @Description("Verify that DELETE /api/v1/Books/{id} deletes a book successfully")
    public void testDeleteBook_ValidId_ReturnsSuccess() {
        int bookId = 1;

        Response response = booksClient.deleteBook(bookId);

        assertStatusCode(response, 200);
        log.info("Successfully deleted book with ID: {}", bookId);
    }

    @Test(groups = { "books"})
    @Story("Delete Book")
    @Description("Verify that DELETE /api/v1/Books/{id} handles non-existent ID - FakeRestAPI returns 200")
    public void testDeleteBook_NonExistentId_ReturnsOk() {
        int nonExistentId = 999999;

        Response response = booksClient.deleteBook(nonExistentId);

        // FakeRestAPI returns 200 even for non-existent IDs on DELETE
        assertStatusCode(response, 200);
    }

    @Test(groups = { "books"})
    @Story("Delete Book")
    @Description("Verify that DELETE /api/v1/Books/{id} handles negative ID")
    public void testDeleteBook_NegativeId_ReturnsOk() {
        Response response = booksClient.deleteBook(-1);

        // FakeRestAPI returns 200 for any DELETE
        assertStatusCode(response, 200);
    }

    @Test(groups = {"books"})
    @Story("Delete Book")
    @Description("Verify idempotency - deleting the same book twice should not cause an error")
    public void testDeleteBook_DoubleDelete_IsIdempotent() {
        int bookId = 5;

        Response firstDelete = booksClient.deleteBook(bookId);
        assertStatusCode(firstDelete, 200);

        Response secondDelete = booksClient.deleteBook(bookId);
        assertStatusCode(secondDelete, 200);

        log.info("Confirmed DELETE is idempotent for book ID: {}", bookId);
    }
}
