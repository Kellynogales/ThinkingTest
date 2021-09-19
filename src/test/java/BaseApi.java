//import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseApi {
    private static final Logger logger = LogManager.getLogger(ThinkingTest.class);

    @BeforeClass
    public static void setup(){
        logger.info("Start configuration");
        RestAssured.requestSpecification = defaultRequestSpecification();
        logger.info("Successfully configuration");
    }

    public static RequestSpecification defaultRequestSpecification(){

        List<Filter> filters = new ArrayList<>();
        filters.add(new RequestLoggingFilter());
        filters.add(new ResponseLoggingFilter());
//        filters.add(new AllureRestAssured());

        return new RequestSpecBuilder().setBaseUri("https://thinking-tester-contact-list.herokuapp.com")
                .addFilters(filters)
                .setContentType(ContentType.JSON).build();
    }
}
