package handson;


import com.commercetools.importapi.client.ProjectApiRoot;
import com.commercetools.importapi.models.common.Money;
import com.commercetools.importapi.models.common.MoneyBuilder;
import com.commercetools.importapi.models.importsummaries.OperationStates;
import handson.impl.ApiPrefixHelper;
import handson.impl.ImportService;
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
        final String containerKey = "berlin-store-prices";

        Logger logger = LoggerFactory.getLogger(Task02b_UPDATE_Group.class.getName());
        final ProjectApiRoot client = createImportApiClient(apiImportClientPrefix);
        final ImportService importService = new ImportService(client);


        // TODO
        //  CREATE an import container
        //  CREATE a price import request
        //  CHECK the status of your import requests
        //
        logger.info("Created import container {} ",
                importService.createImportContainer(containerKey)
                        .toCompletableFuture().get()
        );

        // TODO
        Money amount = null;

        logger.info("Created price resource {} ",
                importService.createPriceImportRequest(containerKey,"tulip-seed-product","tulip-seed-box", amount)
                        .toCompletableFuture().get()
        );

        logger.info("Total containers in our project: {}",
                client
                        .importContainers()
                        .get()
                        .execute()
                        .toCompletableFuture().get()
                        .getBody().getTotal()
        );
        OperationStates states = client
                .importContainers()
                .withImportContainerKeyValue(containerKey)
                .importSummaries()
                .get()
                .execute()
                .toCompletableFuture().get()
                .getBody().getStates();
        logger.info("Processing: {} Imported: {} Unresolved: {} ",states.getProcessing(),states.getImported(),states.getUnresolved());

        client.close();
    }

}

