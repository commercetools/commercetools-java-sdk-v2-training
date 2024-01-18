package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.CartDraftBuilder;
import com.commercetools.api.models.me.MyCartDraftBuilder;
import handson.impl.ApiPrefixHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.*;

public class Task05c_ME {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        Logger logger = LoggerFactory.getLogger(Task05c_ME.class.getName());

        // TODO: Create a cart via /me endpoint
        //  Provide API client with SPA for customer with global permissions
        //  Update the ApiPrefixHelper with the prefix for Me(SPA) API Client
        //  Visit impex to inspect the carts created

        final String meApiClientPrefix = ApiPrefixHelper.API_ME_CLIENT_PREFIX.getPrefix();
        final ProjectApiRoot meClient = createMeTokenApiClient(meApiClientPrefix);
        final String customerEmail = getCustomerEmail(meApiClientPrefix);

        logger.info("Created cart for customer via me endpoint: " +
                meClient
                        .me()
                        .carts()
                        .post(
                                MyCartDraftBuilder.of()
                                                  .currency("EUR")
                                                  .deleteDaysAfterLastModification(90L)
                                                  .customerEmail(customerEmail)
                                                  .build()
                        )
                        .execute()
                        .exceptionally(throwable -> {
                            logger.info(throwable.getLocalizedMessage());
                            return null;
                        })
                        .get()
                        .getBody().getId()
        );
        meClient.close();

        // TODO: Create in-store customer-bound Cart with in-store-me API client
        //  Update the ApiPrefixHelper with the prefix for Me(SPA) API Client
        //  Provide in-store-me API client with scope for a store and me endpoint
        //  Try creating a global cart without me and check the error message
        //  Visit impex to inspect the carts created

        final String storeMeApiClientPrefix = ApiPrefixHelper.API_STORE_ME_CLIENT_PREFIX.getPrefix();
        final ProjectApiRoot meStoreClient = createStoreMeApiClient(storeMeApiClientPrefix);
        final String meStoreKey = getStoreKey(storeMeApiClientPrefix);
        final String storeCustomerEmail = getCustomerEmail(storeMeApiClientPrefix);

        logger.info("Created in-store cart with a store api client: "+
                meStoreClient
                        .inStore(meStoreKey)
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
                            logger.info(throwable.getLocalizedMessage());
                            return null;
                        })
                        .get()
                        .getBody().getId()
        );
        meStoreClient.close();
    }
}
