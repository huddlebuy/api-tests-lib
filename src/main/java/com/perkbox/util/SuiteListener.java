package com.perkbox.util;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import static io.restassured.RestAssured.given;

public class SuiteListener implements ISuiteListener {

    public void onStart(ISuite iSuite) {
        int serviceReady = 0;
        ExtractableResponse<Response> response = callHealth();

        String marker = "#########################################################";
        System.out.println(marker + "\nWaiting for service to be up ...");
        
        while (serviceReady < 20 && response.statusCode() != 200) {
            try {
                Thread.sleep(1000);
                serviceReady++;
                response = callHealth();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (response.statusCode() != 200) {
            System.out.println("Exiting... Service health check was unsuccessful.\n" + marker);
            System.exit(-1);
        }
        
        System.out.println("Service ready!\nSeconds delayed to be ready: " + serviceReady + "\n" + marker);
    }
    
    private ExtractableResponse<Response> callHealth() {
        return given().header("Content-Type", "application/json").when().get(Env.get("HEALTH_URL")).then().extract();
    }

    public void onFinish(ISuite suite) {}
}
