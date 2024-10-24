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
            
            final String storeKey = "your-store-key";
            
            final String scopeString = "manage_discount_codes:projectKey manage_states:projectKey manage_customers:projectKey:storeKey manage_categories:projectKey manage_cart_discounts:projectKey:storeKey manage_import_containers:projectKey manage_tax_categories:projectKey manage_product_selections:projectKey manage_payments:projectKey view_project_settings:projectKey manage_types:projectKey manage_customer_groups:projectKey view_shipping_methods:projectKey manage_connectors_deployments:projectKey manage_stores:projectKey view_published_products:projectKey manage_order_edits:projectKey manage_connectors:projectKey manage_extensions:projectKey manage_orders:projectKey:storeKey manage_subscriptions:projectKey";

            final String scope = scopeString.replaceAll("projectKey", projectKey)
                    .replaceAll("storeKey", storeKey);

            // TODO: Create store api client
            //

        }
    }
}
