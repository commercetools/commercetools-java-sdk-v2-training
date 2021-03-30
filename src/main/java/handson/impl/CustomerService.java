package handson.impl;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.common.AddressBuilder;
import com.commercetools.api.models.customer.*;

import com.commercetools.api.models.customer_group.CustomerGroup;
import com.commercetools.api.models.customer_group.CustomerGroupResourceIdentifierBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This class provides operations to work with {@link Customer}s.
 */
public class CustomerService {

    ApiRoot apiRoot;
    String projectKey;

    public CustomerService(final ApiRoot client, String projectKey) {
        this.apiRoot = client;
        this.projectKey = projectKey;
    }

    public CompletableFuture<ApiHttpResponse<Customer>> getCustomerByKey(String customerKey) {
        return
                apiRoot
                        .withProjectKey(projectKey)
                        .customers()
                        .withKey(customerKey)
                        .get()
                        .execute();
    }

    public CompletableFuture<ApiHttpResponse<CustomerSignInResult>> createCustomer(
            final String email,
            final String password,
            final String customerKey,
            final String firstName,
            final String lastName,
            final String country) {

        return apiRoot
                        .withProjectKey(projectKey)
                        .customers()
                        .post(CustomerDraftBuilder.of()
                                .email(email)
                                .password(password)
                                .firstName(firstName)
                                .lastName(lastName)
                                .key(customerKey)
                                .addresses(
                                    AddressBuilder.of()
                                            .country(country)
                                            .build()
                                )
                                .defaultShippingAddress(0L)
                                .build())
                        .execute();

    }

    public CompletableFuture<ApiHttpResponse<CustomerToken>> createEmailVerificationToken(
            final ApiHttpResponse<CustomerSignInResult> customerSignInResultApiHttpResponse,
            final long timeToLiveInMinutes
    ) {

        final Customer customer = customerSignInResultApiHttpResponse.getBody().getCustomer();

        return
                apiRoot
                        .withProjectKey(projectKey)
                        .customers()
                        .emailToken()
                        .post(
                                CustomerCreateEmailTokenBuilder.of()
                                        .id(customer.getId())
                                        .ttlMinutes(timeToLiveInMinutes)
                                        .build()
                        )
                        .execute();
    }

    public CompletableFuture<ApiHttpResponse<CustomerToken>> createEmailVerificationToken(final Customer customer, final long timeToLiveInMinutes) {

        return
                apiRoot
                        .withProjectKey(projectKey)
                        .customers()
                        .emailToken()
                        .post(
                                CustomerCreateEmailTokenBuilder.of()
                                        .id(customer.getId())
                                        .ttlMinutes(timeToLiveInMinutes)
                                .build()
                        )
                        .execute();
    }

    public CompletableFuture<ApiHttpResponse<JsonNode>> verifyEmail(final ApiHttpResponse<CustomerToken> customerTokenApiHttpResponse) {

        final CustomerToken customerToken = customerTokenApiHttpResponse.getBody();

        return
                apiRoot
                        .withProjectKey(projectKey)
                        .customers()
                        .emailConfirm()
                        .post(
                                CustomerEmailVerifyBuilder.of()
                                        .tokenValue(customerToken.getValue())
                                        .build()
                        )
                        .execute();
    }

    public CompletableFuture<ApiHttpResponse<JsonNode>> verifyEmail(final CustomerToken customerToken) {


        return
                apiRoot
                        .withProjectKey(projectKey)
                        .customers()
                        .emailConfirm()
                        .post(
                               CustomerEmailVerifyBuilder.of()
                                    .tokenValue(customerToken.getValue())
                                    .build()
                                )
                        .execute();
    }

    public CompletableFuture<ApiHttpResponse<CustomerGroup>> getCustomerGroupByKey(String customerGroupKey) {
        return
                apiRoot
                        .withProjectKey(projectKey)
                        .customerGroups()
                        .withKey(customerGroupKey)
                        .get()
                        .execute();
    }

    public CompletableFuture<ApiHttpResponse<Customer>> assignCustomerToCustomerGroup(
            final ApiHttpResponse<Customer> customerApiHttpResponse,
            final ApiHttpResponse<CustomerGroup> customerGroupApiHttpResponse) {

        final Customer customer = customerApiHttpResponse.getBody();
        final CustomerGroup customerGroup = customerGroupApiHttpResponse.getBody();

        return
                apiRoot
                        .withProjectKey(projectKey)
                        .customers()
                        .withKey(customer.getKey())
                        .post(CustomerUpdateBuilder.of()
                                .version(customer.getVersion())
                                .actions(
                                    CustomerSetCustomerGroupActionBuilder.of()
                                        .customerGroup(CustomerGroupResourceIdentifierBuilder.of()
                                                .key(customerGroup.getKey())
                                                .build())
                                        .build()
                                )
                                .build())
                        .execute();
    }

}
