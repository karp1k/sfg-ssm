package guru.springframework.sfgssm.repositories;

import guru.springframework.sfgssm.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author kas
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
