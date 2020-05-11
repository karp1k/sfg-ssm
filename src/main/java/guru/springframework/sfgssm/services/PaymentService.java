package guru.springframework.sfgssm.services;

import guru.springframework.sfgssm.domain.Payment;
import guru.springframework.sfgssm.domain.PaymentEvent;
import guru.springframework.sfgssm.domain.PaymentStatus;
import org.springframework.statemachine.StateMachine;

/**
 * @author kas
 */
public interface PaymentService {

    Payment newPayment(Payment payment);
    StateMachine<PaymentStatus, PaymentEvent> preAuth(Long paymentId);
    StateMachine<PaymentStatus, PaymentEvent> authorizePayment(Long paymentId);

}
