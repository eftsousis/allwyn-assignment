package com.allwyn.config;

/**
 *  API configuration class
 * @author Efthymios Tsousis eftsousis@yahoo.gr
 */
public final class ApiConfig {

    private ApiConfig() {}

    private static final String DEFAULT_BASE_URL = "https://fakerestapi.azurewebsites.net";
    private static final String BASE_URL_PROPERTY = "base.url";

    private static String overrideUrl = null;

    public static void setBaseUrl(String url) {
        overrideUrl = url;
    }

    public static String getBaseUrl() {
        if (overrideUrl != null && !overrideUrl.isBlank()) {
            return overrideUrl;
        }
        String url = System.getProperty(BASE_URL_PROPERTY);
        if (url != null && !url.isBlank()) {
            return url;
        }
        return DEFAULT_BASE_URL;
    }

    // API Paths
    public static final String BOOKS_PATH = "/api/v1/Books";
    public static final String BOOKS_BY_ID_PATH = "/api/v1/Books/{id}";
    public static final String AUTHORS_PATH = "/api/v1/Authors";
    public static final String AUTHORS_BY_ID_PATH = "/api/v1/Authors/{id}";

    // Timeouts (seconds)
    public static final int CONNECTION_TIMEOUT = 10;
    public static final int RESPONSE_TIMEOUT = 30;
}