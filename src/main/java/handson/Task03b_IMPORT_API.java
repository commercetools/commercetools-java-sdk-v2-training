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


public class Task03b_IMPORT_API {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        // TODO
        //  Update your prefix for an Import Api Client in the PrefixHelper
        //  Provide a container key
        //
        final String apiImportClientPrefix = ApiPrefixHelper.API_DEV_IMPORT_PREFIX.getPrefix();
        final String containerKey = "mh-berlin-store-prices";

        Logger logger = LoggerFactory.getLogger(Task02b_UPDATE_Group.class.getName());
        final ProjectApiRoot client = createImportApiClient(apiImportClientPrefix);
        final ImportService importService = new ImportService(client);


        // TODO
        //  CREATE an import container
        //  CREATE a price import request
        //  CHECK the status of your import requests

        importService.createImportContainer(containerKey)
                .thenApply(ApiHttpResponse::getBody)
                .handle((importContainer, exception) -> {
                    if (exception == null) {
                        logger.info("Created import container {} ", importContainer.getKey());
                        return importContainer;
                    };
                    logger.error("Exception: " + exception.getMessage());
                    return null;
                }).thenRun(() -> client.close());

//        Money amount = MoneyBuilder.of()
//                .currencyCode("EUR")
//                .centAmount(3412L)
//                .build();
//
//
//        importService.createPriceImportRequest(
//                containerKey,
//                "tulip-seed-product",
//                "tulip-seed-box",
//                "TulipSeed01Price01",
//                amount
//            )
//            .thenApply(ApiHttpResponse::getBody)
//            .handle((response, exception) -> {
//                if (exception == null) {
//                    logger.info("Importing {} price(s) ", response.getOperationStatus().size());
//                    return response;
//                };
//                logger.error("Exception: " + exception.getMessage());
//                return null;
//            }).thenRun(() -> client.close());



//        client
//                .importContainers()
//                .get().execute()
//                .thenApply(ApiHttpResponse::getBody)
//                .handle((response, exception) -> {
//                    if (exception == null) {
//                        logger.info("Total containers in our project: {}", response.getTotal());
//                        return response;
//                    };
//                    logger.error("Exception: " + exception.getMessage());
//                    return null;
//                }).thenRun(() -> client.close());

//        client
//                .importContainers().withImportContainerKeyValue(containerKey)
//                .importSummaries()
//                .get().execute()
//                .thenApply(ApiHttpResponse::getBody)
//                .handle((importSummary, exception) -> {
//                        if (exception == null) {
//                            OperationStates states = importSummary.getStates();
//                            logger.info("Processing: {} Imported: {} Unresolved: {} ",
//                                    states.getProcessing(),
//                                    states.getImported(),
//                                    states.getUnresolved()
//                            );
//                            return importSummary;
//                        };
//                        logger.error("Exception: " + exception.getMessage());
//                        return null;
//                }).thenRun(() -> client.close());
    }
}

