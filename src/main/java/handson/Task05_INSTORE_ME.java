package handson;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.cart.CartDraftBuilder;
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

        // TODO: Create in-store cart with global api client
        //  Provide an api client with global permissions
        //  Provide a customer with only store permissions
        //
        final String globalApiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();
        final String projectKey = getProjectKey(globalApiClientPrefix);
        final ApiRoot client = createApiClient(globalApiClientPrefix);

        try (ApiHttpClient apiHttpClient = ClientService.apiHttpClient) {
            logger.info("Created in-store cart with a global api client: " +
                    client
                            .withProjectKey(projectKey)
                            .inStoreKeyWithStoreKeyValue("berlin-store")
                            .carts()
                            .post(
                                    CartDraftBuilder.of()
                                            .currency("EUR")
                                            .deleteDaysAfterLastModification(90l)
                                            .customerId("a59d3061-3f3e-41db-88df-0f9c0e24deae")
                                            .customerEmail("michael13@example.com")
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


        // TODO: Create in-store Cart with in-store-client
        //  Provide api client for customer with only store permissions
        //
        final String storeApiClientPrefix = ApiPrefixHelper.API_STORE_CLIENT_PREFIX.getPrefix();
        final ApiRoot storeClient = createStoreApiClient(storeApiClientPrefix);
        final String storeKey = getStoreKey(storeApiClientPrefix);
        final String storeCustomerEmail = getCustomerEmail(storeApiClientPrefix);

        logger.info("Created in-store cart with a store api client: "+
                storeClient
                        .withProjectKey(projectKey)
                        .inStoreKeyWithStoreKeyValue(storeKey)
                        .me()
                        .carts()
                        .post(
                                MyCartDraftBuilder.of()
                                        .currency("EUR")
                                        .deleteDaysAfterLastModification(90l)
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

        // TODO
        //  Verify on impex that the carts are holding the same information
        //


        // TODO: Create a cart via me-endpoint
        //  Provide me api client for customer with global permissions
        //
        final String meApiClientPrefix = ApiPrefixHelper.API_ME_CLIENT_PREFIX.getPrefix();
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
