package guru.springframework.sfgssm.config;

import guru.springframework.sfgssm.domain.PaymentEvent;
import guru.springframework.sfgssm.domain.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;

import java.util.EnumSet;

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
}
