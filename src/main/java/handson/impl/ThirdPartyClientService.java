package handson.impl;

import io.vrap.rmf.base.client.VrapHttpClient;
import io.vrap.rmf.base.client.oauth2.ClientCredentialsTokenSupplier;
import io.vrap.rmf.okhttp.VrapOkHttpClient;

import java.util.concurrent.ExecutionException;

public class ThirdPartyClientService {


    public String createClientAndFetchToken(String clientId, String clientSecret, String projectKey) {

        VrapHttpClient client = new VrapOkHttpClient();

        String tokenEndpoint = "https://auth.europe-west1.gcp.commercetools.com/oauth/token";
        ClientCredentialsTokenSupplier clientCredentialsTokenSupplier =
                new ClientCredentialsTokenSupplier(clientId, clientSecret, null, tokenEndpoint, client);
        try {
            return clientCredentialsTokenSupplier.getToken().get().getAccessToken();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Exception" + e.toString());
        }
        return "";
    }


    public String createClientAndFetchMeToken(String clientId, String clientSecret, String projectKey, String customerEmail, String customerLogon)
    {
//        VrapHttpClient client = new VrapOkHttpClient();
//
//        String tokenEndpoint = "https://auth.europe-west1.gcp.commercetools.com/oauth/" + projectKey +"/token";
//        AnonymousSessionTokenSupplier clientCredentialsTokenSupplier =
//                new AnonymousSessionTokenSupplier(clientId, clientSecret, null, tokenEndpoint, client);
//        try {
//            return clientCredentialsTokenSupplier.getToken().get().getAccessToken();
//        } catch (InterruptedException | ExecutionException e) {
//            System.out.println("Exception" + e.toString());
//        }
        return "";
    }

}
