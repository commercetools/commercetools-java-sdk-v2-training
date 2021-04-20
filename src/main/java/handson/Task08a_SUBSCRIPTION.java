package handson;


import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.subscription.ChangeSubscriptionBuilder;
import com.commercetools.api.models.subscription.SqsDestinationBuilder;
import com.commercetools.api.models.subscription.SubscriptionDraftBuilder;
import com.commercetools.api.models.type.ResourceTypeId;
import handson.impl.ApiPrefixHelper;
import handson.impl.ClientService;
import io.vrap.rmf.base.client.ApiHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getProjectKey;

/**
 * Create a subscription for customer change requests.
 *
 */
public class Task08a_SUBSCRIPTION {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {


        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

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
                                            .key("mh_CustomerChangeSubscription")
                                            .destination(
                                                    SqsDestinationBuilder.of()
                                                            .queueUrl("")
                                                            .region("")
                                                            .accessKey("")
                                                            .accessSecret("")
                                                            .build()
                                            )
                                            .changes(
                                                    ChangeSubscriptionBuilder.of()
                                                            .resourceTypeId(
                                                                    ResourceTypeId.CUSTOMER.toString().toLowerCase()                      // really toString??
                                                            )
                                                            .build()
                                            )
                                            .build()
                            )
                            .execute()
                            .toCompletableFuture().get()
                            .getBody()
            );
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
