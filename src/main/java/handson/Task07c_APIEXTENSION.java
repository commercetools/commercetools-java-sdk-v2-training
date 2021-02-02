package handson;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.extension.*;
import handson.impl.ClientService;
import handson.impl.PrefixHelper;
import io.vrap.rmf.base.client.ApiHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getProjectKey;


public class Task07c_APIEXTENSION {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = PrefixHelper.getDevApiClientPrefix();

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
                                            .key("mhPlantCheck777")
                                            .destination(
                                                    ExtensionAWSLambdaDestinationBuilder.of()
                                                            .arn("arn:aws:lambda:eu-central-1:923270384842:function:training-002-happy-garden-dev-plant-check")
                                                            .accessKey("AKIAJLJRDGBNBIPY2ZHQ")
                                                            .accessSecret("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")
                                                            .build()
                                            )
                                            .triggers(
                                                    Arrays.asList(
                                                            ExtensionTriggerBuilder.of()
                                                                    .resourceTypeId(ExtensionResourceTypeId.ORDER)
                                                                    .actions(
                                                                            Arrays.asList(
                                                                                    ExtensionAction.CREATE
                                                                            )
                                                                    )
                                                                    .build()
                                                    )
                                            )
                                            .build()
                            )
                            .execute()
                            .toCompletableFuture().get()
                            .getBody().getId()
            );
        }

    }
}

