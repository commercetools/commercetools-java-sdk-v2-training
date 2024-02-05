package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.common.LocalizedStringBuilder;
import com.commercetools.api.models.type.*;
import handson.impl.ApiPrefixHelper;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;


public class Task07a_CUSTOMTYPES {


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        final ProjectApiRoot client = createApiClient(apiClientPrefix);
        Logger logger = LoggerFactory.getLogger(Task07a_CUSTOMTYPES.class.getName());

        Map<String, String> labelsForFieldCheck = new HashMap<String, String>() {
            {
                put("de-DE", "Allowed to place orders");
                put("en-US", "Allowed to place orders");
            }
        };
        Map<String, String> labelsForFieldComments = new HashMap<String, String>() {
            {
                put("de-DE", "Bemerkungen");
                put("en-US", "comments");
            }
        };

        // Which fields will be used?
        List<FieldDefinition> definitions = Arrays.asList(
                 FieldDefinitionBuilder.of()
                        .name("allowed-to-place-orders")
                        .required(true)
                        .label(LocalizedStringBuilder.of()
                                .values(labelsForFieldCheck)
                                .build()
                        )
                        .type(CustomFieldBooleanType.of())
                        .build()
                ,
                FieldDefinitionBuilder.of()
                        .name("Comments")
                        .required(true)
                        .label(LocalizedStringBuilder.of()
                                .values(labelsForFieldComments)
                                .build()
                        )
                        .type(CustomFieldStringType.of())
                        .inputHint(TypeTextInputHint.MULTI_LINE)            // shown as single line????
                        .build()
        );

        Map<String, String> namesForType = new HashMap<String, String>() {
            {
                put("de-DE", "mh-Block-Customer");
                put("en-US", "mh-Block-Customer");
            }
        };

        client
                .types()
                .post(
                        typeDraftBuilder -> typeDraftBuilder
                                .key("mh-block-customer")
                                .name(
                                        LocalizedStringBuilder.of()
                                                .values(namesForType)
                                                .build()
                                )
                                .resourceTypeIds(
                                        ResourceTypeId.CUSTOMER
                                )
                                .fieldDefinitions(definitions)
                )
                .execute()
                .thenApply(ApiHttpResponse::getBody)
                .handle((type, exception) -> {
                    if (exception != null) {
                        logger.error("Exception: " + exception.getMessage());
                        return null;
                    }
                    ;
                    logger.info("Custom Type ID: "
                            + type.getId());
                    return type;
                })
                .thenRun(() -> client.close());
    }
}
