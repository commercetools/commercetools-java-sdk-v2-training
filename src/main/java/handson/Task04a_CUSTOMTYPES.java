package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.common.LocalizedStringBuilder;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.customer.CustomerSetCustomFieldActionBuilder;
import com.commercetools.api.models.customer.CustomerSetCustomTypeActionBuilder;
import com.commercetools.api.models.order.Order;
import com.commercetools.api.models.order.OrderSetCustomFieldActionBuilder;
import com.commercetools.api.models.order.OrderSetCustomTypeActionBuilder;
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
        try (ProjectApiRoot apiRoot = createApiClient(apiClientPrefix)) {

            Logger logger = LoggerFactory.getLogger("commercetools");
            final String storeKey = getStoreKey(apiClientPrefix);

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

            apiRoot
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

//            //TODO UPDATE the Order with custom type
//            //
//            Order order = apiRoot
//                    .inStore(storeKey)
//                    .orders()
//                    .withOrderNumber("")
//                    .get()
//                    .executeBlocking().getBody();
//
//            apiRoot
//                .inStore(storeKey)
//                .orders()
//                .withId(order.getId())
//                .post(
//                    updateBuilder -> updateBuilder
//                        .version(order.getVersion())
//                        .actions(
//                            Arrays.asList(
//                                OrderSetCustomTypeActionBuilder.of()
//                                    .type(typeResourceIdentifierBuilder -> typeResourceIdentifierBuilder.key("delivery-instructions"))
//                                    .build(),
//                                OrderSetCustomFieldActionBuilder.of()
//                                    .name("code")
//                                    .value(1221)
//                                    .build(),
//                                OrderSetCustomFieldActionBuilder.of()
//                                    .name("instructions")
//                                    .value("Leave at door")
//                                    .build()
//                            )
//                        )
//                ).execute()
//                .thenAccept(orderApiHttpResponse ->
//                        logger.info("Order updated {}", orderApiHttpResponse.getBody().getOrderNumber())
//                )
//                .exceptionally(throwable -> {
//                    logger.error("Exception: {}", throwable.getMessage());
//                    return null;
//                }).join();
//
//
//            //TODO UPDATE the customer with custom type
//            //
//            Customer customer = apiRoot
//                    .inStore(storeKey)
//                    .customers()
//                    .withKey("")
//                    .get()
//                    .executeBlocking().getBody();
//
//            apiRoot
//                .inStore(storeKey)
//                .customers()
//                .withId(customer.getId())
//                .post(
//                    customerUpdateBuilder -> customerUpdateBuilder
//                        .version(customer.getVersion())
//                        .actions(
//                            CustomerSetCustomTypeActionBuilder.of()
//                                .type(typeResourceIdentifierBuilder -> typeResourceIdentifierBuilder.key("delivery-instructions"))
//                                .fields(fieldContainerBuilder -> fieldContainerBuilder.values(
//                                    new HashMap<String, Object>() {
//                                        {
//                                            put("instructions", "Leave at door");
//                                            put("code", 1223);
//                                        }
//                                    }
//                                ))
//                                .build()
//                            )
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
