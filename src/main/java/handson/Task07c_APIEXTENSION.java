package handson;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.extension.*;
import handson.impl.ApiPrefixHelper;
import handson.impl.ClientService;
import io.vrap.rmf.base.client.ApiHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getProjectKey;


public class Task07c_APIEXTENSION {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        final String projectKey = getProjectKey(apiClientPrefix);
        final ApiRoot client = createApiClient(apiClientPrefix);
        Logger logger = LoggerFactory.getLogger(Task07c_APIEXTENSION.class.getName());

        try (ApiHttpClient apiHttpClient = ClientService.apiHttpClient) {
            logger.info("Created extension: " +
                    client
                            .withProjectKey(projectKey)
                            .extensions()
                            .post(
                                    ExtensionDraftBuilder.of()
                                            .key("mh_PlantCheck")
                                            .destination(
                                                    ExtensionAWSLambdaDestinationBuilder.of()
                                                            .arn("")
                                                            .accessKey("")
                                                            .accessSecret("")
                                                            .build()
                                            )
                                            .triggers(
                                                    ExtensionTriggerBuilder.of()
                                                            .resourceTypeId(ExtensionResourceTypeId.ORDER)
                                                            .actions(
                                                                    ExtensionAction.CREATE
                                                            )
                                                            .build()
                                            )
                                            .build()
                            )
                            .execute()
                            .toCompletableFuture().get()
                            .getBody().getId()
            );
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}

