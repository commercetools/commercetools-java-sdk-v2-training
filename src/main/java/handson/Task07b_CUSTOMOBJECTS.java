package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.custom_object.CustomObjectDraftBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import handson.impl.ApiPrefixHelper;
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

        final ProjectApiRoot client = createApiClient(apiClientPrefix);
        Logger logger = LoggerFactory.getLogger(Task07b_CUSTOMOBJECTS.class.getName());

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

        logger.info("Custom Object info: " +
                client
                        .customObjects()
                        .post(
                                CustomObjectDraftBuilder.of()
                                        .container("plants-compatibility-info")
                                        .key("tulip-seed-product")
                                        .value(
                                                new ObjectMapper()
                                                        .readTree(tulipObject.toString()))
                                        .build()
                        )
                        .execute()
                        .get()
                        .getBody().getId()
        );

        client.close();
    }
}
