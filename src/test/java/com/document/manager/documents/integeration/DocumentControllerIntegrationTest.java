package com.document.manager.documents.integeration;

import com.document.manager.utils.TestUtils;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.HashMap;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:postgresql://localhost:5432/test",
        "spring.datasource.username=Admin",
        "spring.datasource.password=adminPassword"
})
public class DocumentControllerIntegrationTest {

    @LocalServerPort
    private int port;
    private static final String TOKEN_USER_NAME = "Admin1";
    private static final String TOKEN_USER_PASSWORD = "adminPassword";

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    ).withInitScript("schema.sql")
            .waitingFor(Wait.forListeningPort());

    ;


    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }


    private String getTokenForUser(String username, String password) {
        var authRequest = new HashMap<>();
        authRequest.put("userName", TOKEN_USER_NAME);
        authRequest.put("password", TOKEN_USER_PASSWORD);

        Response response = given()
                .contentType("application/json")
                .body(authRequest)
                .when()
                .post("/tokens")
                .then()
                .statusCode(200)
                .extract()
                .response();

        return response.jsonPath().get("response");
    }

    @Test
    public void testCreateDocumentSuccess() {

        var token = getTokenForUser(TOKEN_USER_NAME, TOKEN_USER_PASSWORD);
        var createRequest = TestUtils.getCreateDocumentRequestDto();

        Response response = given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body(createRequest)
                .when()
                .post("/documents");

        response.prettyPrint();

        response.then()
                .statusCode(201)
                .body("title", equalTo("Title1"));


        assertEquals("Title1", response.jsonPath().getString("title"));
    }


    @Test
    public void testCreateDocumentFailure_InvalidToken() {
        var createRequest = new HashMap<String, Object>();
        createRequest.put("title", "Title2");
        createRequest.put("body", "Body2");
        createRequest.put("authorUsername", "u0007");
        createRequest.put("references", List.of("ref2"));

        given()
                .auth().oauth2("invalid_token")
                .contentType("application/json")
                .body(createRequest)
                .when()
                .post("/documents")
                .then()
                .statusCode(401);
    }

    @Test
    public void testGetAllDocumentsSuccess() {
        var token = getTokenForUser(TOKEN_USER_NAME, TOKEN_USER_PASSWORD);

        given()
                .auth().oauth2(token)
                .when()
                .get("/documents?page=0&size=10")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetDocumentByIdSuccess() {
        var token = getTokenForUser(TOKEN_USER_NAME, TOKEN_USER_PASSWORD);

        var documentId = 1L; // Assume this ID exists
        given()
                .auth().oauth2(token)
                .when()
                .get("/documents/{id}", documentId)
                .then()
                .statusCode(200)
                .body("title", not(emptyOrNullString()));
    }

    @Test
    public void testGetDocumentByIdFailure_NotFound() {
        var token = getTokenForUser(TOKEN_USER_NAME, TOKEN_USER_PASSWORD);

        var invalidId = 999L; // Assume this ID does not exist
        given()
                .auth().oauth2(token)
                .when()
                .get("/documents/{id}", invalidId)
                .then()
                .statusCode(404);
    }

    @Test
    public void testUpdateDocumentSuccess() {
        var token = getTokenForUser(TOKEN_USER_NAME, TOKEN_USER_PASSWORD);

        var updateRequest = new HashMap<String, Object>();
        updateRequest.put("title", "Updated Title");
        updateRequest.put("body", "Updated Body");
        updateRequest.put("authorUsername", "authorUserName");
        updateRequest.put("references", List.of("ref2"));

        var documentId = 1L; // Assume this ID exists
        given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body(updateRequest)
                .when()
                .put("/documents/{id}", documentId)
                .then()
                .statusCode(200)
                .body("title", equalTo("Updated Title"));
    }

    @Test
    public void testUpdateDocumentFailure_DocumentNotFound() {
        var token = getTokenForUser(TOKEN_USER_NAME, TOKEN_USER_PASSWORD);

        var updateRequest = new HashMap<>();
        updateRequest.put("title", "Updated Title");
        updateRequest.put("body", "Updated Body");
        updateRequest.put("authorUsername", "authorUserName");
        updateRequest.put("references", List.of("ref2"));

        Long invalidId = 999L; // Assume this ID does not exist
        given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body(updateRequest)
                .when()
                .put("/documents/{id}", invalidId)
                .then()
                .statusCode(204); // Idempotent operation
    }

    @Test
    public void testDeleteDocumentSuccess() {
        var token = getTokenForUser(TOKEN_USER_NAME, TOKEN_USER_PASSWORD);

        var documentId = 1L; // Assume this ID exists
        given()
                .auth().oauth2(token)
                .when()
                .delete("/documents/{id}", documentId)
                .then()
                .statusCode(204);
    }

    @Test
    public void testDeleteDocumentFailure_NotFound() {
        var token = getTokenForUser(TOKEN_USER_NAME, TOKEN_USER_PASSWORD);

        var invalidId = 999L; // Assume this ID does not exist
        given()
                .auth().oauth2(token)
                .when()
                .delete("/documents/{id}", invalidId)
                .then()
                .statusCode(204); // Idempotent operation
    }

    @Test
    public void testGetAllDocumentsFailure_Unauthorized() {
        given()
                .when()
                .get("/documents?page=0&size=10")
                .then()
                .statusCode(401);
    }
}
