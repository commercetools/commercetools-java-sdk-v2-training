package handson.impl;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.defaultconfig.ApiRootBuilder;
import com.commercetools.api.defaultconfig.ServiceRegion;
import com.commercetools.importapi.defaultconfig.ImportApiRootBuilder;
import io.vrap.rmf.base.client.AuthenticationToken;
import io.vrap.rmf.base.client.HttpClientSupplier;
import io.vrap.rmf.base.client.http.ErrorMiddleware;
import io.vrap.rmf.base.client.oauth2.ClientCredentials;
import io.vrap.rmf.base.client.oauth2.ClientCredentialsTokenSupplier;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ClientService {

    public static ProjectApiRoot projectApiRoot;
    public static com.commercetools.importapi.client.ProjectApiRoot importApiRoot;

    /**
     * @throws IOException exception
     */
    public static ProjectApiRoot createApiClient(final String prefix) throws IOException {

        // TODO: create project api root using client credentials flow
        return projectApiRoot;
    }


    public static String getProjectKey(final String prefix) throws IOException {
        Properties props = new Properties();
        props.load(ClientService.class.getResourceAsStream("/dev.properties"));
        return props.getProperty(prefix + "projectKey");
    }

    public static String getClientId(final String prefix) throws IOException {
        Properties props = new Properties();
        props.load(ClientService.class.getResourceAsStream("/dev.properties"));
        return props.getProperty(prefix + "clientId");
    }


    public static String getClientSecret(final String prefix) throws IOException {
        Properties props = new Properties();
        props.load(ClientService.class.getResourceAsStream("/dev.properties"));
        return props.getProperty(prefix + "clientSecret");
    }


    public static String getStoreKey(final String prefix) throws IOException {
        Properties props = new Properties();
        props.load(ClientService.class.getResourceAsStream("/dev.properties"));
        return props.getProperty(prefix + "storeKey");
    }


    public static String getCustomerEmail(final String prefix) throws IOException {
        Properties props = new Properties();
        props.load(ClientService.class.getResourceAsStream("/dev.properties"));
        return props.getProperty(prefix + "customerEmail");
    }


    /**
     * @return apiRoot
     * @throws IOException exception
     */
    public static com.commercetools.importapi.client.ProjectApiRoot createImportApiClient(final String prefix) throws IOException {

        Properties props = new Properties();
        props.load(ClientService.class.getResourceAsStream("/dev.properties"));

        String clientId = props.getProperty(prefix + "clientId");
        String clientSecret = props.getProperty(prefix + "clientSecret");
        String projectKey = props.getProperty(prefix + "projectKey");

        importApiRoot = ImportApiRootBuilder.of().defaultClient(
                ClientCredentials.of()
                        .withClientId(clientId)
                        .withClientSecret(clientSecret)
                        .build(),
                com.commercetools.importapi.defaultconfig.ServiceRegion.GCP_EUROPE_WEST1.getOAuthTokenUrl(),
                com.commercetools.importapi.defaultconfig.ServiceRegion.GCP_EUROPE_WEST1.getApiUrl()
            )
            .build(projectKey);

        return importApiRoot;
    }


    public static AuthenticationToken getTokenForClientCredentialsFlow(final String prefix) throws IOException {

        Properties props = new Properties();
        props.load(ClientService.class.getResourceAsStream("/dev.properties"));

        String clientId = props.getProperty(prefix + "clientId");
        String clientSecret = props.getProperty(prefix + "clientSecret");
        AuthenticationToken token = null;
        try (final ClientCredentialsTokenSupplier clientCredentialsTokenSupplier = new ClientCredentialsTokenSupplier(
            clientId,
            clientSecret,
            null,
            ServiceRegion.GCP_EUROPE_WEST1.getOAuthTokenUrl(),
            HttpClientSupplier.of().get()
        )) {
            token = clientCredentialsTokenSupplier.getToken().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return token;
    }

}
