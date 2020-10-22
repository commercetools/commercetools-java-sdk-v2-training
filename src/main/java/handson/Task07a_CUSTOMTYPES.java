package handson;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.common.LocalizedString;
import com.commercetools.api.models.common.LocalizedStringBuilder;
import com.commercetools.api.models.product_type.TextInputHint;
import com.commercetools.api.models.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;



public class Task07a_CUSTOMTYPES {


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String projectKey = "training-011-avensia-test";
        final ApiRoot client = createApiClient("mh-dev-admin.");

        Logger logger = LoggerFactory.getLogger(Task04b_CHECKOUT.class.getName());

        Map<String, String> namesForFieldCheck = new HashMap<String, String>() {
            {
                put("DE", "plantCheck");
                put("EN", "plantCheck");
            }
        };
        Map<String, String> namesForFieldComments = new HashMap<String, String>() {
            {
                put("DE", "comments");
                put("EN", "Bemerkungen");
            }
        };

        // Which fields will be used?
            List<FieldDefinition> definitions = Arrays.asList(
                    FieldDefinitionBuilder.of()
                            .name("plantCheck")
                            .required(false)
                            .label(LocalizedStringBuilder.of()
                                    .values(namesForFieldCheck)
                                    .build()
                            )
                            .type(CustomFieldBooleanType.of())
                            .build()
                    ,
                    FieldDefinitionBuilder.of()
                            .name("comments")
                            .required(false)
                            .label(LocalizedStringBuilder.of()
                                    .values(namesForFieldComments)
                                    .build()
                            )
                            .type(CustomFieldStringType.of())
                            .inputHint(TypeTextInputHint.MULTI_LINE)            // shown as single line????
                            .build()
                    );

        Map<String, String> namesForType = new HashMap<String, String>() {
            {
                put("DE", "customerPlantChecker");
                put("EN", "customerPlantChecker");
            }
        };

            logger.info("Custom Type info: " +
                client
                        .withProjectKey(projectKey)
                        .types()
                        .post(
                                TypeDraftBuilder.of()
                                    .key("customerPlantChecker")
                                    .name(
                                            LocalizedStringBuilder.of()
                                                .values(namesForType)
                                                .build()
                                    )
                                    .resourceTypeIds(
                                            Arrays.asList(
                                                    ResourceTypeId.CUSTOMER
                                            )
                                    )
                                    .fieldDefinitions(definitions)
                                    .build()
                        )
                        .execute()
                        .toCompletableFuture().get()
                        .getBody().getId()
                );

    }
}
