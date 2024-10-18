package handson;

import com.commercetools.api.client.ProjectApiRoot;
import handson.impl.ApiPrefixHelper;
import handson.impl.ClientService;
import handson.impl.StoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getStoreKey;


/**
 * Configure sphere client and get project information.
 *
 * See:
 *  TODO dev.properties
 *  TODO {@link ClientService#createApiClient(String prefix)}
 */
public class Task02a_CREATE_STORE {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_STORE_CLIENT_PREFIX.getPrefix();
        try (ProjectApiRoot apiRoot = createApiClient(apiClientPrefix)) {

            Logger logger = LoggerFactory.getLogger("commercetools");
            final String storeKey = getStoreKey(apiClientPrefix);
            final StoreService storeService = new StoreService(apiRoot, storeKey);

            // TODO: CREATE a store
            //

            storeService.createStore()
                    .thenAccept(storeApiHttpResponse ->
                            logger.info("Store created: {}",
                                    storeApiHttpResponse.getBody().getId())
                    )
                    .exceptionally(throwable -> {
                        logger.error("Exception: {}", throwable.getMessage());
                        return null;
                    }).join();
        }
    }
}
