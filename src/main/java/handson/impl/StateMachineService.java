package handson.impl;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.common.LocalizedStringBuilder;
import com.commercetools.api.models.state.*;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * .
 */
public class StateMachineService {

    final ProjectApiRoot apiRoot;

    public StateMachineService(final ProjectApiRoot client) {
        this.apiRoot = client;
    }

    public CompletableFuture<ApiHttpResponse<State>> createState(final String key, StateTypeEnum stateTypeEnum, final Boolean initial, final String name) {

        Map<String, String> myNames = new HashMap<String, String>() {
            {
                put("DE", name);
                put("EN", name);
            }
        };
        return
                apiRoot
                        .states()
                        .post(
                                stateDraftBuilder -> stateDraftBuilder
                                        .key(key)
                                        .type(stateTypeEnum)
                                        .initial(initial)
                                        .name(
                                                localizedStringBuilder -> localizedStringBuilder
                                                        .values(myNames)
                                        )
                        )
                        .execute();
    }

    public CompletableFuture<ApiHttpResponse<State>> setStateTransitions(final State stateToBeUpdated, final List<StateResourceIdentifier> states) {

        return
                apiRoot
                        .states()
                        .withId(stateToBeUpdated.getId())
                        .post(
                                stateUpdateBuilder -> stateUpdateBuilder
                                        .version(stateToBeUpdated.getVersion())
                                        .plusActions(
                                                stateUpdateActionBuilder -> stateUpdateActionBuilder.setTransitionsBuilder()
                                                        .transitions(states)
                                        )
                        )
                        .execute();
    }

}
