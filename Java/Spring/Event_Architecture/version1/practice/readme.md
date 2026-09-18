# 주문 처리 로직
사용자 -> 아이템 선택 -> 주문 생성 -> 결제 처리 -> 주문 성공

# 이벤트 목록
1. 결제 요청
```text
- 발생 시점: 주문에 대한 결제를 요청하는 경우
- 포함 데이터: purchasePrice(구매금액), orderId(주문 ID), memberID(사용자 ID)
- 발행 주체: OrderService
- 소비 주체: PaymentEventListner -> PaymentService
- 이벤트로 분리한 이유: 결제 처리와 주문 정보 생성에 대한 기능을 분리하기 위함
```
-----
2. 결제 성공
```text
- 발생 시점: 결제 처리 완료
- 포함 데이터: orderId(주문ID), paymentId(결제Id), chargeAmount(청구금액)
- 발행 주체: PaymentService
- 소비 주체: OrderEventListener
- 이벤트로 분리한 이유: 결제 성공에 따른 주문정보 내 상태 변경 및 결제정보 반영을 수행
```
-----
3. 결제 실패
```text
- 발생 시점: 결제 처리 실패(현재는 미사용)
- 포함 데이터: orderId(주문 ID)
- 발행 주체: PaymentService
- 소비 주체: OrderEventListener
- 이벤트로 분리한 이유: 결제 실패에 따른 주문정보 수정 및 주문 처리 수행을 위함
```
