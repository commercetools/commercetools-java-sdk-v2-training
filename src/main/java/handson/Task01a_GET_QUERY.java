package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.project.Project;
import com.commercetools.api.models.tax_category.TaxCategory;
import com.commercetools.api.models.tax_category.TaxCategoryPagedQueryResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import handson.impl.ApiPrefixHelper;
import handson.impl.ClientService;
import io.vrap.rmf.base.client.ApiHttpResponse;
import io.vrap.rmf.base.client.utils.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static handson.impl.ClientService.createApiClient;


/**
 * Configure client and get project information.
 *
 * See:
 *  TODO dev.properties
 *  TODO {@link ClientService#createApiClient(String prefix)}
 */
public class Task01a_GET_QUERY {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

            final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

            try (ProjectApiRoot apiRoot = createApiClient(apiClientPrefix)) {

                Logger logger = LoggerFactory.getLogger("commercetools");

                // TODO: GET project info
                //

//                Project project = client.get().executeBlocking().getBody();
//                logger.info("Project key: {}", project.getKey());

//                // TODO: GET tax categories
//                //
//
//                TaxCategoryPagedQueryResponse taxCategoryPagedQueryResponse = client.taxCategories().get().executeBlocking().getBody();
//                if (taxCategoryPagedQueryResponse != null && taxCategoryPagedQueryResponse.getResults() != null) {
//                    logger.info("Tax categories: {}",
//                            taxCategoryPagedQueryResponse.getResults().stream().map(TaxCategory::getKey).collect(Collectors.toList())
//                    );
//                } else {
//                    logger.warn("No tax categories found.");
//                }
//
                // TODO Get Tax category by Key
                //
                apiRoot.taxCategories()
                        .withKey("standard-tax")
                        .get()
                        .execute()
                        .thenApply(ApiHttpResponse::getBody)
                        .thenAccept(taxCategory -> {
                            logger.info("Tax category ID: {}", taxCategory.getId());
                            try {
                                System.out.println(JsonUtils.prettyPrint(JsonUtils.toJsonString(taxCategory)));
                            } catch (JsonProcessingException ignored) { }
                        })
                        .exceptionally(throwable -> {
                            logger.error("Exception: {}", throwable.getMessage());
                            return null;
                        }).join();
            }
    }
}
