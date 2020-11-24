package handson;



import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.subscription.*;
import com.commercetools.api.models.type.ResourceTypeId;
import handson.impl.ClientService;
import io.vrap.rmf.base.client.ApiHttpClient;
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

        // TODO:
        //  Check your prefix
        //
        String apiClientPrefix = "mh-dev-admin.";

        final String projectKey = getProjectKey(apiClientPrefix);
        final ApiRoot client = createApiClient(apiClientPrefix);
        Logger logger = LoggerFactory.getLogger(Task08a_SUBSCRIPTION.class.getName());

        try (ApiHttpClient apiHttpClient = ClientService.apiHttpClient) {
            logger.info("Created subscription: " +
                    client
                            .withProjectKey(projectKey)
                            .subscriptions()
                            .post(
                                    SubscriptionDraftBuilder.of()
                                            .key("mhCustomerChangeSubscription")
                                            .destination(
                                                    SqsDestinationBuilder.of()
                                                            .queueUrl("")
                                                            .region("eu-central-1")
                                                            .accessKey("")
                                                            .accessSecret("")
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
}
