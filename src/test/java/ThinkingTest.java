import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.contact.PostContactRequest;
import model.user.PostUserRequest;
import org.apache.http.HttpStatus;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.*;

public class ThinkingTest extends BaseApi {
    /*POST Add User*/
    @Test
    public void addUserSuccessfullyTest(){

        PostUserRequest userRequest = new PostUserRequest("Luis33", "Villa33", "luis33@gmail.com", "myPassword");

        Response responseUser = genericMethodPostAddUser(userRequest, "users");

        int statusCode = responseUser.getStatusCode();
        String body = responseUser.getBody().asString();

        assertThat(statusCode, equalTo(HttpStatus.SC_CREATED));

        String tokenUser = from(body).get("token");
        assertThat(tokenUser, notNullValue());
    }

    @Test
    public void responseCodeBadRequestInAddUserWhenSendMandatoryFieldsEmptyTest(){

        PostUserRequest userRequest = new PostUserRequest("", "", "", "");

        Response responseUser = genericMethodPostAddUser(userRequest, "users");

        int statusCode = responseUser.getStatusCode();
        String body = responseUser.getBody().asString();

        assertThat(statusCode, equalTo(HttpStatus.SC_BAD_REQUEST));

        String message = from(body).get("message");
        assertThat(message, notNullValue());
        assertThat(message, equalTo("User validation failed: firstName: Path `firstName` is required., lastName: Path `lastName` is required., email: Email is invalid, password: Path `password` is required."));
    }

    @Test
    public void getFirsNameWhenAddUserSuccessfullyTest(){

        PostUserRequest userRequest = new PostUserRequest("Luis34", "Villa34", "luis34@gmail.com", "myPassword");

        Response responseUser = genericMethodPostAddUser(userRequest, "users");

        int statusCode = responseUser.getStatusCode();
        String body = responseUser.getBody().asString();

        assertThat(statusCode, equalTo(HttpStatus.SC_CREATED));

        String token = from(body).get("token");
        String userId = from(body).get("user._id");
        String userFirstName = from(body).get("user.firstName");

        assertThat(token, notNullValue());
        assertThat(userId, notNullValue());
        assertThat(userFirstName, equalTo("Luis34"));
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
                .post("users/login")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("token", notNullValue())
                .extract().jsonPath().getString("token");

        Response responseUser = genericMethodPostLogOutUser(userToken, "users/logout");

        int statusCode = responseUser.getStatusCode();
        assertThat(statusCode, equalTo(HttpStatus.SC_OK));
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
                .post("users/login")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("token", notNullValue())
                .extract().jsonPath().getString("token");
        Response responseUser = genericMethodPostLogOutUser(userToken+"a", "users/logout");

        int statusCode = responseUser.getStatusCode();
        assertThat(statusCode, equalTo(HttpStatus.SC_UNAUTHORIZED));
    }

    @Test
    public void verifyIfReceiveErrorMessageWhenUseTokenEmptyTest(){
        Response responseUser = genericMethodPostLogOutUser("", "users/logout");

        int statusCode = responseUser.getStatusCode();
        String body = responseUser.getBody().asString();
        String errorMessage = from(body).get("error");

        assertThat(statusCode, equalTo(HttpStatus.SC_UNAUTHORIZED));
        assertThat(errorMessage, equalTo("Please authenticate."));
    }

    /*POST Add Contact*/
    @Test
    public void addContactSuccessfullyTest(){
        PostContactRequest contactRequest = new PostContactRequest("Rita", "Vidal", "1990-01-01", "rita@gmail.com", "8005555555",
                "1 Main St.", "Apartment A", "Anytown", "KS", "12345", "USA");

        /*Login User*/
        String userToken = given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"email\": \"luis20@gmail.com\",\n" +
                        "    \"password\": \"myPassword\"\n" +
                        "}")
                .post("users/login")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("token", notNullValue())
                .extract().jsonPath().getString("token");

        String validateResponse =  genericMethodPostAddContact(userToken, contactRequest, "contacts", HttpStatus.SC_CREATED);
        assertThat(validateResponse, notNullValue());
    }

    @Test
    public void verifyResponseOfAddContactWhenSendMandatoryFieldsEmptyTest(){
        PostContactRequest contactRequest = new PostContactRequest("", "", "1990-01-01", "rita@gmail.com", "8005555555",
                "1 Main St.", "Apartment A", "Anytown", "KS", "12345", "USA");

        /*Login User*/
        String userToken = given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"email\": \"luis20@gmail.com\",\n" +
                        "    \"password\": \"myPassword\"\n" +
                        "}")
                .post("users/login")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("token", notNullValue())
                .extract().jsonPath().getString("token");

        String validateResponse = genericMethodPostAddContact(userToken, contactRequest, "contacts", HttpStatus.SC_BAD_REQUEST);
        String message = from(validateResponse).get("message");
        assertThat(message, notNullValue());
        assertThat(message, equalTo("Contact validation failed: firstName: Path `firstName` is required., lastName: Path `lastName` is required."));
    }

    @Test
    public void verifyResponseWhenUseInvalidTokenTest(){
        PostContactRequest contactRequest = new PostContactRequest("Luis", "Dimitri", "1990-01-01", "rita@gmail.com", "8005555555",
                "1 Main St.", "Apartment A", "Anytown", "KS", "12345", "USA");

        /*Login User*/
        String userToken = given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"email\": \"luis20@gmail.com\",\n" +
                        "    \"password\": \"myPassword\"\n" +
                        "}")
                .post("users/login")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("token", notNullValue())
                .extract().jsonPath().getString("token");

        String validateResponse = genericMethodPostAddContact(userToken+"a", contactRequest, "contacts", HttpStatus.SC_UNAUTHORIZED);
        String errorMessage = from(validateResponse).get("error");
        assertThat(errorMessage, notNullValue());
        assertThat(errorMessage, equalTo("Please authenticate."));
    }

    @Test
    public void getResponseWhenAddContactSuccessfullyTest(){
        PostContactRequest contactRequest = new PostContactRequest("Java java", "Du", "1990-01-01", "rita@gmail.com", "8005555555",
                "1 Main St.", "Apartment A", "Anytown", "KS", "12345", "USA");

        /*Login User*/
        String userToken = given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"email\": \"luis20@gmail.com\",\n" +
                        "    \"password\": \"myPassword\"\n" +
                        "}")
                .post("users/login")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("token", notNullValue())
                .extract().jsonPath().getString("token");

        String validateResponse = genericMethodPostAddContact(userToken, contactRequest, "contacts", HttpStatus.SC_CREATED);

        String contactId = from(validateResponse).get("_id");
        assertThat(contactId, notNullValue());
        String contactBirthdate = from(validateResponse).get("birthdate");
        assertThat(contactBirthdate, equalTo("1990-01-01"));
    }

    /*Refactor Method*/
    //AddUser
    private Response genericMethodPostAddUser(PostUserRequest userRequest, String path) {
        Response response = given()
                .contentType(ContentType.JSON)
                .body(userRequest)
                .post(path);
        return response;
    }

    //Log out User
    private Response genericMethodPostLogOutUser(String token, String path) {
        Response response = given()
                .auth()
                .oauth2(token)
                .post(path);
        return response;
    }

    //Contact
    private String genericMethodPostAddContact(String token, PostContactRequest contactRequest, String request, int codeStatus) {
        String response =  given()
                .auth()
                .oauth2(token)
                .contentType(ContentType.JSON)
                .body(contactRequest)
                .post(request)
                .then()
                .assertThat()
                .statusCode(codeStatus)
                .extract().body().asString();
        return response;
    }
}
