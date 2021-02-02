package handson;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.cart.CartDraftBuilder;
import com.commercetools.api.models.me.MyCartDraftBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import handson.impl.*;
//import okhttp3.*;
import io.vrap.rmf.base.client.ApiHttpClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.*;


/**
 *
 */
public class Task05_INSTORE_ME {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        Logger logger = LoggerFactory.getLogger(Task05_INSTORE_ME.class.getName());

        // TODO: Create in-store cart with global api client
        //  Provide an api client with global permissions
        //  Provide a customer with only store permissions
        //
        final String globalApiClientPrefix = PrefixHelper.getDevApiClientPrefix();
        final String projectKey = getProjectKey(globalApiClientPrefix);
        final ApiRoot client = createApiClient(globalApiClientPrefix);

        try (ApiHttpClient apiHttpClient = ClientService.apiHttpClient) {
            logger.info("Created in-store cart with a global api client: " +
                    " "
            );
        }


        // TODO: Create in-store Cart with in-store-client
        //  Provide api client for customer with only store permissions
        //
        final String storeApiClientPrefix = "berlin-store.";
        final ApiRoot storeClient = createStoreApiClient(storeApiClientPrefix);
        final String storeKey = getStoreKey(storeApiClientPrefix);
        final String storeCustomerEmail = getCustomerEmail(storeApiClientPrefix);

        logger.info("Created in-store cart with a store api client: "+
                " "
        );

        // TODO
        //  Verify on impex that the carts are holding the same information
        //




        // TODO: Create a cart via me-endpoint
        //  Provide me api client for customer with global permissions
        // Update the PrefixHelper with the prefix for your Me API Client
        //
        final String meApiClientPrefix = PrefixHelper.getMeApiClientPrefix();

        final ApiRoot meClient = createMeTokenApiClient(meApiClientPrefix);
        final String customerEmail = getCustomerEmail(meApiClientPrefix);

        logger.info("Get cart for customer via me endpoint: " +
                meClient
                        .withProjectKey(projectKey)
                        .me()
                        .carts()
                        .post(
                                MyCartDraftBuilder.of()
                                        .currency("EUR")
                                        .deleteDaysAfterLastModification(90l)
                                        .customerEmail(customerEmail)
                                        .build()
                        )
                        .execute()
                        .exceptionally(throwable -> {
                            logger.info(throwable.getLocalizedMessage().toString());
                            return null;
                        })
                        .toCompletableFuture().get()
                        .getBody().getId()
        );

    }
}
