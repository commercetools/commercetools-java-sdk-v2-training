package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.common.Reference;
import handson.impl.ApiPrefixHelper;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;


public class Task04b_CUSTOMOBJECTS {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_STORE_CLIENT_PREFIX.getPrefix();
        try (ProjectApiRoot apiRoot = createApiClient(apiClientPrefix)) {
            Logger logger = LoggerFactory.getLogger("commercetools");

            // TODO: CREATE a custom object
            // container: cross-sell-upsell-info,
            // key: the product key
            // cross-sell: references to products
            //

            Map<String, Object> jsonObject = new HashMap<>();
            jsonObject.put("crossSell", Arrays.asList(
                (Reference.productBuilder().id("72e5463f-b8e6-4d9f-8712-696650d37302").build()),
                Reference.productBuilder().id("c1cb54ce-166d-40d9-96d6-cc989298b932").build()));
            jsonObject.put("upSell", Arrays.asList(
                Reference.productBuilder().id("c1cb54ce-166d-40d9-96d6-cc989298b932").build(),
                Reference.productBuilder().id("72e5463f-b8e6-4d9f-8712-696650d37302").build()));

            apiRoot
                .customObjects()
                .post(
                    customObjectDraftBuilder -> customObjectDraftBuilder
                        .container("cross-sell-upsell-info")
                        .key("rustic-bowl")
                        .value(jsonObject)
                ).execute()
                .thenApply(ApiHttpResponse::getBody)
                .thenAccept(customObject -> {
                            logger.info("Custom Object ID: " + customObject.getId());
                        }
                )
                .exceptionally(throwable -> {
                    logger.error("Exception: {}", throwable.getMessage());
                    return null;
                }).join();


            // TODO: CREATE a custom object
            // Update the Product Type to add a reference type attribute for key-value-document.
            // Update Products by storing a reference to the Custom Object created in the above step.
            //
        }
    }
}
