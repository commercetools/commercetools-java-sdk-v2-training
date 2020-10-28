package handson;


import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.defaultconfig.ServiceRegion;
import com.commercetools.api.models.project.Project;
import handson.impl.ClientService;
import io.vrap.rmf.base.client.*;
import io.vrap.rmf.base.client.http.RetryMiddleware;
import io.vrap.rmf.base.client.oauth2.ClientCredentials;
import io.vrap.rmf.okhttp.VrapOkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static com.commercetools.api.defaultconfig.ApiFactory.*;
import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getProjectKey;

public class Task09b_SPHERECLIENT_LOGGING {


    public static final String MH_DEV_ADMIN = "mh-dev-admin.";

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String projectKey = getProjectKey(MH_DEV_ADMIN);
        final ApiRoot client = createApiClient(MH_DEV_ADMIN);
        Logger logger = LoggerFactory.getLogger(Task04b_CHECKOUT.class.getName());

        // TODO

            // 1
            // Intensive logging, inspect returning headers, log all calls

            // 2
            // ReUse tokens

            // 3
            // Arrays-asList for UpdateActions

            // 4
            // Decorate client for X-Correlation-ID
        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));
        String clientId = prop.getProperty(MH_DEV_ADMIN + "clientId");
        String clientSecret = prop.getProperty(MH_DEV_ADMIN + "clientSecret");

        try (ApiHttpClient correlationIdApiHttpClient = defaultClient(
                new VrapOkHttpClient(),
                ClientCredentials.of().withClientId(clientId).withClientSecret(clientSecret).build(),
                ServiceRegion.GCP_EUROPE_WEST1.getOAuthTokenUrl(),
                ServiceRegion.GCP_EUROPE_WEST1.getApiUrl(),
                new ArrayList<>(Arrays.asList(
                        (request, next) -> {
                            request.withHeader(ApiHttpHeaders.X_CORRELATION_ID, projectKey + "/" + UUID.randomUUID().toString());
                            return next.apply(request);
                        },
                        (request, next) -> next.apply(request).whenComplete((response, throwable) -> {
                            if (throwable.getCause() instanceof ApiHttpException) {
                                logger.info(((ApiHttpException)throwable.getCause()).getHeaders().getFirst(ApiHttpHeaders.X_CORRELATION_ID));
                            } else {
                                logger.info(response.getHeaders().getFirst(ApiHttpHeaders.X_CORRELATION_ID));
                            }
                        })
                ))
        )) {
            final ApiRoot correlationIdClient = create(() -> correlationIdApiHttpClient);
        }

        // or per request
        try (ApiHttpClient apiHttpClient = ClientService.apiHttpClient) {
            final ApiHttpResponse<Project> projectApiHttpResponse = client
                    .withProjectKey(projectKey)
                    .get()
                    .withHeader(ApiHttpHeaders.X_CORRELATION_ID, UUID.randomUUID().toString())
                    .execute()
                    .get();
        }

        // 5
            // Simulate failover, 5xx errors


        try (ApiHttpClient retryHttpClient = defaultClient(
                new VrapOkHttpClient(),
                ClientCredentials.of().withClientId(clientId).withClientSecret(clientSecret).build(),
                ServiceRegion.GCP_EUROPE_WEST1.getOAuthTokenUrl(),
                ServiceRegion.GCP_EUROPE_WEST1.getApiUrl(),
                new ArrayList<>(Collections.singletonList(
                        new RetryMiddleware(3, Arrays.asList(500, 503))
                ))
        )) {
            final ApiRoot retryClient = create(() -> retryHttpClient);
        }
    }
}
