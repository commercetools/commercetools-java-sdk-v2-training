package handson;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.me.MyCartDraftBuilder;
import handson.impl.ApiPrefixHelper;
import handson.impl.ClientService;
import io.vrap.rmf.base.client.ApiHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.*;


/**
 *
 */
public class Task05_INSTORE_ME {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        Logger logger = LoggerFactory.getLogger(Task05_INSTORE_ME.class.getName());

        // TODO: Create in-store cart with global API client
        //  Provide an API client with global permissions
        //  Provide a customer who is restricted to a store
        //  Note: A global cart creation should fail but an in-store cart should world
        //

        final String globalApiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();
        final String projectKey = getProjectKey(globalApiClientPrefix);
        final ApiRoot client = createApiClient(globalApiClientPrefix);

        try (ApiHttpClient apiHttpClient = ClientService.apiHttpClient) {
            logger.info("Created in-store cart with a global api client: " +
                    ""
            );
        }
        catch (Exception e){
            e.printStackTrace();
        }



        // TODO: Create in-store Cart with in-store API client
        //  Update the ApiPrefixHelper with the prefix for Store API Client
        //  Provide an API client with scope limited to a store
        //  Provide a customer with only store permissions
        //  Try creating a global cart with a global customer and check the error message


        final String storeApiClientPrefix = ApiPrefixHelper.API_STORE_CLIENT_PREFIX.getPrefix();
        final String storeKey = getStoreKey(storeApiClientPrefix);
        final ApiRoot storeClient = createApiClient(storeApiClientPrefix);


        logger.info("Created in-store cart with a store api client: "+
                ""
        );

        // TODO
        //  Visit impex to verify that the carts are holding the same information
        //


        // TODO: Create a cart via /me endpoint
        //  Provide API client with SPA for customer with global permissions
        //  Update the ApiPrefixHelper with the prefix for Me(SPA) API Client
        //  You can also create in-store customer-bound cart
        //  Visit impex to inspect the carts created

        final String meApiClientPrefix = ApiPrefixHelper.API_ME_CLIENT_PREFIX.getPrefix();
        final ApiRoot meClient = createMeTokenApiClient(meApiClientPrefix);
        final String customerEmail = getCustomerEmail(meApiClientPrefix);

        logger.info("Get cart for customer via me endpoint: " +
                meClient
                        .withProjectKey(projectKey)
                        //.inStoreKeyWithStoreKeyValue("berlin-store")
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

        // TODO: Create in-store customer-bound Cart with in-store-me API client
        //  Update the ApiPrefixHelper with the prefix for Me(SPA) API Client
        //  Provide in-store-me API client with scope for a store and me endpoint
        //  Try creating a global cart without me and check the error message
        //  Visit impex to inspect the carts created

        final String storeMeApiClientPrefix = ApiPrefixHelper.API_STORE_ME_CLIENT_PREFIX.getPrefix();
        final ApiRoot storeMeClient = createStoreMeApiClient(storeMeApiClientPrefix);
        final String storeCustomerEmail = getCustomerEmail(storeMeApiClientPrefix);

        logger.info("Created in-store cart with a store api client: "+
                storeMeClient.withProjectKey(projectKey)
                        .inStoreKeyWithStoreKeyValue(storeKey)
                        .me()
                        .carts()
                        .post(
                                MyCartDraftBuilder.of()
                                        .deleteDaysAfterLastModification(90L)
                                        .currency("EUR")
                                        .customerEmail(storeCustomerEmail)
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
