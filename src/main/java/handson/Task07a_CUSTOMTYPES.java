package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.common.LocalizedStringBuilder;
import com.commercetools.api.models.type.*;
import handson.impl.ApiPrefixHelper;
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

        Map<String, String> namesForFieldCheck = new HashMap<String, String>() {
            {
                put("DE", "Allowed to place orders");
                put("EN", "Allowed to place orders");
            }
        };
        Map<String, String> namesForFieldComments = new HashMap<String, String>() {
            {
                put("DE", "Bemerkungen");
                put("EN", "comments");
            }
        };

        // Which fields will be used?
        List<FieldDefinition> definitions = Arrays.asList(
                FieldDefinitionBuilder.of()
                        .name("allowed-to-place-orders")
                        .required(false)
                        .label(LocalizedStringBuilder.of()
                                .values(namesForFieldCheck)
                                .build()
                        )
                        .type(CustomFieldBooleanType.of())
                        .build()
                ,
                FieldDefinitionBuilder.of()
                        .name("Comments")
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
                put("DE", "mh-Block-Customer");
                put("EN", "mh-Block-Customer");
            }
        };

        logger.info("Custom Type info: " +
                " "
        );

        client.close();
    }
}
