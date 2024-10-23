package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.product.Product;
import handson.impl.ApiPrefixHelper;
import handson.impl.StoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getStoreKey;

public class Task02b_UPDATE {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();
        try (ProjectApiRoot apiRoot = createApiClient(apiClientPrefix)) {
            Logger logger = LoggerFactory.getLogger("commercetools");


          // TODO: Assign Product Selection to your store
          //
            Product product = apiRoot.products()
                    .withKey("emerald-velvet-chair")
                    .get()
                    .executeBlocking().getBody();

            apiRoot.products()
                    .withId(product.getId())
                    .post(pu -> pu
                            .plusActions(
                                    ab -> ab.addToCategoryBuilder()
                                            .category(cri -> cri.key("clearance"))
                            )
                            .version(product.getVersion())
                    )
                    .execute()
                    .thenAccept(productApiHttpResponse ->
                            logger.info("Product {} added to category.", product.getKey()))
                .exceptionally(throwable -> {
                    logger.error("Exception: {}", throwable.getMessage());
                    return null;
                }).join();
        }
    }
}

