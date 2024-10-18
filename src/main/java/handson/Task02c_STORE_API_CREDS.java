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

public class Task02c_STORE_API_CREDS {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();
        try (ProjectApiRoot apiRoot = createApiClient(apiClientPrefix)) {
            Logger logger = LoggerFactory.getLogger("commercetools");


          // TODO: Create store api client
          //
            apiRoot.apiClients()
                    .post(
                            apiClientDraftBuilder -> apiClientDraftBuilder
                                    .name("nagesh store client test")
                                    .scope("manage_discount_codes:training-dev-dryrun1-20241017 manage_states:training-dev-dryrun1-20241017 manage_customers:training-dev-dryrun1-20241017:nagesh-store manage_categories:training-dev-dryrun1-20241017 manage_cart_discounts:training-dev-dryrun1-20241017:nagesh-store manage_import_containers:training-dev-dryrun1-20241017 manage_tax_categories:training-dev-dryrun1-20241017 manage_product_selections:training-dev-dryrun1-20241017 manage_payments:training-dev-dryrun1-20241017 view_project_settings:training-dev-dryrun1-20241017 manage_types:training-dev-dryrun1-20241017 manage_customer_groups:training-dev-dryrun1-20241017 view_shipping_methods:training-dev-dryrun1-20241017 manage_connectors_deployments:training-dev-dryrun1-20241017 manage_stores:training-dev-dryrun1-20241017 view_published_products:training-dev-dryrun1-20241017 manage_order_edits:training-dev-dryrun1-20241017 manage_connectors:training-dev-dryrun1-20241017 manage_extensions:training-dev-dryrun1-20241017 manage_orders:training-dev-dryrun1-20241017:nagesh-store manage_subscriptions:training-dev-dryrun1-20241017")
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

