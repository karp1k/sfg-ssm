package guru.springframework.sfgssm.config;

import guru.springframework.sfgssm.domain.PaymentEvent;
import guru.springframework.sfgssm.domain.PaymentStatus;
import guru.springframework.sfgssm.statemachine.actions.StateMachinePaymentAction;
import guru.springframework.sfgssm.statemachine.guards.PaymentIdGuard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

/**
 * StateMachineConfigureAdapter and EnumStateMachineConfigureAdapter - no difference if states and events are enums
 * @author kas
 */
@Slf4j
@RequiredArgsConstructor
@EnableStateMachineFactory // component to generate state machine
@Configuration
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentStatus, PaymentEvent> {

    private final StateMachinePaymentAction paymentAction;
    private final PaymentIdGuard paymentIdGuard;

    @Override
    public void configure(StateMachineStateConfigurer<PaymentStatus, PaymentEvent> states) throws Exception {
        states.withStates()
                .initial(PaymentStatus.NEW) // initial state for every entity
                .states(EnumSet.allOf(PaymentStatus.class)) // there all states located
                .end(PaymentStatus.AUTH) // end state
                .end(PaymentStatus.PRE_AUTH_ERROR) // end state
                .end(PaymentStatus.AUTH_ERROR); // end state
    }

    @Override // transition config
    public void configure(StateMachineTransitionConfigurer<PaymentStatus, PaymentEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(PaymentStatus.NEW) // from "NEW" state to "NEW" on event "PRE_AUTHORIZE"
                .target(PaymentStatus.NEW)
                // not doing state change. In this event Payment still in the "NEW" state
                // guards works only on this step (only on event PRE_AUTHORIZE)
                .event(PaymentEvent.PRE_AUTHORIZE).action(paymentAction.preAuthAction()).guard(paymentIdGuard)
        .and()
                .withExternal().source(PaymentStatus.NEW) // from (source) "NEW" to "PRE_AUTH" (target) on event "PRE_AUTH_APPROVED"
                .target(PaymentStatus.PRE_AUTH)
                .event(PaymentEvent.PRE_AUTH_APPROVED).action(paymentAction.preAuthApprovedAction()) // on event this event change state to "PRE_AUTH"
        .and()
                .withExternal().source(PaymentStatus.NEW) // from "NEW" to "PRE_AUTH_ERROR" on event "PRE_AUTH_DECLINED"
                .target(PaymentStatus.PRE_AUTH_ERROR)
                .event(PaymentEvent.PRE_AUTH_DECLINED).action(paymentAction.preAuthDeclineAction())
                //preauth to auth
        .and()
                .withExternal().source(PaymentStatus.PRE_AUTH)
                .target(PaymentStatus.PRE_AUTH).action(paymentAction.authAction())
                .event(PaymentEvent.AUTHORIZE)
        .and()
                .withExternal().source(PaymentStatus.PRE_AUTH)
                .target(PaymentStatus.AUTH)
                .event(PaymentEvent.AUTH_APPROVED).action(paymentAction.authApprovedAction())
        .and()
                .withExternal().source(PaymentStatus.PRE_AUTH)
                .target(PaymentStatus.AUTH_ERROR)
                .event(PaymentEvent.AUTH_DECLINED).action(paymentAction.authDeclineAction());
    }

    @Override // add logging to listen state change
    public void configure(StateMachineConfigurationConfigurer<PaymentStatus, PaymentEvent> config) throws Exception {
        StateMachineListener<PaymentStatus, PaymentEvent> adapter = new StateMachineListenerAdapter<PaymentStatus, PaymentEvent>() {
            @Override
            public void stateChanged(State from, State to) {
//                log.info(String.format("stateChanged(from: %s, to %s)", from.getId(), to.getId()));
                log.info(String.format("stateChanged(from: %s, to %s)", from, to));
                //super.stateChanged(from, to);
            }
        };
        config.withConfiguration().listener(adapter);

    }

}
