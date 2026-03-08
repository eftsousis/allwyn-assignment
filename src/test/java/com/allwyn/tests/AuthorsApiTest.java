package com.allwyn.tests;

import com.allwyn.base.BaseTest;
import com.allwyn.clients.AuthorsApiClient;
import com.allwyn.models.Author;
import com.allwyn.utils.TestDataGenerator;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test suite for the Authors API endpoints (Bonus Extension).
 * Covers CRUD operations with happy paths and edge cases.
 *
 * Note: FakeRestAPI is a demo API that does not persist data.
 * POST/PUT always return 200 with the submitted data.
 * DELETE always returns 200. GET by non-existent ID returns 404.
 */
@Epic("Bookstore API")
@Feature("Authors API")
public class AuthorsApiTest extends BaseTest {

    private AuthorsApiClient authorsClient;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        authorsClient = new AuthorsApiClient();
    }

    // ==================== GET /api/v1/Authors ====================

    @Test(groups = {"smoke", "authors"})
    @Story("Get All Authors")
    @Description("Verify that GET /api/v1/Authors returns a list of authors with HTTP 200")
    public void testGetAllAuthors_ReturnsOkWithAuthorsList() {
        Response response = authorsClient.getAllAuthors();
        // verify 200 OK in response
        assertStatusCode(response, 200);
        // Verify that response is JSON content
        assertJsonContentType(response);

        List<Author> authors = response.jsonPath().getList(".", Author.class);
        //assertThat(authors.get(0).getFirstName()).isEqualTo("John");
        assertThat(authors)
                .as("Authors list should not be empty")
                .isNotEmpty();
        log.info("Retrieved {} authors", authors.size());
    }

    @Test(groups = {"authors"})
    @Story("Get All Authors")
    @Description("Verify that all authors have required fields populated")
    public void testGetAllAuthors_AllAuthorsHaveRequiredFields() {
        Response response = authorsClient.getAllAuthors();
        assertStatusCode(response, 200);

        List<Author> authors = response.jsonPath().getList(".", Author.class);
        SoftAssert softAssert = createSoftAssert();

        for (Author author : authors) {
            softAssert.assertTrue(author.getId() > 0, "Author ID should be positive, got: " + author.getId());
            softAssert.assertTrue(author.getIdBook() > 0, "Author idBook should be positive for ID: " + author.getId());
        }
        softAssert.assertAll();
    }

    @Test(groups = {"performance", "authors"})
    @Story("Get All Authors")
    @Description("Verify that GET /api/v1/Authors responds within acceptable time")
    public void testGetAllAuthors_ResponseTimeWithinLimit() {
        Response response = authorsClient.getAllAuthors();
        assertResponseTimeWithinLimit(response, 5000);
    }

    // ==================== GET /api/v1/Authors/{id} ====================

    @Test(groups = {"smoke", "authors"})
    @Story("Get Author By ID")
    @Description("Verify that GET /api/v1/Authors/{id} returns the correct author for a valid ID")
    public void testGetAuthorById_ValidId_ReturnsAuthor() {
        int authorId = 1;
        Response response = authorsClient.getAuthorById(authorId);

        assertStatusCode(response, 200);
        assertJsonContentType(response);

        Author author = response.as(Author.class);
        assertThat(author.getId())
                .as("Returned author ID should match the requested ID")
                .isEqualTo(authorId);
        log.info("Retrieved author: {} - {} {}", author.getId(), author.getFirstName(), author.getLastName());
    }

    @Test(groups = { "authors"})
    @Story("Get Author By ID")
    @Description("Verify that GET /api/v1/Authors/{id} returns 404 for a non-existent ID")
    public void testGetAuthorById_NonExistentId_Returns404() {
        int nonExistentId = 999999;
        Response response = authorsClient.getAuthorById(nonExistentId);

        assertStatusCode(response, 404);
    }

    @Test(groups = { "authors"})
    @Story("Get Author By ID")
    @Description("Verify that GET /api/v1/Authors/{id} handles negative ID")
    public void testGetAuthorById_NegativeId_ReturnsNotFound() {
        Response response = authorsClient.getAuthorById(-1);

        assertStatusCode(response, 404);
    }

    // ==================== POST /api/v1/Authors ====================

    @Test(groups = {"smoke", "authors"})
    @Story("Create Author")
    @Description("Verify that POST /api/v1/Authors creates a new author successfully")
    public void testCreateAuthor_ValidData_ReturnsCreatedAuthor() {
        Author newAuthor = TestDataGenerator.createValidAuthor();

        Response response = authorsClient.createAuthor(newAuthor);

        assertStatusCode(response, 200);
        assertJsonContentType(response);

        Author createdAuthor = response.as(Author.class);
        SoftAssert softAssert = createSoftAssert();
        softAssert.assertEquals(createdAuthor.getFirstName(), newAuthor.getFirstName(), "First name should match");
        softAssert.assertEquals(createdAuthor.getLastName(), newAuthor.getLastName(), "Last name should match");
        softAssert.assertEquals(createdAuthor.getIdBook(), newAuthor.getIdBook(), "Book ID should match");
        softAssert.assertAll();

        log.info("Created author: {} {}", createdAuthor.getFirstName(), createdAuthor.getLastName());
    }

    @Test(groups = { "authors"})
    @Story("Create Author")
    @Description("Verify behavior when creating an author with empty name fields")
    public void testCreateAuthor_EmptyFields_ReturnsOk() {
        Author emptyAuthor = TestDataGenerator.createAuthorWithEmptyFields();

        Response response = authorsClient.createAuthor(emptyAuthor);

        assertStatusCode(response, 200);
    }

    @Test(groups = { "authors"})
    @Story("Create Author")
    @Description("Verify that creating an author with special characters in names is handled")
    public void testCreateAuthor_SpecialCharacters_PreservesNames() {
        Author specialAuthor = TestDataGenerator.createAuthorWithSpecialCharacters();

        Response response = authorsClient.createAuthor(specialAuthor);

        assertStatusCode(response, 200);
        Author created = response.as(Author.class);
        assertThat(created.getFirstName())
                .as("Special characters should be preserved")
                .isEqualTo(specialAuthor.getFirstName());
    }

    // ==================== PUT /api/v1/Authors/{id} ====================

    @Test(groups = {"smoke", "authors"})
    @Story("Update Author")
    @Description("Verify that PUT /api/v1/Authors/{id} updates an author successfully")
    public void testUpdateAuthor_ValidData_ReturnsUpdatedAuthor() {
        int authorId = 1;
        Author updatedAuthor = TestDataGenerator.createValidAuthorWithId(authorId);

        Response response = authorsClient.updateAuthor(authorId, updatedAuthor);

        assertStatusCode(response, 200);
        assertJsonContentType(response);

        Author returnedAuthor = response.as(Author.class);
        assertThat(returnedAuthor.getFirstName())
                .as("Updated first name should be reflected")
                .isEqualTo(updatedAuthor.getFirstName());
    }

    @Test(groups = { "authors"})
    @Story("Update Author")
    @Description("Verify that PUT /api/v1/Authors/{id} handles non-existent ID - FakeRestAPI returns 200")
    public void testUpdateAuthor_NonExistentId_ReturnsOk() {
        int nonExistentId = 999999;
        Author author = TestDataGenerator.createValidAuthorWithId(nonExistentId);

        Response response = authorsClient.updateAuthor(nonExistentId, author);

        // FakeRestAPI returns 200 for any PUT
        assertStatusCode(response, 200);
    }

    // ==================== DELETE /api/v1/Authors/{id} ====================

    @Test(groups = {"smoke", "authors"})
    @Story("Delete Author")
    @Description("Verify that DELETE /api/v1/Authors/{id} deletes an author successfully")
    public void testDeleteAuthor_ValidId_ReturnsSuccess() {
        int authorId = 1;

        Response response = authorsClient.deleteAuthor(authorId);

        assertStatusCode(response, 200);
        log.info("Successfully deleted author with ID: {}", authorId);
    }

    @Test(groups = { "authors"})
    @Story("Delete Author")
    @Description("Verify that DELETE /api/v1/Authors/{id} handles non-existent ID - FakeRestAPI returns 200")
    public void testDeleteAuthor_NonExistentId_ReturnsOk() {
        int nonExistentId = 999999;

        Response response = authorsClient.deleteAuthor(nonExistentId);

        // FakeRestAPI returns 200 for any DELETE
        assertStatusCode(response, 200);
    }

    @Test(groups = { "authors"})
    @Story("Delete Author")
    @Description("Verify idempotency - deleting the same author twice")
    public void testDeleteAuthor_DoubleDelete_IsIdempotent() {
        int authorId = 5;

        Response firstDelete = authorsClient.deleteAuthor(authorId);
        assertStatusCode(firstDelete, 200);

        Response secondDelete = authorsClient.deleteAuthor(authorId);
        assertStatusCode(secondDelete, 200);

        log.info("Confirmed DELETE is idempotent for author ID: {}", authorId);
    }
}
