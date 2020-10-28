package handson.impl;

import com.commercetools.api.defaultconfig.ServiceRegion;
import io.vrap.rmf.base.client.VrapHttpClient;
import io.vrap.rmf.base.client.oauth2.AnonymousSessionTokenSupplier;
import io.vrap.rmf.base.client.oauth2.ClientCredentialsTokenSupplier;
import io.vrap.rmf.base.client.oauth2.GlobalCustomerPasswordTokenSupplier;
import io.vrap.rmf.base.client.oauth2.TokenSupplier;
import io.vrap.rmf.okhttp.VrapOkHttpClient;

import java.util.concurrent.ExecutionException;

public class ThirdPartyClientService {


    public static final VrapOkHttpClient CLIENT = new VrapOkHttpClient();

    public String createClientAndFetchToken(String clientId, String clientSecret) {

        TokenSupplier clientCredentialsTokenSupplier =
                new ClientCredentialsTokenSupplier(clientId, clientSecret, null, ServiceRegion.GCP_EUROPE_WEST1.getOAuthTokenUrl(), CLIENT);
        try {
            return clientCredentialsTokenSupplier.getToken().get().getAccessToken();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Exception" + e.toString());
        }
        return "";
    }


    public String createClientAndFetchAnonMeToken(String clientId, String clientSecret, String projectKey)
    {
        TokenSupplier passwordTokenSupplier =
                new AnonymousSessionTokenSupplier(clientId, clientSecret, null,ServiceRegion.GCP_EUROPE_WEST1.getAuthUrl() + "/oauth" + projectKey + "/anonymous/token", CLIENT);
        try {
            return passwordTokenSupplier.getToken().get().getAccessToken();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Exception" + e.toString());
        }
        return "";
    }

    public String createClientAndFetchMeToken(String clientId, String clientSecret, String projectKey, String customerEmail, String customerLogon)
    {
        TokenSupplier passwordTokenSupplier =
                new GlobalCustomerPasswordTokenSupplier(clientId, clientSecret, customerEmail, customerLogon,  null,ServiceRegion.GCP_EUROPE_WEST1.getAuthUrl() + "/oauth" + projectKey + "/customers/token", CLIENT);
        try {
            return passwordTokenSupplier.getToken().get().getAccessToken();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Exception" + e.toString());
        }
        return "";
    }
}
