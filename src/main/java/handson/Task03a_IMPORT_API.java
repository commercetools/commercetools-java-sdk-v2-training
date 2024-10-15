package handson;


import com.commercetools.importapi.client.ProjectApiRoot;
import com.commercetools.importapi.models.common.Money;
import com.commercetools.importapi.models.common.MoneyBuilder;
import com.commercetools.importapi.models.importsummaries.OperationStates;
import handson.impl.ApiPrefixHelper;
import handson.impl.ImportService;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createImportApiClient;


public class Task03a_IMPORT_API {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        // TODO
        //  Update your prefix for an Import Api Client in the PrefixHelper
        //  Provide a container key
        //
        final String apiImportClientPrefix = ApiPrefixHelper.API_DEV_IMPORT_PREFIX.getPrefix();
        try (ProjectApiRoot client = createImportApiClient(apiImportClientPrefix)) {
            Logger logger = LoggerFactory.getLogger("commercetools");

            final String containerKey = "nd-berlin-store-prices";

            final ImportService importService = new ImportService(client);

            // TODO
            //  CREATE an import container
            //  CREATE a customers import request
            //  CHECK the status of your import requests

            importService.createImportContainer(containerKey)
                .thenApply(ApiHttpResponse::getBody)
                    .thenAccept(importContainer -> {
                        logger.info("Created import container {} ", importContainer.getKey());
                    }
                )
                .exceptionally(throwable -> {
                        logger.error("Exception: {}", throwable.getMessage());
                        return null;
                    }).join();


            importService.importCustomersFromCsv(
                containerKey,
                "customers.csv"
            )
                .thenApply(ApiHttpResponse::getBody)
                .thenAccept(response -> {
                            logger.info("Importing {} customer(s) ", response.getOperationStatus().size());
                        }
                )
                .exceptionally(throwable -> {
                    logger.error("Exception: {}", throwable.getMessage());
                    return null;
                }).join();


            client
                .importContainers()
                .get().execute()
                .thenAccept(responseApiHttpResponse -> {
                        logger.info("Total containers in our project: {}", responseApiHttpResponse.getBody().getTotal());
                    }
                )
                .exceptionally(throwable -> {
                    logger.error("Exception: {}", throwable.getMessage());
                    return null;
                }).join();

                client
                        .importContainers().withImportContainerKeyValue(containerKey)
                        .importSummaries()
                        .get().execute()
                        .thenAccept(importSummaryApiHttpResponse -> {
                                OperationStates states = importSummaryApiHttpResponse.getBody().getStates();
                                logger.info("Processing: {} Imported: {} Unresolved: {} Rejected: {} Validation Failed: {}",
                                        states.getProcessing(),
                                        states.getImported(),
                                        states.getUnresolved(),
                                        states.getRejected(),
                                        states.getValidationFailed()
                                );
                            }
                        )
                        .exceptionally(throwable -> {
                            logger.error("Exception: {}", throwable.getMessage());
                            return null;
                        }).join();

        }
    }
}

