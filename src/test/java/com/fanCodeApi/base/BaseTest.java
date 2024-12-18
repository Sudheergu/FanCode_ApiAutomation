package com.fanCodeApi.base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger; // Use Log4j2 logger instead of SLF4J
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import io.restassured.RestAssured;


public class BaseTest {

    protected static ExtentReports extent;
    protected static ExtentTest test;
    protected static final Logger logger = LogManager.getLogger(BaseTest.class); // Log4j2 Logger

    @BeforeClass
    public void setUp() {
        // Initialize ExtentReports
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter("extent-report.html");
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);

        // Set the base URI for Rest-Assured
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
        logger.info("Base URI set to: " + RestAssured.baseURI); // Use Log4j2 for logging
    }

    @AfterClass
    public void tearDown() {
        // Flush the report at the end of tests
        extent.flush();
    }
}






