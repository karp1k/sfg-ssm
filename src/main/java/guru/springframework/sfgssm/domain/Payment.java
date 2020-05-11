package guru.springframework.sfgssm.domain;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author kas
 */
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Payment {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private BigDecimal amount;



}
