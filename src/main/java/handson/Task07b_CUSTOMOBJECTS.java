package handson;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.custom_object.CustomObjectDraftBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.json.*;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getProjectKey;


public class Task07b_CUSTOMOBJECTS {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String projectKey = getProjectKey("mh-dev-admin.");
        final ApiRoot client = createApiClient("mh-dev-admin.");
        Logger logger = LoggerFactory.getLogger(Task04b_CHECKOUT.class.getName());


            // TODO:
            // Store custom objects
            // container: MH_PlantCheck, Add your prefix
            // key: the product variant sku
            // incompatibleSKUs: all the product variants above sku is incompatible with

            JsonObject tulipObject = Json.createObjectBuilder()
                    .add("incompatibleSKUs", "tulip6125")
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
                            .withProjectKey(projectKey)
                            .customObjects()
                            // .withContainerAndKey("plantCheck", "tulip6736")
                            .post(
                                    CustomObjectDraftBuilder.of()
                                        .container("plantCheck")
                                        .key("tulip6736")
                                        .value(
                                                new ObjectMapper()
                                                    .readTree(tulipObject.toString()))
                                        .build()
                            )
                            .execute()
                            .toCompletableFuture().get()
                            .getBody().getId()
            );

    }
}
