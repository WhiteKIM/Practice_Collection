package whitekim.practice.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import whitekim.practice.order.dto.request.ReqOrderInfo;
import whitekim.practice.order.dto.response.RespOrderInfo;
import whitekim.practice.order.service.OrderService;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/info")
    public ResponseEntity<RespOrderInfo> getOrderInfo(@RequestParam Long orderId) {
        RespOrderInfo orderInfo = orderService.getOrderInfo(orderId);

        return ResponseEntity.ok(orderInfo);
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitOrder(@RequestBody ReqOrderInfo orderInfo) {
        orderService.processOrder(orderInfo);

        return ResponseEntity.ok("처리 완료");
    }
}
