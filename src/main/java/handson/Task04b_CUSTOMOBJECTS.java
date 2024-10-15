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

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        Logger logger = LoggerFactory.getLogger("commercetools");

        final ProjectApiRoot client = createApiClient(apiClientPrefix);


        // TODO: CREATE a custom object
        // container: cross-sell-upsell-info,
        // key: the product key
        // cross-sell: references to products
        //

        Map<String, Object> jsonObject = new HashMap<>();
        jsonObject.put("crossSell", Arrays.asList(
                (Reference.productBuilder().id("6cb809eb-b12f-460b-9ac9-f356cc445f17").build()),
                Reference.productBuilder().id("2a6c7d6e-ac0c-479f-8eba-c7613027b830").build()));
        jsonObject.put("upSell", Arrays.asList(
                Reference.productBuilder().id("31c70b1a-d309-4e15-a5d8-3c3a0f02e866").build(),
                Reference.productBuilder().id("66eb1ec0-50e1-43ad-a491-7970361dc884").build()));

        client
            .customObjects()
            .post(
                customObjectDraftBuilder -> customObjectDraftBuilder
                    .container("cross-sell-upsell-info")
                    .key("86651")
                    .value(jsonObject)
            ).execute()
            .thenApply(ApiHttpResponse::getBody)
            .handle((customObject, exception) -> {
                if (exception == null) {
                    logger.info("Custom Object ID: " + customObject.getId());
                    return customObject;
                }
                logger.error("Exception: " + exception.getMessage());
                return null;
            }).thenRun(() -> client.close());


        // TODO: CREATE a custom object
        // Update the Product Type to add a reference type attribute for key-value-document.
        // Update Products by storing a reference to the Custom Object created in the above step.
        //
    }
}
