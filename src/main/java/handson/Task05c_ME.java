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

        Logger logger = LoggerFactory.getLogger("commercetools");

        // TODO: Create in-store customer-bound Cart with me API client
        //  Update the ApiPrefixHelper with the prefix for Me(SPA) API Client
        //  Provide me API client with scope for a store and me endpoint
        //  Visit impex to inspect the carts created

        final String meApiClientPrefix = ApiPrefixHelper.API_ME_CLIENT_PREFIX.getPrefix();
        final ProjectApiRoot meClient = createStoreMeApiClient(meApiClientPrefix);
        final String storeKey = getStoreKey(meApiClientPrefix);
        final String customerEmail = getCustomerEmail(meApiClientPrefix);

        meClient
            .inStore(storeKey)
            .me()
            .carts()
            .post(
                myCartDraftBuilder -> myCartDraftBuilder.of()
                    .deleteDaysAfterLastModification(90L)
                    .currency("EUR")
                    .customerEmail(customerEmail)
            ).execute()
            .thenApply(ApiHttpResponse::getBody)
            .handle((cartApiHttpResponse, exception) -> {
                if (exception == null) {
                    logger.info("Me cart with an SPA Client: " + cartApiHttpResponse.getId());
                    return cartApiHttpResponse;
                }
                logger.error("Exception: " + exception.getMessage());
                return null;
            }).thenRun(() -> meClient.close());

    }
}
