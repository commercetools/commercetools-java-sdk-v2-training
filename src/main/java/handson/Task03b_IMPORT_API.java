package handson;


import com.commercetools.importapi.client.ProjectApiRoot;
import com.commercetools.importapi.models.common.Money;
import com.commercetools.importapi.models.common.MoneyBuilder;
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
        //  Provide a price sink key
        //
        final String apiImportClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();
        final String containerKey = "berlin-store-prices";

        Logger logger = LoggerFactory.getLogger(Task02b_UPDATE_Group.class.getName());
        final ProjectApiRoot client = createImportApiClient(apiImportClientPrefix);
        final ImportService importService = new ImportService(client);


        // TODO
        //  CREATE a price import sink
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

            logger.info("Summary report on all containers on our project: {}",
                    client
                            .importC
                            .get()
                            .execute()
                            .toCompletableFuture().get()
                            .getBody().getResults().get(0).getResourceType()
            );
            logger.info("Report on all queued import operations on our import container {} ",
                    client
                            .importContainers()
                            .withImportContainerKeyValue(containerKey)
                            .importSummaries()
                            .get()
                            .execute()
                            .toCompletableFuture().get()
                            .getBody().getStates().getImported()
            );
        }

    }
}

