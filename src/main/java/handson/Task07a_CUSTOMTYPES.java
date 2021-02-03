package handson;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.common.LocalizedString;
import com.commercetools.api.models.common.LocalizedStringBuilder;
import com.commercetools.api.models.product_type.TextInputHint;
import com.commercetools.api.models.type.*;
import handson.impl.ClientService;
import handson.impl.PrefixHelper;
import io.vrap.rmf.base.client.ApiHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getProjectKey;


public class Task07a_CUSTOMTYPES {


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = PrefixHelper.getDevApiClientPrefix();

        final String projectKey = getProjectKey(apiClientPrefix);
        final ApiRoot client = createApiClient(apiClientPrefix);
        Logger logger = LoggerFactory.getLogger(Task07a_CUSTOMTYPES.class.getName());

        try (ApiHttpClient apiHttpClient = ClientService.apiHttpClient) {

            // Which fields will be used?
            List<FieldDefinition> definitions = Arrays.asList(
                    FieldDefinitionBuilder.of()
                            .name("plantCheck")
                            .required(false)
                            .label(LocalizedStringBuilder.of()
                                    .addValue("de", "plantCheck")
                                    .addValue("en", "plantCheck")
                                    .build()
                            )
                            .type(CustomFieldBooleanType.of())
                            .build()
                    ,
                    FieldDefinitionBuilder.of()
                            .name("comments")
                            .required(false)
                            .label(LocalizedStringBuilder.of()
                                    .addValue("de", "Bemerkungen")
                                    .addValue("en", "comments")
                                    .build()
                            )
                            .type(CustomFieldStringType.of())
                            .inputHint(TypeTextInputHint.MULTI_LINE)            // shown as single line????
                            .build()
            );

            logger.info("Custom Type info: " +
                    client
                            .withProjectKey(projectKey)
                            .types()
                            .post(
                                    TypeDraftBuilder.of()
                                            .key("customerPlantChecker")
                                            .name(
                                                    LocalizedStringBuilder.of()
                                                            .addValue("de", "customerPlantChecker")
                                                            .addValue("en", "customerPlantChecker")
                                                            .build()
                                            )
                                            .resourceTypeIds(
                                                    ResourceTypeId.CUSTOMER
                                            )
                                            .fieldDefinitions(definitions)
                                            .build()
                            )
                            .execute()
                            .toCompletableFuture().get()
                            .getBody().getId()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
