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


public class Task01a_GET_QUERY {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

            final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

            try (ProjectApiRoot client = createApiClient(apiClientPrefix)) {

                Logger logger = LoggerFactory.getLogger("commercetools");
                // TODO: UPDATE API Client in dev.properties
                // TODO {@link ClientService#createApiClient(String prefix)}
                // TODO: GET project info
                //

                Project project = client.get().executeBlocking().getBody();
                logger.info("Project key: {}", project.getKey());

                // TODO: GET tax categories
                //

                // TODO Get Tax category by Key
                //

            }
    }
}
