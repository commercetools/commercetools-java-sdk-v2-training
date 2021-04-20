package handson;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.custom_object.CustomObjectDraftBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import handson.impl.ApiPrefixHelper;
import handson.impl.ClientService;
import io.vrap.rmf.base.client.ApiHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getProjectKey;


public class Task07b_CUSTOMOBJECTS {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        final String projectKey = getProjectKey(apiClientPrefix);
        final ApiRoot client = createApiClient(apiClientPrefix);
        Logger logger = LoggerFactory.getLogger(Task07b_CUSTOMOBJECTS.class.getName());

        try (ApiHttpClient apiHttpClient = ClientService.apiHttpClient) {

            // TODO:
            // Store custom objects
            // container: MH_PlantCheck, Add your prefix
            // key: the product variant sku
            // incompatibleSKUs: all the product variants above sku is incompatible with

            JsonObject tulipObject = Json.createObjectBuilder()
                    .add("incompatibleSKUs", "BASILSEED01")
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
                                            .container("mhPlantCheck")
                                            .key("TULIPSEED01")
                                            .value(
                                                    new ObjectMapper()
                                                            .readTree(tulipObject.toString()))
                                            .build()
                            )
                            .execute()
                            .toCompletableFuture().get()
                            .getBody().getId()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
