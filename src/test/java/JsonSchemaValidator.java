import io.restassured.RestAssured;
import org.testng.annotations.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class JsonSchemaValidator {

    static final String POST_EP= "https://www.someURL.com";

    @Test
    void testPlayground() {
        RestAssured.get(POST_EP)
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("testfile.json"));
    }
}
