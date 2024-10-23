package handson.impl;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.state.State;
import com.commercetools.api.models.state.StateResourceIdentifier;
import com.commercetools.api.models.state.StateTypeEnum;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * .
 */
public class StateMachineService {

    final ProjectApiRoot apiRoot;

    public StateMachineService(final ProjectApiRoot apiRoot) {
        this.apiRoot = apiRoot;
    }

    public CompletableFuture<ApiHttpResponse<State>> getStateByKey(final String key) {

        return
            apiRoot
                .states()
                .withKey(key)
                .get()
                .execute();
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
