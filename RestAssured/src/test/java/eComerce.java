
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
    static private String ad_id;

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
    @DisplayName("Test case 1: Obtener las categoras.")
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
        System.out.println ("Header: " + headers_response);

        assertEquals (200, response.getStatusCode ());
        assertNotNull (body_response);
        assertTrue (body_response.contains ("all_categories"));
        assertTrue (time < 2900);

    }


    @Test
    @Order(2)
    @DisplayName("Test case 2: Ver URLs de las categorías.")
    @Severity(SeverityLevel.BLOCKER)
    public void obtener_Token_usando_header_authorization2 () {
        RestAssured.baseURI = String.format ("https://%s/urls/v1/public/ad-listing", url_base);

        String body_request = "{\"filters\":[{\"category\":\"1000\"},{\"category\":\"1020\"},{\"category\":\"1040\"},{\"category\":\"1060\"},{\"category\":\"1080\"},{\"category\":\"2000\"},{\"category\":\"2020\"},{\"category\":\"2040\"},{\"category\":\"2120\"},{\"category\":\"2080\"},{\"category\":\"2060\"},{\"category\":\"5000\"},{\"category\":\"5040\"},{\"category\":\"5080\"},{\"category\":\"5020\"},{\"category\":\"5060\"},{\"category\":\"3000\"},{\"category\":\"3040\"},{\"category\":\"3020\"},{\"category\":\"3060\"},{\"category\":\"3100\"},{\"category\":\"3080\"},{\"category\":\"3120\"},{\"category\":\"6000\"},{\"category\":\"6020\"},{\"category\":\"6040\"},{\"category\":\"6060\"},{\"category\":\"4000\"},{\"category\":\"4020\"},{\"category\":\"4040\"},{\"category\":\"4060\"},{\"category\":\"4100\"},{\"category\":\"4080\"},{\"category\":\"4120\"},{\"category\":\"4140\"},{\"category\":\"8000\"},{\"category\":\"8020\"},{\"category\":\"8040\"},{\"category\":\"8060\"},{\"category\":\"8080\"},{\"category\":\"8100\"},{\"category\":\"8120\"},{\"category\":\"8140\"},{\"category\":\"8160\"},{\"category\":\"8180\"},{\"category\":\"8200\"},{\"category\":\"8220\"},{\"category\":\"8240\"},{\"category\":\"9000\"},{\"category\":\"9020\"},{\"category\":\"9040\"}]}";


        Response response = given ()
                .log ().all ()
                .queryParam ("lang", "es")
                .body (body_request)
                .post ();
        String body_response = response.getBody ().asString ();
        String headers_response = response.getHeaders ().toString ();
        System.out.println ("Body Response: " + body_response);
        System.out.println ("Headers: " + headers_response);
        assertEquals (200, response.getStatusCode ());
        assertNotNull (body_response);
        assertTrue (body_response.contains ("data"));
        //new
        JsonPath jsonResponse = response.jsonPath ();
        long time = response.getTime ();
        System.out.println ("Time: " + time);
        assertTrue (time <= 2500);
        String time2 = String.format (String.valueOf (response.time ()));
        Allure.addAttachment ("time response: ", time2);
    }


    @Test
    @Order(3)
    @DisplayName("Test case 3: Crear un usuario nuevo.")
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
    @Order(4)
    @DisplayName("Test case 4: Obtener mi usuario.")
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
        System.out.println ("Headers: " + headers_response);
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
    @Order(5)
    @DisplayName("Test case 5: Obtener token usando correo y contraseña")
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
        System.out.println ("Headers: " + headers_response);
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
    @Order(6)
    @DisplayName("Test case 6: Obtener información básica del usuario.")
    @Severity(SeverityLevel.CRITICAL)
    public void info_usuario () {

        RestAssured.baseURI = String.format ("https://%s/nga/api/v1/%s", url_base, account_id);

        Response response = given ()
                .log ().all ()
                .queryParam ("lang", "es")
                .header ("Content-type", "application/json")
                .header ("Authorization", "tag:scmcoord.com,2013:api " + access_token)
                .get ();

        String body_response = response.getBody ().asString ();

        System.out.println ("Body Response: " + body_response);

        assertEquals (200, response.getStatusCode ());
        assertNotNull (body_response);
        assertTrue (body_response.contains ("account"));
    }



    @Test
    @Order(7)
    @DisplayName("Test case 7: Editar datos del usuario")
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
    @Order(8)
    @DisplayName("Test case 8: Actualizar número de teléfono")
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
    @Order(9)
    @DisplayName("Test case 9: Crear un anuncio.")
    @Severity(SeverityLevel.CRITICAL)
    public void crear_anuncio () {
        String tokenUp2 = obtenerToken ();
        System.out.println ("Token 2: " + tokenUp2);

        RestAssured.baseURI = String.format ("https://%s/v2/accounts/%s/up", url_base, uuid);

        System.out.printf ("Endpoint: %s", RestAssured.baseURI);

        String body_request = "{\n" +
                "    \"category\": \"8121\",\n" +
                "    \"subject\": \"Mudanzas y fletes baratos Junio\",\n" +
                "    \"body\": \"Si estas buscando una mudanza barata, esta es tu opción. Tenemos cobertura en todo el país\",\n" +
                "    \"region\": \"5\",\n" +
                "    \"municipality\": \"51\",\n" +
                "    \"area\": \"140000\",\n" +
                "    \"price\": \"1\",\n" +
                "    \"phone_hidden\": \"true\",\n" +
                "    \"show_phone\": \"false\",\n" +
                "    \"contact_phone\": \"5544345678\"\n" +
                "}";
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
        ad_id = response.jsonPath ().getString ("data.ad.ad_id");
        System.out.println ("ad_id: " + ad_id);

        assertEquals (200, response.getStatusCode ());
        assertNotNull (body_response);

    }

    @Test
    @Order(10)
    @DisplayName("Test case 10: Ver los anuncios pendientes publicados")
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
    @DisplayName("Test case 11: Editar un anuncio.")
    @Severity(SeverityLevel.CRITICAL)
    public void editar_anuncio () {
        String tokenUp2 = obtenerToken ();
        System.out.println ("Token 2: " + tokenUp2);

        RestAssured.baseURI = String.format ("https://%s/accounts/%s/up/%s", url_base, uuid,ad_id);

        System.out.printf ("Endpoint: %s", RestAssured.baseURI);

        String body_request = "{\n" +
                "    \"category\": \"8121\",\n" +
                "    \"subject\": \"Mudanzas y fletes baratos Julio\",\n" +
                "    \"body\": \"Si estas buscando una mudanza barata, esta es tu opción. Tenemos cobertura en todo el país\",\n" +
                "    \"region\": \"5\",\n" +
                "    \"municipality\": \"51\",\n" +
                "    \"area\": \"140000\",\n" +
                "    \"price\": \"1\",\n" +
                "    \"phone_hidden\": \"true\",\n" +
                "    \"show_phone\": \"false\",\n" +
                "    \"contact_phone\": \"5544345678\"\n" +
                "}";
        Response response = given ()
                .log ().all ()
                .header ("Authorization", "Basic " + tokenUp2)
                .contentType ("application/json")
                .accept ("application/json, text/plain, */*")
                .body (body_request)
                .filter (new AllureRestAssured ())
                .header ("x-source", "PHOENIX_DESKTOP")
                .put ();

        String body_response = response.getBody ().asString ();

        System.out.println ("Body Response: " + body_response);

        assertEquals (200, response.getStatusCode ());
        assertNotNull (body_response);

    }

    @Test
    @Order(12)
    @DisplayName("Test case 12: Borrar un anuncio.")
    @Severity(SeverityLevel.CRITICAL)
    public void borrar_anuncio () {
        String tokenUp2 = obtenerToken ();
        System.out.println ("Token 2: " + tokenUp2);

        RestAssured.baseURI = String.format ("https://%s/nga/api/v1%s/klfst/%s", url_base,account_id,ad_id);

        System.out.printf ("Endpoint URL %s ", RestAssured.baseURI);

        String body_request = "{\n" +
                "    \"delete_reason\": {\n" +
                "        \"code\": \"5\"\n" +
                "    }\n" +
                "}";

        Response response = given ()
                .log ().all ()
                .header ("Content-type", "application/json")
                .header ("Authorization", "tag:scmcoord.com,2013:api " + access_token)
                .body (body_request)
                .filter (new AllureRestAssured ())
                .delete ();


        String body_response = response.getBody ().asString ();

        System.out.println ("Body Response: " + body_response);

        assertEquals (403, response.getStatusCode ());
        assertNotNull (body_response);
        assertTrue (body_response.contains ("ERROR_AD_ALREADY_DELETED"));

    }

    @Test
    @Order(13)
    @DisplayName("Test case 13: Crear una nueva dirección")
    @Severity(SeverityLevel.BLOCKER)
    public void crear_direccion () {
        // phoneNumber

        String token2UP = uuid + ":" + access_token;
        token2 = Base64.getEncoder ().encodeToString (token2UP.getBytes ());

        RestAssured.baseURI = String.format ("https://%s/addresses/v1/create", url_base);

        Response response = given ()
                .log ().all ()
                .header ("Content-type", "application/x-www-form-urlencoded")
                .contentType ("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam ("contact", "Vendedor Casas Durango")
                .formParam ("phone", "5511111111")
                .formParam ("rfc", "VESC9012113E3")
                .formParam ("zipCode", "06720")
                .formParam ("exteriorInfo", "DR Erazo")
                .formParam ("region", "16")
                .formParam ("municipality", "755")
                .formParam ("alias", "CASA DE LAS ARTESANIAS SUR")
                .header ("Authorization", "Basic " + token2)
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
    @Order(14)
    @DisplayName("Test case 14: Editar una dirección.")
    @Severity(SeverityLevel.BLOCKER)
    public void editar_direccion () {

        String token2UP = uuid + ":" + access_token;
        token2 = Base64.getEncoder ().encodeToString (token2UP.getBytes ());

        RestAssured.baseURI = String.format ("https://%s/addresses/v1/modify/%s", url_base,addressID);

        Response response = given ()
                .log ().all ()
                .header ("Content-type", "application/x-www-form-urlencoded")
                .header ("Authorization", "Basic " + token2)
                .formParam ("zipCode", "06720")
                .formParam ("exteriorInfo", "117")
                .formParam ("region", "11")
                .formParam ("municipality", "292")
                .formParam ("area", "7488")
                .formParam ("alias", "Depto")
                .filter (new AllureRestAssured ())
                .put ();


        String body_response = response.getBody ().asString ();

        System.out.println ("Body Response: " + body_response);

        assertEquals (200, response.getStatusCode ());
        assertNotNull (body_response);
        assertTrue (body_response.contains ("{\"message\":\"" + addressID + " modified correctly\"}"));
    }

    @Test
    @Order(15)
    @DisplayName("Test case 15: Eliminar dirección previamente dada de alta")
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
    @Test
    @Order(16)
    @DisplayName("Test case 16: Obtener el detalle del balance")
    @Severity(SeverityLevel.NORMAL)
    public void balance() {

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
        System.out.println ("Headers: " + headers_response);

        assertEquals (200, response.getStatusCode ());
        assertNotNull (body_response);
        assertTrue (body_response.contains ("token_if_re_free"));
        assertTrue (time < 2500);

    }
    @Test
    @Order(17)
    @DisplayName("Test case 17: Olvide mi contraseña.")
    @Severity(SeverityLevel.BLOCKER)
    public void olvide_contrsena () {
        RestAssured.baseURI = String.format ("https://%s/nga/api/v1/private/accounts/otp", url_base);

        String body_request = "{\n" +
                "    \"account\": {\n" +
                "        \"email\": \"test1_agente@mailinator.com\"\n" +
                "    }\n" +
                "}";

        Response response = given ()
                .log ().all ()
                .queryParam ("lang", "es")
                .body (body_request)
                .post ();
        String body_response = response.getBody ().asString ();
        String headers_response = response.getHeaders ().toString ();
        System.out.println ("Body Response: " + body_response);
        System.out.println ("Headers: " + headers_response);
        assertEquals (200, response.getStatusCode ());
        assertNotNull (body_response);
        assertTrue (body_response.contains ("account_id"));

    }

    @Test
    @Order(18)
    @DisplayName("Test case 18: Buscar trabajo.")
    @Severity(SeverityLevel.CRITICAL)
    public void buscar_trabajo () {
        String tokenUp2 = obtenerToken ();
        System.out.println ("Token 2: " + tokenUp2);

        RestAssured.baseURI = String.format ("https://%s/v2/accounts/%s/up", url_base, uuid);

        System.out.printf ("Endpoint: %s", RestAssured.baseURI);

        String body_request = "{\n" +
                "    \"category\": \"6040\",\n" +
                "    \"subject\": \"Busco trabajo medio tiempo 6 niños\",\n" +
                "    \"body\": \"Estudiante busca empleo de medio tiempo.\",\n" +
                "    \"region\": \"9\",\n" +
                "    \"municipality\": \"86186\",\n" +
                "    \"area\": \"146072\",\n" +
                "    \"price\": \"200\",\n" +
                "    \"phone_hidden\": \"true\",\n" +
                "    \"show_phone\": \"false\",\n" +
                "    \"contact_phone\": \"5512345678\"\n" +
                "}";
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
        ad_id = response.jsonPath ().getString ("data.ad.ad_id");
        System.out.println ("ad_id: " + ad_id);

        assertEquals (200, response.getStatusCode ());
        assertNotNull (body_response);

    }
    @Test
    @Order(19)
    @DisplayName("Test case 19: Búsqueda de artículos")
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
        System.out.println ("Headers: " + headers_response);

        assertEquals (200, response.getStatusCode ());
        assertNotNull (body_response);
        assertTrue (time < 2500);
    }

    @Test
    @Order(20)
    @DisplayName("Test case 20: Mis ventas.")
    @Severity(SeverityLevel.NORMAL)
    public void mis_ventas() {
        String tokenUp2 = obtenerToken ();
        System.out.println ("Token 2: " + tokenUp2);
        RestAssured.baseURI = String.format ("https://%s/delivery/v1/seller/order", url_base);

        Response response = given ()
                .log ().all ()
                .header ("Authorization", "Basic " + tokenUp2)
                .filter (new AllureRestAssured ())
                .get ();

        String body_response = response.getBody ().asString ();
        String headers_response = response.getHeaders ().toString ();
        long time = response.getTime ();

        System.out.println ("Time: " + time);
        System.out.println ("Body Response: " + body_response);
        System.out.println ("Headers: " + headers_response);

        assertEquals (200, response.getStatusCode ());
        assertNotNull (body_response);
        assertTrue (time < 2500);
    }

    @Test
    @Order(21)
    @DisplayName("Test case 21: Mis compras.")
    @Severity(SeverityLevel.NORMAL)
    public void mis_compras() {
        String tokenUp2 = obtenerToken ();
        System.out.println ("Token 2: " + tokenUp2);
        RestAssured.baseURI = String.format ("https://%s/delivery/v1/buyer/order", url_base);

        Response response = given ()
                .log ().all ()
                .header ("Authorization", "Basic " + tokenUp2)
                .filter (new AllureRestAssured ())
                .get ();

        String body_response = response.getBody ().asString ();
        String headers_response = response.getHeaders ().toString ();
        long time = response.getTime ();

        System.out.println ("Time: " + time);
        System.out.println ("Body Response: " + body_response);
        System.out.println ("Headers: " + headers_response);

        assertEquals (200, response.getStatusCode ());
        assertNotNull (body_response);
        assertTrue (time < 1000);
    }
    }