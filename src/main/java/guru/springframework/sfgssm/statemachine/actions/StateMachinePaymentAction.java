package guru.springframework.sfgssm.statemachine.actions;

import guru.springframework.sfgssm.domain.PaymentEvent;
import guru.springframework.sfgssm.domain.PaymentStatus;
import guru.springframework.sfgssm.services.PaymentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author kas
 */
@Slf4j
@Component
public class StateMachinePaymentAction {

    // invoked after event PRE_AUTHORIZE happen
    public Action<PaymentStatus, PaymentEvent> preAuthAction() {
        return stateContext -> {
            log.info("preAuth was called");
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

    public Action<PaymentStatus, PaymentEvent> preAuthApprovedAction() {
        return stateContext -> {
            log.info("pre auth successfully approved");
        };
    }

    public Action<PaymentStatus, PaymentEvent> preAuthDeclineAction() {
        return stateContext -> {
            log.info("pre auth declined");
        };
    }


    // invoked after event AUTHORIZE happen
    public Action<PaymentStatus, PaymentEvent> authAction() {
        return stateContext -> {
            log.info("auth was called");
            if (new Random().nextInt(10) < 8) {
                log.info("auth approved");
                stateContext.getStateMachine()
                        .sendEvent(MessageBuilder
                                .withPayload(PaymentEvent.AUTH_APPROVED)
                                .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, stateContext.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                                .build());
            } else {
                log.info("auth declined, no credit");
                stateContext.getStateMachine()
                        .sendEvent(MessageBuilder
                                .withPayload(PaymentEvent.AUTH_DECLINED)
                                .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, stateContext.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                                .build());
            }
        };
    }

    public Action<PaymentStatus, PaymentEvent>authApprovedAction() {
        return stateContext -> {
            log.info("auth successfully approved");
        };
    }

    public Action<PaymentStatus, PaymentEvent> authDeclineAction() {
        return stateContext -> {
            log.info("auth declined");
        };
    }
}
