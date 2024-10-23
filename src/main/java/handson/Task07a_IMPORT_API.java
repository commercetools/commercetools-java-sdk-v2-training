package handson;


import com.commercetools.importapi.client.ProjectApiRoot;
import com.commercetools.importapi.models.importsummaries.OperationStates;
import handson.impl.ApiPrefixHelper;
import handson.impl.ImportService;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createImportApiClient;


public class Task07a_IMPORT_API {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        // TODO
        //  Update your prefix for an Import Api Client in the PrefixHelper
        //  Provide a container key
        //
        final String apiImportClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();
        try (ProjectApiRoot apiRoot = createImportApiClient(apiImportClientPrefix)) {
            Logger logger = LoggerFactory.getLogger("commercetools");

            final ImportService importService = new ImportService(apiRoot);

            final String containerKey = "boston-store-customers";

            // TODO:  CREATE an import container
            //
            importService.createImportContainer(containerKey)
                    .thenAccept(importContainerApiHttpResponse ->
                        logger.info("Created import container {} ", importContainerApiHttpResponse.getBody().getKey())
                    )
                    .exceptionally(throwable -> {
                            logger.error("Exception: {}", throwable.getMessage());
                            return null;
                        }).join();

//            // TODO: CREATE a customers import request
//            //
//            importService.importCustomersFromCsv(
//                containerKey,
//                "customers.csv"
//            )
//                .thenApply(ApiHttpResponse::getBody)
//                .thenAccept(response -> {
//                            logger.info("Importing {} customer(s) ", response.getOperationStatus().size());
//                        }
//                )
//                .exceptionally(throwable -> {
//                    logger.error("Exception: {}", throwable.getMessage());
//                    return null;
//                }).join();

//            // TODO:  CHECK the status of your import requests
//            //
//            importService.getImportContainerSummary(containerKey)
//                    .thenAccept(importSummaryApiHttpResponse -> {
//                            OperationStates states = importSummaryApiHttpResponse.getBody().getStates();
//                            logger.info("Processing: {} Imported: {} Unresolved: {} Rejected: {} Validation Failed: {}",
//                                    states.getProcessing(),
//                                    states.getImported(),
//                                    states.getUnresolved(),
//                                    states.getRejected(),
//                                    states.getValidationFailed()
//                            );
//                        }
//                    )
//                    .exceptionally(throwable -> {
//                        logger.error("Exception: {}", throwable.getMessage());
//                        return null;
//                    }).join();
        }
    }
}

