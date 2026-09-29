# 주문 처리 로직
사용자 -> 아이템 선택 -> 주문 생성 -> 결제 처리 -> 결제 성공 -> 주문상태 : 성공 <br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;L 결제 처리 실패 -> 주문 상태 : 실패 및 재고 롤백

# 이벤트 목록
1. 결제 요청
```text
- 발생 시점: 주문에 대한 결제를 요청하는 경우
- 포함 데이터: purchasePrice(구매금액), orderId(주문 ID), memberID(사용자 ID)
- 발행 주체: OrderService
- 소비 주체: PaymentEventListener -> PaymentService
- 이벤트로 분리한 이유: 결제 처리와 주문 정보 생성에 대한 기능을 분리하기 위함
- 처리 방식: synchronous Spring event
- transaction phase: AFTER_COMMIT
```
-----
2. 결제 성공
```text
- 발생 시점: 결제 처리 완료
- 포함 데이터: orderId(주문ID), paymentId(결제Id), chargeAmount(청구금액)
- 발행 주체: PaymentService
- 소비 주체: OrderEventListener
- 이벤트로 분리한 이유: 결제 성공에 따른 주문정보 내 상태 변경 및 결제정보 반영을 수행
- 처리 방식: synchronous Spring event
- transaction phase: AFTER_COMMIT
- transaction Propagation : REQUIRES_NEW
```
-----
3. 결제 실패
```text
- 발생 시점: 결제 처리 실패
- 포함 데이터: orderId(주문 ID)
- 발행 주체: PaymentService
- 소비 주체: OrderEventListener
- 이벤트로 분리한 이유: 결제 실패에 따른 주문정보 수정 및 주문 처리 수행을 위함
- 처리 방식: synchronous Spring event
- transaction phase: AFTER_ROLLBACK
- transaction Propagation : REQUIRES_NEW
```
## API
1. MemberExistApi
```text
소유자 : Member
분리 사유 : 멤버 조회, 회원가입 등의 기능은 다른 모듈에서 알 필요가 없는 기능으로 멤버 여부만 주문에서 사용하기 때문
```
---
2. ItemPurchaseApi
```text
소유자 : Item
분리 사유 : 물품 등록, 전체 조회 기능은 다른 모듈에서 사용하지 않고 롤백 기능과도 명시적인 구분을 위해 분리
```
---
3. ItemRollbackApi
```text
소유자 : Item
분리 사유 : 물품 등록, 전체 조회 기능은 다른 모듈에서 사용하지 않고 구매 전 재고확보 기능과도 명시적인 구분을 위해 분리
```

## Event
1. OrderRequestPaymentEvent
```text
- 소유자 : Order
- 소비자 : Payment
```
-----
2. PaymentSuccessEvent
```text
- 소유자 : Payment
- 소비자 : Order
```
-----
3. PaymentFailedEvent
```text
- 소유자 : Payment
- 소비자 : Order
```

- 현재 패키지 구조에서는 소유자 별로 분리 시 순환사이클이 발생하여 분리 후 소유자 소비자 명시
- 해당 common.event 패키지는 공통 이벤트를 위한 패키지가 아닌 순환 사이클이 발생하여 부득이한 경우에만 사용해야하며, 소유자와 소비자가 누구인지 명시해야 함