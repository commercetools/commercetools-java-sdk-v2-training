package handson;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.extension.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import static handson.impl.ClientService.createApiClient;


public class Task07c_APIEXTENSION {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String projectKey = "training-011-avensia-test";
        final ApiRoot client = createApiClient("mh-dev-admin.");
        Logger logger = Logger.getLogger(Task04b_CHECKOUT.class.getName());

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

