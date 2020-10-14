package handson.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import handson.json.TokenAnswer;
//import okhttp3.*;

import sun.security.util.IOUtils;

import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class ThirdPartyClientService {


    public String createClientAndFetchToken(String clientID, String clientSecret, String projectID)
    {
        return null;

//        OkHttpClient myAPIClient = new OkHttpClient();
//        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
//        Response response = null;
//
//        try {
//            String encoding = Base64.getEncoder().encodeToString(new String(clientID + ":" + clientSecret).getBytes("UTF-8"));
//            RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials");
//
//            // Request for token
//            Request oAuthRequest = new Request.Builder()
//                    .url("https://auth.europe-west1.gcp.commercetools.com/oauth/token")
//                    .post(body)
//                    .addHeader("Authorization", "Basic " + encoding)
//                    .addHeader("cache-control", "no-cache")
//                    .build();
//
//            response = myAPIClient.newCall(oAuthRequest).execute();
//
//            String bodyString = new String(response.body().bytes(), "UTF-8");
//            TokenAnswer tokenAnswer = new ObjectMapper().readValue(bodyString, TokenAnswer.class);
//
//            return tokenAnswer.getAccess_token();
//
//        } catch (IOException e) {
//            System.out.println("Execption" + e.toString());
//        }
//        response.body().close();                        // TODO: Not closing properly!!
//        return "";
    }


    public String createClientAndFetchMeToken(String clientID, String clientSecret, String projectID, String customerEmail, String customerLogon)
    {
//
//        OkHttpClient myAPIClient = new OkHttpClient().newBuilder()
//                .connectionPool(new ConnectionPool(0,1, TimeUnit.MILLISECONDS))
//                .build();
//
//        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
//        Response response = null;
//
//        try {
//            String encoding = Base64.getEncoder().encodeToString(new String(clientID + ":" + clientSecret).getBytes("UTF-8"));
//            RequestBody body = RequestBody.create(mediaType, "grant_type=password&username=" + customerEmail + "&password=" + customerLogon);
//
//            Request oAuthRequest = new Request.Builder()
//                    .url("https://auth.europe-west1.gcp.commercetools.com/oauth/" + projectID + "/customers/token")
//                    .post(body)
//                    .addHeader("Authorization", "Basic " + encoding)
//                    .addHeader("cache-control", "no-cache")
//                    .addHeader("Connection", "close")
//                    .build();
//
//            response = myAPIClient.newCall(oAuthRequest).execute();
//            String bodyString = new String(response.body().bytes(), "UTF-8");
//
//            System.out.println("bodyString: " + bodyString);
//            response.body().close();
//
//            myAPIClient.dispatcher().executorService().shutdown();
//            myAPIClient.connectionPool().evictAll();
//
//            System.out.println("ThirdPartyService was active.");
//
//            TokenAnswer tokenAnswer = new ObjectMapper().readValue(bodyString, TokenAnswer.class);
//
//            return tokenAnswer.getAccess_token();
//
//
//        } catch (IOException e) {
//            System.out.println("Exception: " + e.toString());
//        }
        return "";
    }

}
