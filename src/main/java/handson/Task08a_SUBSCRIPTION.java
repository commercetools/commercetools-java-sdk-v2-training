package handson;


import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.subscription.*;
import com.commercetools.api.models.type.ResourceTypeId;
import handson.impl.ApiPrefixHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;

/**
 * Create a subscription for customer change requests.
 *
 */
public class Task08a_SUBSCRIPTION {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        final ProjectApiRoot client = createApiClient(apiClientPrefix);
        Logger logger = LoggerFactory.getLogger(Task08a_SUBSCRIPTION.class.getName());

        logger.info("Created subscription: " +
                client
                        .subscriptions()
                        .post(
                                SubscriptionDraftBuilder.of()
                                        .key("mhOrderPlacedSubscription")
                                        .destination(
                                                //for GCP Pub/Sub topic
                                                GoogleCloudPubSubDestinationBuilder.of()
                                                        .projectId("ct-support")
                                                        .topic("training-subscription-sample")
                                                        .build()
                                                //for AWS SQS Queue
//                                                SqsDestinationBuilder.of()
//                                                        .queueUrl("https://sqs.eu-central-1.amazonaws.com/923270384842/training-customer_change_queue")
//                                                        .region("eu-central-1")
//                                                        .accessKey("AKIAJLJRDGBNBIPY2ZHQ")
//                                                        .accessSecret("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")
//                                                        .build()
                                        )
                                        .messages(
                                                MessageSubscriptionBuilder.of()
                                                        .resourceTypeId("order")
                                                        .types("OrderCreated")
                                                        .build()
                                        )
                                        .build()
                        )
                        .execute()
                        .toCompletableFuture().get()
                        .getBody()
        );

        client.close();
    }
}
