package handson.impl;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.defaultconfig.ApiFactory;
import com.commercetools.api.defaultconfig.ServiceRegion;
import com.commercetools.importapi.defaultconfig.ImportApiFactory;
import com.commercetools.ml.defaultconfig.MLApiRootFactory;
import io.vrap.rmf.base.client.ApiHttpClient;

import io.vrap.rmf.base.client.ClientFactory;
import io.vrap.rmf.base.client.VrapHttpClient;
import io.vrap.rmf.base.client.oauth2.ClientCredentials;
import io.vrap.rmf.base.client.oauth2.GlobalCustomerPasswordTokenSupplier;
import io.vrap.rmf.impl.okhttp.VrapOkhttpClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class ClientService {

    public static ApiHttpClient apiHttpClient;

    // TODO: Add the Constant-Token Client

    /**
     * @throws IOException
     */
    public static ApiRoot createApiClient(final String prefix) throws IOException {

        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));

        String clientId = prop.getProperty(prefix + "clientId");
        String clientSecret = prop.getProperty(prefix + "clientSecret");

        apiHttpClient = ApiFactory.defaultClient(
                ClientCredentials.of().withClientId(clientId).withClientSecret(clientSecret).build(),
                ServiceRegion.GCP_EUROPE_WEST1.getOAuthTokenUrl(),
                ServiceRegion.GCP_EUROPE_WEST1.getApiUrl(),
                new ArrayList<>()
        );
        return ApiFactory.create(() -> apiHttpClient);
    }

    public static String getProjectKey(final String prefix) throws IOException {
        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));

        return prop.getProperty(prefix + "projectKey");
    }

    public static String getStoreKey(final String prefix) throws IOException {
        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));

        return prop.getProperty(prefix + "storeKey");
    }

    public static String getCustomerEmail(final String prefix) throws IOException {
        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));

        return prop.getProperty(prefix + "customerEmail");
    }

    public static String getCustomerPassword(final String prefix) throws IOException {
        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));

        return prop.getProperty(prefix + "customerPassword");
    }

    /**
     * @return
     * @throws IOException
     */
    public static com.commercetools.importapi.client.ApiRoot createImportApiClient(final String prefix) throws IOException {

        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));
        String clientId = prop.getProperty(prefix + "clientId");
        String clientSecret = prop.getProperty(prefix + "clientSecret");

        return ImportApiFactory.create(
                ClientCredentials.of().withClientId(clientId).withClientSecret(clientSecret).build(),
                com.commercetools.importapi.defaultconfig.ServiceRegion.GCP_EUROPE_WEST1.getOAuthTokenUrl(),
                com.commercetools.importapi.defaultconfig.ServiceRegion.GCP_EUROPE_WEST1.getApiUrl()
        );
    }

    public static ApiRoot createStoreApiClient(final String prefix) throws IOException {

        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));
        String projectKey = prop.getProperty(prefix + "projectKey");
        String storeKey = prop.getProperty(prefix + "storeKey");
        String storeCustomerEmail = prop.getProperty(prefix + "customerEmail");
        String storeCustomerPassword = prop.getProperty(prefix + "customerPassword");

        VrapHttpClient vrapHttpClient = new VrapOkhttpClient();

        final ApiHttpClient apiHttpClient = ClientFactory.create(
                "https://api.europe-west1.gcp.commercetools.com/",
                vrapHttpClient,
                new GlobalCustomerPasswordTokenSupplier(
                        prop.getProperty(prefix + "clientId"),
                        prop.getProperty(prefix + "clientSecret"),
                        storeCustomerEmail,
                        storeCustomerPassword,
                        prop.getProperty(prefix + "scopes"),
                        "https://auth.europe-west1.gcp.commercetools.com/oauth/" + projectKey + "/in-store/key=" + storeKey + "/customers/token"
                        , vrapHttpClient
                ));
        return
                ApiRoot.fromClient(apiHttpClient);

    }

    public static ApiRoot createMeTokenApiClient(final String prefix) throws IOException {

        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));
        String projectKey = prop.getProperty(prefix + "projectKey");
        String customerEmail = prop.getProperty(prefix + "customerEmail");
        String customerPassword = prop.getProperty(prefix + "customerPassword");

        VrapHttpClient vrapHttpClient = new VrapOkhttpClient();

        final ApiHttpClient apiHttpClient = ClientFactory.create(
                "https://api.europe-west1.gcp.commercetools.com/",
                vrapHttpClient,
                new GlobalCustomerPasswordTokenSupplier(
                        prop.getProperty(prefix + "clientId"),
                        prop.getProperty(prefix + "clientSecret"),
                        customerEmail,
                        customerPassword,
                        prop.getProperty(prefix + "scopes"),
                        "https://auth.europe-west1.gcp.commercetools.com/oauth/" + projectKey + "/customers/token"
                        , vrapHttpClient
                ));
        return
                ApiRoot.fromClient(apiHttpClient);

    }





    public static Object createConstantTokenApiClient(final String prefix, String token) throws IOException {

        // checkout out comments from Jens


        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));

        return
                null;
    }



    /**
     * @return
     * @throws IOException
     */
    public static com.commercetools.ml.client.ApiRoot createMLApiClient(final String prefix) throws IOException {

        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));
        String clientId = prop.getProperty(prefix + "clientId");
        String clientSecret = prop.getProperty(prefix + "clientSecret");

        return
                MLApiRootFactory.create(
                        ClientCredentials.of().withClientId(clientId).withClientSecret(clientSecret).build(),
                        com.commercetools.ml.defaultconfig.ServiceRegion.GCP_EUROPE.getOAuthTokenUrl(),
                        com.commercetools.ml.defaultconfig.ServiceRegion.GCP_EUROPE.getApiUrl()
                );
    }

}
