package com.allwyn.clients;

import com.allwyn.config.ApiConfig;
import com.allwyn.models.Book;
import io.restassured.response.Response;

/**
 * Client for interacting with the Books API endpoints.
 * Encapsulates all HTTP operations for the /api/v1/Books resource.
 */
public class BooksApiClient extends BaseApiClient {

    /**
     * Retrieves all books.
     * GET /api/v1/Books
     */
    public Response getAllBooks() {
        return get(ApiConfig.BOOKS_PATH);
    }

    /**
     * Retrieves specific book by its ID - GET /api/v1/Books/{id}
     * @param bookId
     * @return
     */
    public Response getBookById(int bookId) {
        return getById(ApiConfig.BOOKS_BY_ID_PATH, bookId);
    }

    /**
     * Creates a new book.
     * POST /api/v1/Books
     */
    public Response createBook(Book book) {
        return post(ApiConfig.BOOKS_PATH, book);
    }

    /**
     * Updates an existing book by its ID.
     * PUT /api/v1/Books/{id}
     */
    public Response updateBook(int bookId, Book book) {
        return put(ApiConfig.BOOKS_BY_ID_PATH, bookId, book);
    }

    /**
     * Deletes a book by its ID.
     * DELETE /api/v1/Books/{id}
     */
    public Response deleteBook(int bookId) {
        return delete(ApiConfig.BOOKS_BY_ID_PATH, bookId);
    }
}
