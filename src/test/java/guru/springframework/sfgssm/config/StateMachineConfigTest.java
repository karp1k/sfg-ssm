package guru.springframework.sfgssm.config;

import guru.springframework.sfgssm.domain.PaymentEvent;
import guru.springframework.sfgssm.domain.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StateMachineConfigTest {

    @Autowired
    StateMachineFactory<PaymentStatus, PaymentEvent> factory;

    @Test
    public void test() {
        StateMachine<PaymentStatus, PaymentEvent> stateMachine = factory.getStateMachine(UUID.randomUUID());

        stateMachine.start();

        System.out.println(stateMachine.getState().toString());

        stateMachine.sendEvent(PaymentEvent.PRE_AUTHORIZE);

        System.out.println(stateMachine.getState().toString());

        stateMachine.sendEvent(PaymentEvent.PRE_AUTH_APPROVED);

        System.out.println(stateMachine.getState().toString());

    }

}