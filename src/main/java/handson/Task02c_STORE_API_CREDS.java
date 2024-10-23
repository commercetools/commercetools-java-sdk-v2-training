package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.fasterxml.jackson.core.JsonProcessingException;
import handson.impl.ApiPrefixHelper;
import io.vrap.rmf.base.client.utils.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getProjectKey;

public class Task02c_STORE_API_CREDS {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();
        try (ProjectApiRoot apiRoot = createApiClient(apiClientPrefix)) {
            Logger logger = LoggerFactory.getLogger("commercetools");

            final String projectKey = getProjectKey(apiClientPrefix);
            
            final String storeKey = "nagesh-store";
            
            final String scopeString = "manage_discount_codes:projectKey manage_states:projectKey manage_customers:projectKey:storeKey manage_categories:projectKey manage_cart_discounts:projectKey:storeKey manage_import_containers:projectKey manage_tax_categories:projectKey manage_product_selections:projectKey manage_payments:projectKey view_project_settings:projectKey manage_types:projectKey manage_customer_groups:projectKey view_shipping_methods:projectKey manage_connectors_deployments:projectKey manage_stores:projectKey view_published_products:projectKey manage_order_edits:projectKey manage_connectors:projectKey manage_extensions:projectKey manage_orders:projectKey:storeKey manage_subscriptions:projectKey";

            final String scope = scopeString.replaceAll("projectKey", projectKey)
                    .replaceAll("storeKey", storeKey);


          // TODO: Create store api client
          //
            apiRoot.apiClients()
                    .post(
                            apiClientDraftBuilder -> apiClientDraftBuilder
                                    .name("store client " + System.nanoTime())
                                    .scope(scope)
                    )
                    .execute()
                    .thenAccept(
                            apiClientApiHttpResponse ->
                            {
                                try {
                                    System.out.println(JsonUtils.prettyPrint(JsonUtils.toJsonString(apiClientApiHttpResponse.getBody())));
                                } catch (JsonProcessingException ignored) {}
                            }
                    )
                    .exceptionally(throwable -> {
                        logger.error("Exception: {}", throwable.getMessage());
                        return null;
                    }).join();
        }
    }
}

// SUGGESTED SCOPE for DEV ADMIN:
    //     manage_categories:project-key
    //     view_shipping_methods:project-key
    //     manage_types:project-key
    //     view_published_products:project-key
    //     manage_payments:project-key
    //     manage_states:project-key
    //     manage_tax_categories:project-key
    //     view_standalone_prices:project-key
    //     manage_import_containers:project-key
    //     view_project_settings:project-key
    //     manage_discount_codes:project-key
    //     manage_subscriptions:project-key
    //     manage_order_edits:project-key
    //     manage_customer_groups:project-key
    //     manage_extensions:project-key
    //     manage_connectors:project-key
    //     manage_product_selections:project-key
    //     manage_stores:project-key
    //     manage_connectors_deployments:project-key

    //     manage_orders:project-key:boston-store
    //     manage_customers:project-key:boston-store
    //     manage_cart_discounts:project-key:boston-store