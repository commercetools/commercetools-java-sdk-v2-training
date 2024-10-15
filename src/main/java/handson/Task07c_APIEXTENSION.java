package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.extension.*;
import handson.impl.ApiPrefixHelper;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;


public class Task07c_APIEXTENSION {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        Logger logger = LoggerFactory.getLogger("commercetools");

        final ProjectApiRoot client = createApiClient(apiClientPrefix);

        client
            .extensions()
            .post(
                extensionDraftBuilder -> extensionDraftBuilder
                    .key("mhExtension")
                    .destination(
                        // for GCP Cloud functions
                        HttpDestinationBuilder.of()
                            .url("https://europe-west3-ct-support.cloudfunctions.net/training-extensions-sample")
                            .build()
                    )
                    .addTriggers(extensionTriggerBuilder -> extensionTriggerBuilder
                        .resourceTypeId(ExtensionResourceTypeId.ORDER)
                        .actions(
                            ExtensionAction.CREATE
                        )
                        .build()
                    )
            ).execute()
            .thenApply(ApiHttpResponse::getBody)
            .handle((extension, exception) -> {
                if (exception == null) {
                    logger.info("API Extension ID: " + extension.getId());
                    return extension;
                }
                logger.error("Exception: " + exception.getMessage());
                return null;
            }).thenRun(() -> client.close());
    }
}

