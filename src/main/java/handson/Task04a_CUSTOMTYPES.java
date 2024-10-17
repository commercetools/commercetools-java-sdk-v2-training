package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.common.LocalizedStringBuilder;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.customer.CustomerSetCustomFieldActionBuilder;
import com.commercetools.api.models.customer.CustomerSetCustomTypeActionBuilder;
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
import static handson.impl.ClientService.getStoreKey;


public class Task04a_CUSTOMTYPES {


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_STORE_CLIENT_PREFIX.getPrefix();
        try (ProjectApiRoot client = createApiClient(apiClientPrefix)) {

            Logger logger = LoggerFactory.getLogger("commercetools");
            final String storeKey = getStoreKey(apiClientPrefix);

            final String customerKey = "ct-customer";

            // TODO CREATE labels for the type and fields
            //
            Map<String, String> labelsForFieldInstructions = new HashMap<String, String>() {
                {
                    put("de-DE", "Instructions");
                    put("en-US", "Instructions");
                }
            };
            Map<String, String> labelsForFieldCode = new HashMap<String, String>() {
                {
                    put("de-DE", "Buzz code");
                    put("en-US", "Buzz code");
                }
            };

            // TODO DEFINE fields
            //
            List<FieldDefinition> definitions = Arrays.asList(
                FieldDefinitionBuilder.of()
                    .name("instructions")
                    .required(true)
                    .label(LocalizedStringBuilder.of()
                        .values(labelsForFieldInstructions)
                        .build()
                    )
                    .type(CustomFieldStringType.of())
                    .build(),
                FieldDefinitionBuilder.of()
                    .name("code")
                    .required(true)
                    .label(LocalizedStringBuilder.of()
                        .values(labelsForFieldCode)
                        .build()
                    )
                    .type(CustomFieldNumberType.of())
                    .build()
            );

            // TODO CREATE type
            //
            Map<String, String> nameForType = new HashMap<String, String>() {
                {
                    put("de-DE", "Delivery instructions");
                    put("en-US", "Delivery instructions");
                }
            };

            client
                .types()
                .post(
                    typeDraftBuilder -> typeDraftBuilder
                        .key("delivery-instructions")
                        .name(LocalizedStringBuilder.of().values(nameForType).build())
                        .resourceTypeIds(
                            ResourceTypeId.CUSTOMER,
                            ResourceTypeId.ORDER
                        )
                        .fieldDefinitions(definitions)
                ).execute()
                .thenAccept(typeApiHttpResponse ->
                        logger.info("Custom Type ID: " + typeApiHttpResponse.getBody().getId())
                )
                .exceptionally(throwable -> {
                    logger.error("Exception: {}", throwable.getMessage());
                    return null;
                }).join();

//            //TODO UPDATE the customer with custom type
//            //
//            Customer customer = client
//                    .inStore(storeKey)
//                    .customers()
//                    .withKey(customerKey)
//                    .get()
//                    .executeBlocking().getBody();
//
//            client
//                .inStore(storeKey)
//                .customers()
//                .withKey(customerKey)
//                .post(
//                    customerUpdateBuilder -> customerUpdateBuilder
//                        .version(customer.getVersion())
//                        .actions(
//                            Arrays.asList(
//                                CustomerSetCustomTypeActionBuilder.of()
//                                    .type(typeResourceIdentifierBuilder -> typeResourceIdentifierBuilder.key("delivery-instructions"))
//                                    .fields(fieldContainerBuilder -> fieldContainerBuilder.values(
//                                        new HashMap<String, Object>() {
//                                            {
//                                                put("instructions", "Leave at door");
//                                                put("code", 1223);
//                                            }
//                                        }
//                                    ))
//                                    .build(),
//                                CustomerSetCustomFieldActionBuilder.of()
//                                    .name("code")
//                                    .value(1221)
//                                    .build(),
//                                CustomerSetCustomFieldActionBuilder.of()
//                                    .name("instructions")
//                                    .value("Leave at door")
//                                    .build()
//                            )
//                        )
//                ).execute()
//                .thenAccept(customerApiHttpResponse ->
//                        logger.info("Customer updated {}", customerApiHttpResponse.getBody().getKey())
//                )
//                .exceptionally(throwable -> {
//                    logger.error("Exception: {}", throwable.getMessage());
//                    return null;
//                }).join();
        }
    }
}
