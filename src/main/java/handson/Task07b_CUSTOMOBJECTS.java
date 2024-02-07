package handson;

import com.commercetools.api.client.ProjectApiRoot;
import handson.impl.ApiPrefixHelper;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;


public class Task07b_CUSTOMOBJECTS {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        Logger logger = LoggerFactory.getLogger("commercetools");

        final ProjectApiRoot client = createApiClient(apiClientPrefix);


        // TODO:
        // Create a custom object
        // container: plants-compatibility-info
        // key: the product key
        // incompatibleSKUs: all the product variants above sku is incompatible with

        JsonObject tulipObject = Json.createObjectBuilder()
                .add("incompatibleProducts", "basil-seed-product")
                .add("leafletID", "leaflet_1234")

                .add("instructions",
                        Json.createObjectBuilder()
                                .add("title", "Flower Handling")
                                .add("distance_in_m", "2")
                                .add("watering", "heavy")
                                .build()
                )
                .build();

        client
                .customObjects()
                .post(
                        customObjectDraftBuilder -> customObjectDraftBuilder
                                .container("plants-compatibility-info")
                                .key("tulip-seed-product")
                                .value(tulipObject)
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
    }
}
