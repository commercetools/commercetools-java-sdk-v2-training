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

    public String createClientAndFetchToken(String clientId, String clientSecret) {

        try (ClientCredentialsTokenSupplier clientCredentialsTokenSupplier = new ClientCredentialsTokenSupplier(
                clientId, clientSecret, null, ServiceRegion.GCP_EUROPE_WEST1.getOAuthTokenUrl(), new VrapOkHttpClient())
        ) {
            return clientCredentialsTokenSupplier.getToken().get().getAccessToken();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Exception" + e.toString());
        }
        return "";
    }


    public String createClientAndFetchAnonMeToken(String clientId, String clientSecret, String projectKey)
    {
        try (AnonymousSessionTokenSupplier passwordTokenSupplier =
                     new AnonymousSessionTokenSupplier(clientId, clientSecret, null,ServiceRegion.GCP_EUROPE_WEST1.getAuthUrl() + "/oauth" + projectKey + "/anonymous/token", new VrapOkHttpClient())){
            return passwordTokenSupplier.getToken().get().getAccessToken();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Exception" + e.toString());
        }
        return "";
    }

    public String createClientAndFetchMeToken(String clientId, String clientSecret, String projectKey, String customerEmail, String customerLogon)
    {
        try (GlobalCustomerPasswordTokenSupplier passwordTokenSupplier =
                     new GlobalCustomerPasswordTokenSupplier(clientId, clientSecret, customerEmail, customerLogon,  null,ServiceRegion.GCP_EUROPE_WEST1.getAuthUrl() + "/oauth" + projectKey + "/customers/token", new VrapOkHttpClient())){
            return passwordTokenSupplier.getToken().get().getAccessToken();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Exception" + e.toString());
        }
        return "";
    }
}
