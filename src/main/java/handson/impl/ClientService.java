package handson.impl;

//// import com.commercetools.importer.client.ApiRoot;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.defaultconfig.ApiFactory;
import com.commercetools.api.defaultconfig.ServiceRegion;
import com.commercetools.importapi.defaultconfig.ImportApiFactory;
import com.commercetools.ml.defaultconfig.MLApiRootFactory;
import io.vrap.rmf.base.client.ApiHttpClient;
import io.vrap.rmf.base.client.AuthenticationToken;
import io.vrap.rmf.base.client.http.*;
import io.vrap.rmf.base.client.oauth2.ClientCredentials;
import io.vrap.rmf.base.client.oauth2.TokenSupplier;

import java.io.IOException;
import java.util.Properties;

public class ClientService {


    // TODO: Add the Constant-Token Client

    /**
     * @throws IOException
     */
    public static ApiRoot createApiClient(final String prefix) throws IOException {

        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));

        String clientId = prop.getProperty(prefix + "clientId");
        String clientSecret = prop.getProperty(prefix + "clientSecret");
//        String scopes = prop.getProperty(prefix + "scopes");

        return ApiFactory.create(
                ClientCredentials.of().withClientId(clientId).withClientSecret(clientSecret).build(),
                ServiceRegion.GCP_EUROPE_WEST1.getOAuthTokenUrl(),
                ServiceRegion.GCP_EUROPE_WEST1.getApiUrl()
        );
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
        String scopes = prop.getProperty(prefix + "scopes");

        return ImportApiFactory.create(
                ClientCredentials.of().withClientId(clientId).withClientSecret(clientSecret).withScopes(scopes).build(),
                prop.getProperty(prefix + "authUrl"),
                prop.getProperty(prefix + "apiUrl")
        );
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
        String scopes = prop.getProperty(prefix + "scopes");

        return
                MLApiRootFactory.create(
                        ClientCredentials.of().withClientId(clientId).withClientSecret(clientSecret).withScopes(scopes).build(),
                        prop.getProperty(prefix + "authUrl"),
                        prop.getProperty(prefix + "apiUrl")
                );
    }

    public static Object createConstantTokenApiClient(final String prefix, String token) throws IOException {

        // checkout out comments from Jens


        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));

        return
                null;
    }


    public static Object createMeTokenApiClient(final String prefix, String token) throws IOException {

        // checkout out comments from Jens

        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));

        return
                null;
    }


}