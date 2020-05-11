package guru.springframework.sfgssm.services;

import guru.springframework.sfgssm.domain.Payment;
import guru.springframework.sfgssm.domain.PaymentEvent;
import guru.springframework.sfgssm.domain.PaymentStatus;
import guru.springframework.sfgssm.repositories.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentServiceImplTest {

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    Payment payment;


    @BeforeEach
    void setUp() {
        payment = Payment.builder().amount(BigDecimal.valueOf(12.99)).build();

    }
//
//    @Test
//    void newPayment() {
//    }

    @Transactional
    @Test
    void preAuth() {
        Payment savedPayment = paymentService.newPayment(payment);
        paymentService.preAuth(savedPayment.getId());
        Payment preAuthedPayment = paymentRepository.getOne(savedPayment.getId());
        System.out.println(preAuthedPayment);

    }

    @Transactional
    //@Test
    @RepeatedTest(5)
    void authorizePayment() {
        Payment savedPayment = paymentService.newPayment(payment);
        StateMachine<PaymentStatus, PaymentEvent> preAuthSm = paymentService.preAuth(savedPayment.getId());
        System.out.println("Result of preAuth: " + preAuthSm.getState().getId());
        if (preAuthSm.getState().getId() == PaymentStatus.PRE_AUTH) {
            StateMachine<PaymentStatus, PaymentEvent> authSm = paymentService.authorizePayment(savedPayment.getId());
            System.out.println("Result of auth: " + authSm.getState().getId());
        }

    }


}