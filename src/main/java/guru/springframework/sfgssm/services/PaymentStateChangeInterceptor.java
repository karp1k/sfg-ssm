package guru.springframework.sfgssm.services;

import guru.springframework.sfgssm.domain.Payment;
import guru.springframework.sfgssm.domain.PaymentEvent;
import guru.springframework.sfgssm.domain.PaymentStatus;
import guru.springframework.sfgssm.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author kas
 */
@RequiredArgsConstructor
@Component
public class PaymentStateChangeInterceptor extends StateMachineInterceptorAdapter<PaymentStatus, PaymentEvent> {

    private final PaymentRepository repository;

    // persist state change into db
    @Override
    public void preStateChange(State<PaymentStatus, PaymentEvent> state, Message<PaymentEvent> message,
                               Transition<PaymentStatus, PaymentEvent> transition, StateMachine<PaymentStatus,
            PaymentEvent> stateMachine) {
        Optional.ofNullable(message).ifPresent(msg -> {
            Optional.ofNullable((Long) msg.getHeaders().getOrDefault(PaymentServiceImpl.PAYMENT_ID_HEADER,-1L))
                    .ifPresent(paymentId -> {
                        Payment payment = repository.getOne(paymentId);
                        payment.setStatus(state.getId());
                        repository.save(payment);
                    });
        });
        super.preStateChange(state, message, transition, stateMachine);
    }
}
