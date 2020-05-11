package guru.springframework.sfgssm.statemachine.guards;

import guru.springframework.sfgssm.domain.PaymentEvent;
import guru.springframework.sfgssm.domain.PaymentStatus;
import guru.springframework.sfgssm.services.PaymentServiceImpl;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

/**
 * @author kas
 */
@Component
public class PaymentIdGuard implements Guard<PaymentStatus, PaymentEvent> {
    // guard check that message with event contains payment_id in the message
    @Override
    public boolean evaluate(StateContext<PaymentStatus, PaymentEvent> stateContext) {
        return stateContext.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER) != null;
    }
}
