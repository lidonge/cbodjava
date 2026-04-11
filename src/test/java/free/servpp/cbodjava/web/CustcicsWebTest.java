package free.servpp.cbodjava.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustcicsWebTest {

    @LocalServerPort
    private int port;

    @Test
    void querySeedCustomerShouldReturnMappedScreenBuffer() throws Exception {
        String body = postJson("""
                {
                  "SCR-ACTION": "Q",
                  "SCR-ID": "0000000001"
                }
                """);

        assertTrue(body.contains("\"SCR-ACTION\":\"Q\""));
        assertTrue(body.contains("\"SCR-ID\":\"0000000001\""));
        assertTrue(body.contains("\"SCR-NAME\":\"ALICE\""));
        assertTrue(body.contains("\"SCR-PHONE\":\"13800138000\""));
        assertTrue(body.contains("\"SCR-ADDRESS\":\"SHANGHAI\""));
    }

    @Test
    void addThenQueryCustomerShouldPersistThroughMyBatis() throws Exception {
        String customerId = "0000000099";

        postJson("""
                {
                  "SCR-ACTION": "A",
                  "SCR-ID": "0000000099",
                  "SCR-NAME": "BOB",
                  "SCR-PHONE": "13900000000",
                  "SCR-ADDRESS": "BEIJING"
                }
                """);

        String body = postJson("""
                {
                  "SCR-ACTION": "Q",
                  "SCR-ID": "0000000099"
                }
                """);

        assertTrue(body.contains("\"SCR-ACTION\":\"Q\""));
        assertTrue(body.contains("\"SCR-ID\":\"" + customerId + "\""));
        assertTrue(body.contains("\"SCR-NAME\":\"BOB\""));
        assertTrue(body.contains("\"SCR-PHONE\":\"13900000000\""));
        assertTrue(body.contains("\"SCR-ADDRESS\":\"BEIJING\""));
    }

    private String postJson(String requestBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/cics/custcics/main-para"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        return response.body();
    }
}
