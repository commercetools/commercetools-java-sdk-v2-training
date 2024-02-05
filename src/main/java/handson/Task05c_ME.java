package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.CartDraftBuilder;
import com.commercetools.api.models.me.MyCartDraftBuilder;
import handson.impl.ApiPrefixHelper;
import io.vrap.rmf.base.client.ApiHttpResponse;
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

        meClient
                .me()
                .carts()
                .post(
                        myCartDraftBuilder -> myCartDraftBuilder.of()
                                          .currency("EUR")
                                          .deleteDaysAfterLastModification(90L)
                                          .customerEmail(customerEmail)
                )
                .execute()
                .thenApply(ApiHttpResponse::getBody)
                .handle((cartApiHttpResponse, exception) -> {
                    if (exception != null) {
                        logger.error("Exception: " + exception.getMessage());
                        return null;
                    }
                    ;
                    logger.info("Me cart with an SPA Client: "
                            + cartApiHttpResponse.getId());
                    return cartApiHttpResponse;
                })
                .thenRun(() -> meClient.close());

        // TODO: Create in-store customer-bound Cart with in-store-me API client
        //  Update the ApiPrefixHelper with the prefix for Me(SPA) API Client
        //  Provide in-store-me API client with scope for a store and me endpoint
        //  Try creating a global cart without me and check the error message
        //  Visit impex to inspect the carts created

//        final String storeMeApiClientPrefix = ApiPrefixHelper.API_STORE_ME_CLIENT_PREFIX.getPrefix();
//        final ProjectApiRoot meStoreClient = createStoreMeApiClient(storeMeApiClientPrefix);
//        final String meStoreKey = getStoreKey(storeMeApiClientPrefix);
//        final String storeCustomerEmail = getCustomerEmail(storeMeApiClientPrefix);
//
//        meStoreClient
//                .inStore(meStoreKey)
//                .me()
//                .carts()
//                .post(
//                        myCartDraftBuilder -> myCartDraftBuilder.of()
//                                .deleteDaysAfterLastModification(90L)
//                                .currency("EUR")
//                                .customerEmail(storeCustomerEmail)
//                )
//                .execute()
//                .thenApply(ApiHttpResponse::getBody)
//                .handle((cartApiHttpResponse, exception) -> {
//                    if (exception != null) {
//                        logger.error("Exception: " + exception.getMessage());
//                        return null;
//                    }
//                    ;
//                    logger.info("Me cart with an SPA Client and a Store Customer: "
//                            + cartApiHttpResponse.getId());
//                    return cartApiHttpResponse;
//                })
//                .thenRun(() -> meStoreClient.close());

    }
}
