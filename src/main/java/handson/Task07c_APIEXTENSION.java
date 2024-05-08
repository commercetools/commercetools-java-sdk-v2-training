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
                    .key("mhCustomerBlocker")
                    .destination(
                        // for GCP Cloud functions
                        HttpDestinationBuilder.of()
                            .url("https://europe-west3-ct-support.cloudfunctions.net/training-extensions-sample")
                            .build()
                        //for AWS Lambda functions
//                        ExtensionAWSLambdaDestinationBuilder.of()
//                            .arn("arn:aws:lambda:eu-central-1:923270384842:function:training-customer-check")
//                            .accessKey("AKIAJLJRDGBNBIPY2ZHQ")
//                            .accessSecret("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")
//                            .build()
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

