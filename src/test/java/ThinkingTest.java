import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class ThinkingTest {
    @BeforeClass
    public void setup(){
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }
    /*POST Add User*/
    @Test
    public void addUserSuccessfullyTest(){
        given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "    \"firstName\": \"Luis20\",\n" +
                        "    \"lastName\": \"Villa20\",\n" +
                        "    \"email\": \"luis20@gmail.com\",\n" +
                        "    \"password\": \"myPassword\"\n" +
                        "}")
                .post("https://thinking-tester-contact-list.herokuapp.com/users")
                .then()
                .statusCode(HttpStatus.SC_CREATED);

    }
    @Test
    public void responseCodeBadRequestWhenAddInvalidUserTest(){
        given()
                .contentType(ContentType.JSON)
                .log().all()
                .body("{\n" +
                        "    \"firstName\": \"\",\n" +
                        "    \"lastName\": \"\",\n" +
                        "    \"email\": \"\",\n" +
                        "    \"password\": \"\"\n" +
                        "}")
                .post("https://thinking-tester-contact-list.herokuapp.com/users")
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", notNullValue());

    }

    @Test
    public void getResponseWhenAddUserSuccessfullyTest(){
        String userFirstName = given()
                .contentType(ContentType.JSON)
                .log().all()
                .body("{\n" +
                        "    \"firstName\": \"Luis21\",\n" +
                        "    \"lastName\": \"Villa21\",\n" +
                        "    \"email\": \"luis21@gmail.com\",\n" +
                        "    \"password\": \"myPassword\"\n" +
                        "}")
                .post("https://thinking-tester-contact-list.herokuapp.com/users")
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_CREATED)
                .body("token", notNullValue())
                .body("user._id", notNullValue())
                .extract().jsonPath().getString("firstName");

        assertThat(userFirstName, equalTo("Luis21"));
    }
    /*POST Log User*/
    @Test
    public void verifyStatusCodeWhenUseValidTokenTest(){

        /*Login User*/
        String userToken = given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"email\": \"luis20@gmail.com\",\n" +
                        "    \"password\": \"myPassword\"\n" +
                        "}")
                .post("https://thinking-tester-contact-list.herokuapp.com/users/login")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("token", notNullValue())
                .extract().jsonPath().getString("token");

        given()
                .auth()
                .oauth2(userToken)
                .log().all()
                .post("https://thinking-tester-contact-list.herokuapp.com/users/logout")
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void verifyStatusCodeWhenUseInvalidTokenTest(){

        /*Login User*/
        String userToken = given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"email\": \"luis20@gmail.com\",\n" +
                        "    \"password\": \"myPassword\"\n" +
                        "}")
                .post("https://thinking-tester-contact-list.herokuapp.com/users/login")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("token", notNullValue())
                .extract().jsonPath().getString("token");

        given()
                .auth()
                .oauth2(userToken+"a")
                .log().all()
                .post("https://thinking-tester-contact-list.herokuapp.com/users/logout")
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void verifyIfReceiveErrorMessageWhenUseTokenEmptyTest(){
        String errorMessage = given()
                .auth()
                .oauth2("")
                .log().all()
                .post("https://thinking-tester-contact-list.herokuapp.com/users/logout")
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("error", notNullValue())
                .extract().jsonPath().getString("error");

        assertThat(errorMessage, equalTo("Please authenticate."));
    }

    /*POST Add Contact*/
    @Test
    public void addContactSuccessfullyTest(){
        /*Login User*/
        String userToken = given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"email\": \"luis20@gmail.com\",\n" +
                        "    \"password\": \"myPassword\"\n" +
                        "}")
                .post("https://thinking-tester-contact-list.herokuapp.com/users/login")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("token", notNullValue())
                .extract().jsonPath().getString("token");

        given()
                .auth()
                .oauth2(userToken)
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "    \"firstName\": \"Jhohny\",\n" +
                        "    \"lastName\": \"Catch\",\n" +
                        "    \"birthdate\": \"1970-01-01\",\n" +
                        "    \"email\": \"jdoe@mortalKombat.com\",\n" +
                        "    \"phone\": \"8005555555\",\n" +
                        "    \"street1\": \"1 Main St.\",\n" +
                        "    \"street2\": \"Apartment A\",\n" +
                        "    \"city\": \"Anytown\",\n" +
                        "    \"stateProvince\": \"KS\",\n" +
                        "    \"postalCode\": \"12345\",\n" +
                        "    \"country\": \"USA\"\n" +
                        "}")
                .log().all()
                .post("https://thinking-tester-contact-list.herokuapp.com/contacts")
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_CREATED);

    }

    @Test
    public void verifyResponseOfAddContactWhenSendMandatoryFieldsEmptyTest(){
        /*Login User*/
        String userToken = given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"email\": \"luis20@gmail.com\",\n" +
                        "    \"password\": \"myPassword\"\n" +
                        "}")
                .post("https://thinking-tester-contact-list.herokuapp.com/users/login")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("token", notNullValue())
                .extract().jsonPath().getString("token");

        given()
                .auth()
                .oauth2(userToken)
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "    \"firstName\": \"\",\n" +
                        "    \"lastName\": \"\",\n" +
                        "    \"birthdate\": \"1970-01-01\",\n" +
                        "    \"email\": \"jdoe@mortalKombat.com\",\n" +
                        "    \"phone\": \"8005555555\",\n" +
                        "    \"street1\": \"1 Main St.\",\n" +
                        "    \"street2\": \"Apartment A\",\n" +
                        "    \"city\": \"Anytown\",\n" +
                        "    \"stateProvince\": \"KS\",\n" +
                        "    \"postalCode\": \"12345\",\n" +
                        "    \"country\": \"USA\"\n" +
                        "}")
                .log().all()
                .post("https://thinking-tester-contact-list.herokuapp.com/contacts")
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_BAD_REQUEST);

    }

    @Test
    public void verifyResponseWhenUseInvalidTokenTest(){
        /*Login User*/
        String userToken = given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"email\": \"luis20@gmail.com\",\n" +
                        "    \"password\": \"myPassword\"\n" +
                        "}")
                .post("https://thinking-tester-contact-list.herokuapp.com/users/login")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("token", notNullValue())
                .extract().jsonPath().getString("token");

        given()
                .auth()
                .oauth2(userToken+"a")
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "    \"firstName\": \"Java\",\n" +
                        "    \"lastName\": \"Du\",\n" +
                        "    \"birthdate\": \"1970-01-01\",\n" +
                        "    \"email\": \"jdoe@mortalKombat.com\",\n" +
                        "    \"phone\": \"8005555555\",\n" +
                        "    \"street1\": \"1 Main St.\",\n" +
                        "    \"street2\": \"Apartment A\",\n" +
                        "    \"city\": \"Anytown\",\n" +
                        "    \"stateProvince\": \"KS\",\n" +
                        "    \"postalCode\": \"12345\",\n" +
                        "    \"country\": \"USA\"\n" +
                        "}")
                .log().all()
                .post("https://thinking-tester-contact-list.herokuapp.com/contacts")
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void getResponseWhenAddContactSuccessfullyTest(){
        /*Login User*/
        String userToken = given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"email\": \"luis20@gmail.com\",\n" +
                        "    \"password\": \"myPassword\"\n" +
                        "}")
                .post("https://thinking-tester-contact-list.herokuapp.com/users/login")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("token", notNullValue())
                .extract().jsonPath().getString("token");

        String contactBirthdate = given()
                .auth()
                .oauth2(userToken)
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "    \"firstName\": \"Java\",\n" +
                        "    \"lastName\": \"Du\",\n" +
                        "    \"birthdate\": \"1970-01-01\",\n" +
                        "    \"email\": \"jdoe@mortalKombat.com\",\n" +
                        "    \"phone\": \"8005555555\",\n" +
                        "    \"street1\": \"1 Main St.\",\n" +
                        "    \"street2\": \"Apartment A\",\n" +
                        "    \"city\": \"Anytown\",\n" +
                        "    \"stateProvince\": \"KS\",\n" +
                        "    \"postalCode\": \"12345\",\n" +
                        "    \"country\": \"USA\"\n" +
                        "}")
                .log().all()
                .post("https://thinking-tester-contact-list.herokuapp.com/contacts")
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_CREATED)
                .body("_id", notNullValue())
                .extract().jsonPath().getString("birthdate");

        assertThat(contactBirthdate, equalTo("1970-01-01"));
    }
}
