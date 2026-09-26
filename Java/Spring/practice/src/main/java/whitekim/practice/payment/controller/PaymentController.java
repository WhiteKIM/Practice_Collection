package whitekim.practice.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import whitekim.practice.payment.dto.request.ChargePaymentInfo;
import whitekim.practice.payment.dto.response.RespPaymentInfo;
import whitekim.practice.payment.service.PaymentService;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/info")
    public ResponseEntity<RespPaymentInfo> getPaymentInfo(@RequestParam Long paymentId) {
        RespPaymentInfo paymentInfo = paymentService.getPaymentInfo(paymentId);

        return ResponseEntity.ok(paymentInfo);
    }

    @PostMapping("/charge")
    public ResponseEntity<String> chargePayment(@RequestBody ChargePaymentInfo chargePaymentInfo) {
        paymentService.chargePayment(chargePaymentInfo);

        return ResponseEntity.ok("결제 성공");
    }
}
