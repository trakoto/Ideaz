package com.trakoto.Ideaz.controller;

import com.trakoto.Ideaz.entity.Idea;
import com.trakoto.Ideaz.exception.RatingOutOfScaleException;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IdeaControllerTest {

    private Idea createRandomIdea() {
        Idea idea = new Idea();
        idea.setTitle(RandomStringUtils.randomAlphanumeric(10));
        idea.setRating((int) (Math.random()*5 + 1));
        idea.setDescription(RandomStringUtils.randomAlphanumeric(200));
        return idea;
    }

    private Idea globalIdea;

    @Before
    public void setup() {
        baseURI = "http://localhost";
        port = 8080;
        globalIdea = createRandomIdea();
    }

    @Test
    public void anyValidResponseOccurs() {
        get("/ideas").then().assertThat()
                .body("", anything()).statusCode(HttpStatus.OK.value());
    }

    @Test
    public void postIdea_getNewIdea_titlesAreTheSame() {
        Response response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(globalIdea).post("/ideas");

        response.then()
                .assertThat().statusCode(HttpStatus.CREATED.value());

        get("/ideas").then().assertThat()
                .body("title", hasItem(globalIdea.getTitle())).statusCode(HttpStatus.OK.value());

        delete("/ideas/" + response.jsonPath().getString("id"));
    }

    @Test
    public void postIdea_updateValidIdea() {
        Response response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(globalIdea).post("/ideas");

        response.then()
                .assertThat().statusCode(HttpStatus.CREATED.value());

        Idea newIdea = createRandomIdea();
        String id = response.jsonPath().getString("id");
        newIdea.setId(Long.parseLong(id));

        response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(globalIdea).put("/ideas");

        response.then()
                .assertThat().statusCode(HttpStatus.OK.value());

        get("/ideas/" + id).then().assertThat()
                .body("description", equalTo(response.jsonPath().getString("description")))
                .statusCode(HttpStatus.OK.value());

        delete("/ideas/" + response.jsonPath().getString("id"));
    }

    @Test
    public void deleteValidIdea() {
        Response response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(globalIdea).post("/ideas");

        delete("/ideas/" + response.jsonPath().getString("id")).then()
                .assertThat().statusCode(HttpStatus.OK.value());
    }

    @Test
    public void getIdeaByRating() {
        Response response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(globalIdea).post("/ideas");

        get("/ideas/" + response.jsonPath().getString("id"))
                .then().assertThat().body("rating", equalTo(globalIdea.getRating()));

        delete("/ideas/" + response.jsonPath().getString("id")).then()
                .assertThat().statusCode(HttpStatus.OK.value());
    }

    @Test
    public void getIdeaByRatingOutOfRange() {
        get("/ideas/rating/6").then()
                .assertThat().statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        get("/ideas/rating/0").then()
                .assertThat().statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}