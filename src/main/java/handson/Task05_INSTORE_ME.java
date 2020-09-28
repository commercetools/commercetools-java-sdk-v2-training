package handson;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.cart.CartDraftBuilder;
import com.commercetools.api.models.me.MyCartDraftBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import handson.impl.*;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import static handson.impl.ClientService.*;


/**
 *
 */
public class Task05_INSTORE_ME {

    // TODO
    // see below, me-carts


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String projectKey = "training-011-avensia-test";
        final String storeKey = "berlin-store";
        final ApiRoot client = createApiClient("mh-dev-admin.");

        CustomerService customerService = new CustomerService(client, projectKey);
        Logger logger = Logger.getLogger(Task04b_CHECKOUT.class.getName());

        // TODO: Create an instore cart
        // Use CartDrafts
        //
        logger.info("Created instore cart: " +
            customerService.getCustomerByKey("customer-michele")
                .thenComposeAsync(customerApiHttpResponse ->
                        client
                            .withProjectKey(projectKey)
                            .inStoreKeyWithStoreKeyValue(storeKey)
                            .carts()
                            .post(
                                    CartDraftBuilder.of()
                                            .currency("EUR")
                                            .deleteDaysAfterLastModification(90l)
                                            .customerEmail(customerApiHttpResponse.getBody().getEmail())
                                            .build()
                            )
                            .execute()
                )
                .toCompletableFuture().get()
                .getBody().getId()
        );




        // TODO: Create an Cart using a ME-endpoint
        // Use correct auth-url
        // Use MyCartDraft
        //

        String clientID = "UC6k6y0EFoloW6bizT5PskhW";                   // TODO Parse from dev.properties
        String clientSecret = "wj3tWTnXY1Y4I__DKeoaKpeUBujm27mI";

        // CustomerEmail & Password
        // Encode Base64 by Hand !!
        String customerEmail = "michael.hartwig%40test.com";
        String customerLogon = "password";

        ThirdPartyClientService thirdPartyClientService = new ThirdPartyClientService();
        String metoken = thirdPartyClientService.createClientAndFetchMeToken(clientID, clientSecret, projectKey, customerEmail, customerLogon);

        logger.info("Fetched me-token: " + metoken);

        // TODO: Solution using ThirdParty
        //
        OkHttpClient myAPIClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        Response response = null;

        JSONObject simpleCart = new JSONObject();
        simpleCart.put("currency", "EUR");
        ObjectMapper objectMapper = new ObjectMapper();
        try {

            Request oAuthRequest = new Request.Builder()
                    .url("https://api.europe-west1.gcp.commercetools.com/" + projectKey + "/me/carts")
                    .post(
                           RequestBody.create(MediaType.parse("application/json; charset=utf-8"), simpleCart.toString())
                    )
                    .addHeader("Authorization", "Bearer " + metoken)
                    .addHeader("cache-control", "no-cache")
                    .build();

            response = myAPIClient.newCall(oAuthRequest).execute();

            String bodyString = new String(response.body().bytes(), "UTF-8");
            logger.info("Create a simple me-cart: " + bodyString);

        } catch (IOException e) {
            logger.info("Execption" + e.toString());
        }
        response.body().close();

        // TODO: Solution using ConstantToken-apiRoot (then move this to Session 09, performance)
        // TODO: Solution using MeEndpoint-apiRoot


    }
}
