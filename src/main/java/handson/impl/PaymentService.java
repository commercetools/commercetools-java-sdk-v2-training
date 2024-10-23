package handson.impl;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.payment.TransactionType;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;


// TODO: Change order of actions
// a) Create Payment
// b) Set Payment on card
// c) Add Transactions

// TODO: Add Interface actions
// d) Requires the declaration of custom fields (taught in a later session in class)

// TODO: Allow the customer to play with different payment methods
// Have an order number like order1234-12, where -12 is not visible to the customer

public class PaymentService {

    final ProjectApiRoot apiRoot;
    final String storeKey;

    public PaymentService(final ProjectApiRoot apiRoot, final String storeKey) {
        this.apiRoot = apiRoot;
        this.storeKey = storeKey;
    }

    public CompletableFuture<ApiHttpResponse<Cart>> createPaymentAndAddToCart(
            final Cart cart,
            String psp_Name,
            String psp_Method,
            String interfaceId,
            String interactionId) {

        return
            apiRoot
                .payments()
                .post(
                    paymentDraftBuilder -> paymentDraftBuilder
                        .amountPlanned(
                            moneyBuilder -> moneyBuilder
                                .centAmount(cart.getTotalPrice().getCentAmount())
                                .currencyCode(cart.getTotalPrice().getCurrencyCode())
                        )
                        .paymentMethodInfo(
                            paymentMethodInfoBuilder -> paymentMethodInfoBuilder
                                .paymentInterface(psp_Name)        // PSP Provider Name: WireCard, ....
                                .method(psp_Method)                // PSP Provider Method: CreditCard
                        )
                        .interfaceId(interfaceId)                          // ID of the payment, important !!!
                )
                .execute()
                .thenComposeAsync(paymentApiHttpResponse ->
                    apiRoot
                        .payments()
                        .withId(paymentApiHttpResponse.getBody().getId())
                        .post(
                            paymentUpdateBuilder -> paymentUpdateBuilder
                                .version(paymentApiHttpResponse.getBody().getVersion())
                                .plusActions(
                                    paymentUpdateActionBuilder -> paymentUpdateActionBuilder.addTransactionBuilder()
                                        .transaction(
                                            transactionDraftBuilder -> transactionDraftBuilder
                                                .amount(
                                                    moneyBuilder -> moneyBuilder
                                                        .centAmount(cart.getTotalPrice().getCentAmount())
                                                        .currencyCode(cart.getTotalPrice().getCurrencyCode())
                                                )
                                                .timestamp(ZonedDateTime.now())
                                                .type(TransactionType.CHARGE)
                                                .interactionId(interactionId)
                                        )
                               )
                                .plusActions(
                                    paymentUpdateActionBuilder -> paymentUpdateActionBuilder.setStatusInterfaceCodeBuilder()
                                        .interfaceCode("SUCCESS")
                                )
                                .plusActions(
                                    paymentUpdateActionBuilder -> paymentUpdateActionBuilder.setStatusInterfaceTextBuilder()
                                        .interfaceText("We got the money.")
                                )
                        )
                        .execute()
                )
                .thenComposeAsync(paymentApiHttpResponse ->
                    apiRoot
                        .inStore(storeKey)
                        .carts()
                        .withId(cart.getId())
                        .post(
                            cartUpdateBuilder -> cartUpdateBuilder
                                .version(cart.getVersion())
                                .plusActions(
                                    cartUpdateActionBuilder -> cartUpdateActionBuilder.addPaymentBuilder()
                                        .payment(paymentResourceIdentifierBuilder -> paymentResourceIdentifierBuilder.id(paymentApiHttpResponse.getBody().getId()))
                                )
                        )
                        .execute()
                    );
    }

}
