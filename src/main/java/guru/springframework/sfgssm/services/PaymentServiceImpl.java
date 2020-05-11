package guru.springframework.sfgssm.services;

import guru.springframework.sfgssm.domain.Payment;
import guru.springframework.sfgssm.domain.PaymentEvent;
import guru.springframework.sfgssm.domain.PaymentStatus;
import guru.springframework.sfgssm.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
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
        StateMachine<PaymentStatus, PaymentEvent> sm = build(paymentId);
        return null;
    }

    @Override
    public StateMachine<PaymentStatus, PaymentEvent> authorizePayment(Long paymentId) {
        StateMachine<PaymentStatus, PaymentEvent> sm = build(paymentId);
        return null;
    }

    @Override
    public StateMachine<PaymentStatus, PaymentEvent> declineAuth(Long paymentId) {
        StateMachine<PaymentStatus, PaymentEvent> sm = build(paymentId);
        return null;
    }

    // restore state from db
    private StateMachine<PaymentStatus, PaymentEvent> build(Long paymentId) {
        Payment payment = repository.getOne(paymentId);
        // get state machine for specific object
        StateMachine<PaymentStatus, PaymentEvent> sm = stateMachineFactory.getStateMachine(Long.toString(paymentId));
        sm.stop();
        // sets to specific state from db
        sm.getStateMachineAccessor().doWithAllRegions(sma -> {
            sma.resetStateMachine(new DefaultStateMachineContext<>(payment.getStatus(), null, null, null));
        });
        sm.start();
        return sm;
    }
}
