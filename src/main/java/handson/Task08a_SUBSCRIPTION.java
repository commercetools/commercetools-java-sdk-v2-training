package handson;



import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.subscription.*;
import com.commercetools.api.models.type.ResourceTypeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getProjectKey;

/**
 * Create a subscription for customer change requests.
 *
 */
public class Task08a_SUBSCRIPTION {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String projectKey = getProjectKey("mh-dev-admin.");
        final ApiRoot client = createApiClient("mh-dev-admin.");
        Logger logger = LoggerFactory.getLogger(Task04b_CHECKOUT.class.getName());

            logger.info("Created subscription: " +
                    client
                            .withProjectKey(projectKey)
                            .subscriptions()
                            .post(
                                    SubscriptionDraftBuilder.of()
                                        .key("mhCustomerChangeSubscription")
                                        .destination(
                                                SqsDestinationBuilder.of()
                                                    .queueUrl("https://sqs.eu-central-1.amazonaws.com/923270384842/training-001-happy-garden-dev-customer_change")
                                                    .region("eu-central-1")
                                                    .accessKey("AKIAJLJRDGBNBIPY2ZHQ")
                                                    .accessSecret("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")
                                                .build()
                                        )
                                        .changes(
                                            Arrays.asList(
                                                ChangeSubscriptionBuilder.of()
                                                    .resourceTypeId(
                                                        ResourceTypeId.CUSTOMER.toString().toLowerCase()                      // really toString??
                                                    )
                                                    .build()
                                            )
                                        )
                                        .build()
                            )
                            .execute()
                            .toCompletableFuture().get()
                            .getBody()
                    );

    }
}
