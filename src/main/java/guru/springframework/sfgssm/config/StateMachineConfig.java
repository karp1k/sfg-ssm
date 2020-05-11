package guru.springframework.sfgssm.config;

import guru.springframework.sfgssm.domain.PaymentEvent;
import guru.springframework.sfgssm.domain.PaymentStatus;
import guru.springframework.sfgssm.services.PaymentService;
import guru.springframework.sfgssm.services.PaymentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;
import java.util.Random;

/**
 * StateMachineConfigureAdapter and EnumStateMachineConfigureAdapter - no difference if states and events are enums
 * @author kas
 */
@Slf4j
@EnableStateMachineFactory // component to generate state machine
@Configuration
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentStatus, PaymentEvent> {

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
                .target(PaymentStatus.NEW).action(preAuthAction())
                .event(PaymentEvent.PRE_AUTHORIZE) // not duing state change. In this event Payment still in the "NEW" state
        .and()
                .withExternal().source(PaymentStatus.NEW) // from (source) "NEW" to "PRE_AUTH" (target) on event "PRE_AUTH_APPROVED"
                .target(PaymentStatus.PRE_AUTH)
                .event(PaymentEvent.PRE_AUTH_APPROVED) // on event this event change state to "PRE_AUTH"
        .and()
                .withExternal().source(PaymentStatus.NEW) // from "NEW" to "PRE_AUTH_ERROR" on event "PRE_AUTH_DECLINED"
                .target(PaymentStatus.PRE_AUTH_ERROR)
                .event(PaymentEvent.PRE_AUTH_DECLINED);
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
    // invoked after event PRE_AUTHORIZE happen
    public Action<PaymentStatus, PaymentEvent> preAuthAction() {
        return stateContext -> {
            log.info("PreAuth was called");
            if (new Random().nextInt(10) < 8) {
                log.info("approved");
                stateContext.getStateMachine()
                        .sendEvent(MessageBuilder
                                .withPayload(PaymentEvent.PRE_AUTH_APPROVED)
                                .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, stateContext.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                                .build());
            } else {
                log.info("declined, no credit");
                stateContext.getStateMachine()
                        .sendEvent(MessageBuilder
                                .withPayload(PaymentEvent.PRE_AUTH_DECLINED)
                                .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, stateContext.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                                .build());
            }
        };
    }
}
