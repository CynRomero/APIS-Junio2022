import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ejemplo_test_api {
    @Test
    public void api_covid_test (){
        ///https://api.covidtracking.com/v2/us/daily.json
        RestAssured.baseURI= String.format("https://reqres.in/api/users?page=2");
        Response response=given()
                .log().all()
                .headers("Accept","application/json").get();

        String body_response =response.getBody ().prettyPrint ();

        assertEquals(200,response.getStatusCode ());
    }
}
