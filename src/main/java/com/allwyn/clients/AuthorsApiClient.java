package com.allwyn.clients;

import com.allwyn.config.ApiConfig;
import com.allwyn.models.Author;
import io.restassured.response.Response;

/**
 * Client for operations with the Authors API endpoints -  /api/v1/Authors
 *
 */
public class AuthorsApiClient extends BaseApiClient {

    /**
     * Retrieves all authors.
     * GET /api/v1/Authors
     */
    public Response getAllAuthors() {
        return get(ApiConfig.AUTHORS_PATH);
    }

    /**
     * Retrieves a specific author by their ID.
     * GET /api/v1/Authors/{id}
     */
    public Response getAuthorById(int authorId) {
        return getById(ApiConfig.AUTHORS_BY_ID_PATH, authorId);
    }

    /**
     * Creates a new author.
     * POST /api/v1/Authors
     */
    public Response createAuthor(Author author) {
        return post(ApiConfig.AUTHORS_PATH, author);
    }

    /**
     * Updates an existing author by their ID.
     * PUT /api/v1/Authors/{id}
     */
    public Response updateAuthor(int authorId, Author author) {
        return put(ApiConfig.AUTHORS_BY_ID_PATH, authorId, author);
    }

    /**
     * Deletes an author by their ID.
     * DELETE /api/v1/Authors/{id}
     */
    public Response deleteAuthor(int authorId) {
        return delete(ApiConfig.AUTHORS_BY_ID_PATH, authorId);
    }
}
