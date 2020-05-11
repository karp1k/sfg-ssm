package guru.springframework.sfgssm.services;

import guru.springframework.sfgssm.domain.Payment;
import guru.springframework.sfgssm.domain.PaymentEvent;
import guru.springframework.sfgssm.domain.PaymentStatus;
import guru.springframework.sfgssm.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;

/**
 * @author kas
 */
@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;
    private final StateMachineFactory<PaymentStatus, PaymentEvent> stateMachineFactory;

    @Override
    public Payment newPayment(Payment payment) {
        payment.setStatus(PaymentStatus.NEW);
        return repository.save(payment);
    }

    @Override
    public StateMachine<PaymentStatus, PaymentEvent> preAuth(Long paymentId) {
        return null;
    }

    @Override
    public StateMachine<PaymentStatus, PaymentEvent> authorizePayment(Long paymentId) {
        return null;
    }

    @Override
    public StateMachine<PaymentStatus, PaymentEvent> declineAuth(Long paymentId) {
        return null;
    }
}
