
import io.qameta.allure.Allure;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.path.json.JsonPath;
//import com.jayway.jsonpath.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
//import org.junit.FixMethodOrder;
//import static java.util.concurrent.TimeUinit.MILLISECONDS;

import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class eComerce {

    //Variables
    static private String url_base = "webapi.segundamano.mx";
    static private String token_basic1 = "dGVzdDFfYWdlbnRlQG1haWxpbmF0b3IuY29tOjEyMzQ1Ng==";
    static private String email = "test1_agente@mailinator.com";
    static private String pass = "123456";
    static private String access_token;
    static private String account_id;
    static private String uuid;
    static private String username;
    static private int phoneNumber;
    static private String token2;
    static private String addressID;

    @BeforeAll
    public static void configurarVariables () {
        Allure.addAttachment ("Environment", "QA");
    }

    //@BeforeEach
    private String obtenerToken () {
        RestAssured.baseURI = String.format ("https://%s/nga/api/v1.1/private/accounts?lang=es", url_base);

        Response response = given ().log ().all ()
                .queryParam ("lang", "es")
                .auth ().preemptive ().basic (email, pass)
                .post ();

        String body_response = response.getBody ().asString ();
        System.out.println ("Body Response: " + body_response);

        JsonPath jsonResponse = response.jsonPath ();

        System.out.println ("AccessToken: " + jsonResponse.get ("access_token"));
        access_token = jsonResponse.get ("access_token");
        System.out.println ("Access token: " + access_token);

        System.out.println ("Account id: " + jsonResponse.get ("account.account_id"));
        System.out.println ("uuid: " + jsonResponse.get ("account.uuid"));

        account_id = jsonResponse.get ("account.account_id");
        uuid = jsonResponse.get ("account.uuid");

        String datos = uuid + ":" + access_token;
        String encodedToken2UP = Base64.getEncoder ().encodeToString (datos.getBytes ());

        return encodedToken2UP;
    }

    @Test
    @Order(1)
    @DisplayName("Test case: Vaidar que se desplieguen todas las categoras")
    @Severity(SeverityLevel.CRITICAL)
    public void obtener_categorias () {
        RestAssured.baseURI = String.format ("https://%s/nga/api/v1.1/public/categories/filter", url_base);

        Response response = given ()
                .log ().all ()
                .queryParam ("lang", "es")
                .filter (new AllureRestAssured ())
                .get ();

        String body_response = response.getBody ().asString ();
        String headers_response = response.getHeaders ().toString ();
        long time = response.getTime ();

        System.out.println ("Time: " + time);
        System.out.println ("Body Response: " + body_response);
        System.out.println ("Heades: " + headers_response);

        assertEquals (200, response.getStatusCode ());
        assertNotNull (body_response);
        assertTrue (body_response.contains ("all_categories"));
        assertTrue (time < 2900);

    }


    @Test
    @Order(2)
    @DisplayName("Test case: Vaidar que se desplieguen todas las categoras")
    @Severity(SeverityLevel.BLOCKER)
    public void obtener_Token_usando_header_authorization () {
        RestAssured.baseURI = String.format ("https://%s/nga/api/v1.1/private/accounts", url_base);

        Response response = given ()
                .log ().all ()
                .queryParam ("lang", "es")
                .header ("Authorization", "Basic " + token_basic1)
                .post ();

        String body_response = response.getBody ().asString ();
        String headers_response = response.getHeaders ().toString ();

        System.out.println ("Body Response: " + body_response);
        System.out.println ("Heades: " + headers_response);

        assertEquals (200, response.getStatusCode ());
        assertNotNull (body_response);
        assertTrue (body_response.contains ("access_token"));
        //new
        JsonPath jsonResponse = response.jsonPath ();

        System.out.println ("AccessToken: " + jsonResponse.get ("access_token"));
        access_token = jsonResponse.get ("access_token");
        Allure.addAttachment ("token: ", access_token);

        assertEquals (email, jsonResponse.get ("account.email"));
        assertEquals ("tag:scmcoord.com,2013:api", jsonResponse.get ("token_type"));
        //Validar que el contenido de datos del token
        assertTrue (access_token.matches ("[A-Za-z0-9-_]+"));
        assertTrue (headers_response.contains ("Content-Type"));

        long time = response.getTime ();
        System.out.println ("Time: " + time);
        assertTrue (time <= 1900);

        String time2 = String.format (String.valueOf (response.time ()));
        Allure.addAttachment ("time response: ", time2);


    }

    @Test
    @Order(3)
    @DisplayName("Test case: Obtener token usando correo u contraseña")
    @Severity(SeverityLevel.CRITICAL)
    public void obtener_token_usando_auth_emil_pass () {
        RestAssured.baseURI = String.format ("https://%s/nga/api/v1.1/private/accounts", url_base);

        Response response = given ()
                .log ().all ()
                .queryParam ("lang", "es")
                .auth ().preemptive ().basic (email, pass)
                .post ();

        String body_response = response.getBody ().asString ();
        String body_pretty = response.prettyPrint ();
        String headers_response = response.getHeaders ().toString ();
        //int  time = response.getTime();

        //Allure.addAttachment("Tiempo de respuesta", response.getTime()));
        Allure.addAttachment ("Boddy pretty: ", body_pretty);

        System.out.println ("Body Response: " + body_pretty);
        System.out.println ("Heades: " + headers_response);
        System.out.println ("status response: " + response.getStatusCode ());
        System.out.println ("time response: " + response.getTime ());

        assertEquals (200, response.getStatusCode ());
        // Validar el tiempo de respuesta
        //assert
        assertNotNull (body_response);
        assertTrue (body_response.contains ("access_token"));

        //Obtener valores de variables

        JsonPath jsonResponse = response.jsonPath ();

        System.out.println ("AccessToken: " + jsonResponse.get ("access_token"));
        access_token = jsonResponse.get ("access_token");
        System.out.println ("Access token: " + access_token);

        System.out.println ("Account id: " + jsonResponse.get ("account.account_id"));
        System.out.println ("uuid: " + jsonResponse.get ("account.uuid"));

        account_id = jsonResponse.get ("account.account_id");
        uuid = jsonResponse.get ("account.uuid");
    }

    @Test
    @Order(4)
    @DisplayName("Test case: Editar datos del usuario")
    @Severity(SeverityLevel.CRITICAL)
    public void editar_info_usuario () {

        String body_request = "{\"account\"" +
                ":{\"name\":\"Paloma Cabeza de Vaca\"," +
                "\"phone\":\"5323034487\"," +
                "\"locations\"" +
                ":[{\"code\":\"8\"," +
                "\"key\":\"region\"," +
                "\"label\":\"Colima\"," +
                "\"locations\"" +
                ":[{\"code\":\"110\"," +
                "\"key\":\"municipality\"," +
                "\"label\":\"Tecomán\"}]}]," +
                "\"professional\":false," +
                "\"phone_hidden\":false}}";

        RestAssured.baseURI = String.format ("https://%s/nga/api/v1%s", url_base, account_id);

        Response response = given ()
                .log ().all ()
                .queryParam ("lang", "es")
                .header ("Content-type", "application/json")
                .header ("Authorization", "tag:scmcoord.com,2013:api " + access_token)
                .body (body_request)
                .patch ();

        String body_response = response.getBody ().asString ();

        System.out.println ("Body Response: " + body_response);

        assertEquals (200, response.getStatusCode ());
        assertNotNull (body_response);
        assertTrue (body_response.contains ("account"));
    }

    @Test
    @Order(5)
    @DisplayName("Test case: Crear un usuario nuevo")
    @Severity(SeverityLevel.NORMAL)
    public void crear_usuario_nuevo () {
        username = "agente_ventas" + (Math.floor (Math.random () * 435) + 4321) + "@mailinator.com";
        double passworrd = ((Math.random () * 488) + 54321);

        String datos = username + ":" + passworrd;
        String encode = Base64.getEncoder ().encodeToString (datos.getBytes ());
        String bodyRequest = "{\"account\":{\"email\":\"" + username + "\"}}";

        RestAssured.baseURI = String.format ("https://%s/nga/api/v1.1/private/accounts", url_base);

        Response response = given ().log ().all ()
                .header ("Authorization", "Basic " + encode)
                .queryParam ("lang", "es")
                .contentType ("application/json")
                .body (bodyRequest)
                .post ();

        String body_response = response.getBody ().asString ();

        System.out.println ("Body Response: " + body_response);

        assertEquals (401, response.getStatusCode ());
        assertNotNull (body_response);
        assertTrue (body_response.contains ("{\"error\":{\"code\":\"ACCOUNT_VERIFICATION_REQUIRED\"}}"));

    }

    @Test
    @Order(6)
    @DisplayName("Test case: Editar número de teléfono")
    @Severity(SeverityLevel.MINOR)
    public void update_telefono () {
        phoneNumber = (int) (Math.random () * 999999999 + 987654321);
        String bodyRequest = "{\"account\":{\"name\":\"" + username + "\",\"phone\":\"" + phoneNumber + "\",\"professional\":false}}";

        RestAssured.baseURI = String.format ("https://%s/nga/api/v1%s", url_base, account_id);

        Response response = given ()
                .log ().all ()
                .queryParam ("lang", "es")
                .contentType ("application/json")
                .accept ("application/json, text/plain, */*")
                .header ("Authorization", "tag:scmcoord.com,2013:api " + access_token)
                .body (bodyRequest)
                .patch ();

        String body_response = response.getBody ().asString ();

        System.out.println ("Body Response: " + body_response);

        assertEquals (200, response.getStatusCode ());
        assertNotNull (body_response);
        assertTrue (body_response.contains ("account"));


        //Validar un campo para asegurarse que es el mismo
        JsonPath jsonResponse = response.jsonPath ();

        System.out.println ("Telefono: " + jsonResponse.get ("account.phone"));
        System.out.println ("Telefono request: " + phoneNumber);
        String phoneRespone = jsonResponse.getString ("account.phone");
        assertEquals (phoneRespone, "" + phoneNumber);

    }

    @Test
    @Order(7)
    @DisplayName("Test case: Crear una nueva dirección")
    @Severity(SeverityLevel.BLOCKER)
    public void crear_direccion () {
        // phoneNumber

        String token2UP = uuid + ":" + access_token;
        token2 = Base64.getEncoder ().encodeToString (token2UP.getBytes ());

        RestAssured.baseURI = String.format ("https://%s/addresses/v1/create", url_base);

        Response response = given ()
                .log ().all ()
                .header ("Content-type", "application/x-www-form-urlencoded")
                .header ("Authorization", "Basic " + token2)
                .formParam ("contact", username + " Lopez ")
                .formParam ("phone", phoneNumber)
                .formParam ("rfc", "CAPL800101")
                .formParam ("zipCode", "45999")
                .formParam ("exteriorInfo", "Lopez Mateos 761283")
                .formParam ("region", "11")
                .formParam ("municipality", "292")
                .formParam ("area", "7488")
                .formParam ("alias", "Casa")
                .filter (new AllureRestAssured ())
                .post ();


        String body_response = response.getBody ().asString ();

        System.out.println ("Body Response: " + body_response);

        assertEquals (201, response.getStatusCode ());
        assertNotNull (body_response);
        assertTrue (body_response.contains ("addressID"));


        //Ambas funcionan
        JsonPath jsonResponse = response.jsonPath ();
        //addressID =jsonResponse.get("addressID");
        addressID = response.jsonPath ().get ("addressID");
        System.out.println ("Address: " + addressID);

    }

    @Test
    @Order(8)
    @DisplayName("Test case: Eliminar dirección previamente dada de alta")
    @Severity(SeverityLevel.CRITICAL)
    public void borrar_direccion () {

        RestAssured.baseURI = String.format ("https://%s/addresses/v1/delete/%s", url_base, addressID);

        Response response = given ()
                .log ().all ()
                .auth ().preemptive ().basic (uuid, access_token)
                .delete ();
        String body_response = response.getBody ().asString ();

        System.out.println ("Body Response: " + body_response);

        assertEquals (200, response.getStatusCode ());
        assertNotNull (body_response);
        assertTrue (body_response.contains ("{\"message\":\"" + addressID + " deleted correctly\"}"));

    }

    //Crear un anuncio usando la funcion para tener un token
    @Test
    @Order(9)
    @DisplayName("Test case: Crear un anuncio")
    @Severity(SeverityLevel.CRITICAL)
    public void crear_anuncio () {
        String tokenUp2 = obtenerToken ();
        System.out.println ("Token 2 " + tokenUp2);

        RestAssured.baseURI = String.format ("https://%s/v2/accounts/%s/up", url_base, uuid);

        System.out.printf ("Endpoint: %s", RestAssured.baseURI);

        String body_request = "{\"category\":\"8121\"," +
                "\"subject\":\"Mudanzas y fletes a todo el pais\",\"body\":\"Si estas buscando una mudanza barata, esta es tu opción. Tenemos cobertura en todo el país\",\"region\":\"5\",\"municipality\":\"51\",\"area\":\"140000\",\"price\":\"1\",\"phone_hidden\":\"true\",\"show_phone\":\"false\",\"contact_phone\":\"9977886655\"}";

        Response response = given ()
                .log ().all ()
                .header ("Authorization", "Basic " + tokenUp2)
                .contentType ("application/json")
                .accept ("application/json, text/plain, */*")
                .body (body_request)
                .filter (new AllureRestAssured ())
                .header ("x-source", "PHOENIX_DESKTOP")
                .post ();

        String body_response = response.getBody ().asString ();

        System.out.println ("Body Response: " + body_response);

        assertEquals (200, response.getStatusCode ());
        assertNotNull (body_response);

    }

    @Test
    @Order(10)
    @DisplayName("Test case: Ver los anuncios publicados")
    @Severity(SeverityLevel.NORMAL)
    public void ver_anuncios_pendientes () {
        RestAssured.baseURI = String.format ("https://%s/nga/api/v1%s/klfst?status=pending&lim=20&o=0&query=&lang=es", url_base, account_id);

        Response response = given ()
                .log ().all ()
                .queryParam ("status", "pending")
                .queryParam ("lim", "10")
                .queryParam ("o", "0")
                .queryParam ("query", "")
                .queryParam ("lang", "es")
                .header ("Authorization", "tag:scmcoord.com,2013:api " + access_token)
                .get ();

        String body_response = response.getBody ().asString ();

        System.out.println ("Body Response: " + body_response);

        assertEquals (200, response.getStatusCode ());
        assertNotNull (body_response);

    }

    @Test
    @Order(11)
    @DisplayName("Test case: Ver y editar una dirección")
    @Severity(SeverityLevel.CRITICAL)
    public void direccion () {
        //add a new address to user
        String ToEncode = email + ":" + pass;
        String Basic_encoded = Base64.getEncoder ().encodeToString (ToEncode.getBytes ());

        RestAssured.baseURI = String.format ("https://%s/nga/api/v1.1/private/accounts", url_base);
        Response response = given ().queryParam ("lang", "es")
                .log ().all ()
                .header ("Authorization", "Basic " + Basic_encoded)
                .post ();
        String body = response.getBody ().asString ();
        System.out.println ("Body response= " + body);
        System.out.println ("Status response: " + response.getStatusCode ());
        assertEquals (200, response.getStatusCode ());
        assertTrue (body.contains ("access_token"));
        //consultamos las direcciones
        token_basic1 = response.jsonPath ().getString ("access_token");
        System.out.println ("token: " + token_basic1);
        account_id = response.jsonPath ().getString ("account.account_id");
        System.out.println ("accountID: " + account_id);
        uuid = response.jsonPath ().getString ("account.uuid");
        System.out.println ("uuid: " + uuid);

        String token2Keys = uuid + ":" + token_basic1;
        token2 = Base64.getEncoder ().encodeToString (token2Keys.getBytes ());
        RestAssured.baseURI = String.format ("https://%s/addresses/v1/create", url_base);
        Response response2;
        response2 = given ()
                .log ().all ()
                .header ("Content-type", "application/x-www-form-urlencoded")
                .contentType ("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam ("contact", "Vendedor")
                .formParam ("phone", "5511111111")
                .formParam ("rfc", "VESC9012113E3")
                .formParam ("zipCode", "06720")
                .formParam ("exteriorInfo", "DR Erazo")
                .formParam ("region", "16")
                .formParam ("municipality", "755")
                .formParam ("alias", "CASA MAMA")
                .header ("Authorization", "Basic " + token2)
                .post ();
        //Validaciones
        String body2 = response.getBody ().asString ();

        System.out.println ("Body addres: " + body2);
        System.out.println ("Status expected: 200");
        System.out.println ("Result: " + response.getStatusCode ());
        assertEquals (200, response.getStatusCode ());
        System.out.printf ("Body not null: " + body);
        assertNotNull (body);
    }

    @Test
    @Order(12)
    @DisplayName("Test case: Obtener Balance")
    @Severity(SeverityLevel.NORMAL)
    public void balance() {

        String bodyRequest = "{\"delete_reason\":{\"code\":\"5\"} }";
        RestAssured.baseURI = String.format ("https://%s/tokens/v1/public/balance/detail/%s", url_base, uuid);

        Response response = given ()
                .log ().all ()
                .filter (new AllureRestAssured ())
                .header ("Authorization", "Basic " + token2)
                .get ();

        String body_response = response.getBody ().asString ();
        String headers_response = response.getHeaders ().toString ();
        long time = response.getTime ();

        System.out.println ("Time: " + time);
        System.out.println ("Body Response: " + body_response);
        System.out.println ("Heades: " + headers_response);

        assertEquals (200, response.getStatusCode ());
        assertNotNull (body_response);
        assertTrue (body_response.contains ("token_if_re_free"));
        assertTrue (time < 1900);

    }

    @Test
    @Order(13)
    @DisplayName("Test case: Búsqueda de artículos")
    @Severity(SeverityLevel.NORMAL)
    public void busqueda() {

       RestAssured.baseURI = String.format ("https://%s/shops/api/v1/public/shops?limit=15&search=carros", url_base);

        Response response = given ()
                .log ().all ()
                .queryParam ("limit", "15")
                .queryParam ("search", "carros")
                .filter (new AllureRestAssured ())
                .get ();

        String body_response = response.getBody ().asString ();
        String headers_response = response.getHeaders ().toString ();
        long time = response.getTime ();

        System.out.println ("Time: " + time);
        System.out.println ("Body Response: " + body_response);
        System.out.println ("Heades: " + headers_response);

        assertEquals (200, response.getStatusCode ());
        assertNotNull (body_response);
        assertTrue (time < 1900);
    }

}