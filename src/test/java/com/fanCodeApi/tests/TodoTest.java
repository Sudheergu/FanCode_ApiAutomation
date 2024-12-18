package com.fanCodeApi.tests;

import com.aventstack.extentreports.Status;
import com.fanCodeApi.base.BaseTest;
import com.fanCodeApi.models.Todo;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;
import io.restassured.response.Response;

import java.util.List;

import static io.restassured.RestAssured.given;

public class TodoTest extends BaseTest {

    @Test
    public void testUserCompletedTaskPercentage() {
        // Start the test logging in the report
        test = extent.createTest("testUserCompletedTaskPercentage");

        // Fetch all users
        String logMessage = "Fetching all users from the API.";
        logger.info(logMessage);
        test.log(Status.INFO, logMessage);  // Log to ExtentReports

        Response response = given()
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().response();

        List<Integer> userIds = response.jsonPath().getList("id");
        logMessage = "Fetched " + userIds.size() + " users from the API.";
        logger.info(logMessage);  // Log to Console
        test.log(Status.INFO, logMessage);  // Log to ExtentReports

        for (int userId : userIds) {
            try {
                logMessage = "Fetching todos for user: " + userId;
                logger.info(logMessage);
                test.log(Status.INFO, logMessage);  // Log to ExtentReports

                // Fetch todos for each user
                Response todosResponse = given()
                        .when()
                        .get("/todos?userId=" + userId)
                        .then()
                        .statusCode(200)
                        .contentType(ContentType.JSON)
                        .extract().response();

                List<Todo> todos = todosResponse.jsonPath().getList(".", Todo.class);

                if (todos.isEmpty()) {
                    logMessage = "User " + userId + " has no todos. Skipping.";
                    logger.info(logMessage);
                    test.log(Status.INFO, logMessage);  // Log to ExtentReports
                    continue;
                }

                // Check if the user is from FanCode city by lat/long range
                Response userResponse = given()
                        .when()
                        .get("/users/" + userId)
                        .then()
                        .statusCode(200)
                        .contentType(ContentType.JSON)
                        .extract().response();

                double lat = userResponse.jsonPath().getDouble("address.geo.lat");
                double lon = userResponse.jsonPath().getDouble("address.geo.lng");

                if (lat >= -40 && lat <= 5 && lon >= 5 && lon <= 100) {
                    // Calculate completed tasks percentage
                    long completedCount = todos.stream().filter(Todo::isCompleted).count();
                    double completionPercentage = ((double) completedCount / todos.size()) * 100;

                    logMessage = "User " + userId + " has completed " + completionPercentage + "% of their tasks.";
                    logger.info(logMessage);
                    test.log(Status.INFO, logMessage);  // Log to ExtentReports

                    // Ensure that more than 50% of tasks are completed
                    Assert.assertTrue(completionPercentage > 50, "User " + userId + " has less than 50% tasks completed.");
                } else {
                    logMessage = "User " + userId + " is not from FanCode city. Skipping.";
                    logger.info(logMessage);
                    test.log(Status.INFO, logMessage);  // Log to ExtentReports
                }

            } catch (Exception e) {
                logMessage = "Error occurred for user " + userId + ": " + e.getMessage();
                logger.error(logMessage, e);
                test.log(Status.ERROR, logMessage);  // Log to ExtentReports
                throw new SkipException("Skipping user " + userId + " due to error: " + e.getMessage());
            }
        }
    }
}





