# 포인트 히스토리 기록 구현 완료

## ✅ 작업 완료 요약

일정 참여 및 장터(온라인/오프라인) 상품 구매 시 포인트 히스토리에 자동으로 기록되도록 구현되었습니다.

---

## 📋 구현 상세

### 1️⃣ 일정 참여 포인트 히스토리 (CalendarService.java)

#### 이미 구현 완료된 기능:

**일정 참여 (paid 타입 - 포인트 차감)**
```java
// Line 444-453
PointHistory history = PointHistory.builder()
        .userUid(user.getUid())
        .channelUid(calendar.getChannelUid())
        .pointType("SCHEDULE")
        .pointAmount(-points)  // 음수로 차감
        .currentBalance(newBalance)
        .description("일정 참여: " + calendar.getTitle())
        .referenceId(String.valueOf(calendarIdx))
        .build();
pointHistoryRepository.save(history);
```

**일정 참여 취소 (환불)**
```java
// Line 544-553
PointHistory history = PointHistory.builder()
        .userUid(user.getUid())
        .channelUid(calendar.getChannelUid())
        .pointType("SCHEDULE_REFUND")
        .pointAmount(participant.getPointsAmount())  // 양수로 환불
        .currentBalance(newBalance)
        .description("일정 참여 취소 환불: " + calendar.getTitle())
        .referenceId(String.valueOf(calendarIdx))
        .build();
pointHistoryRepository.save(history);
```

**일정 포인트 지급 (earn 타입)**
```java
// Line 636-645
PointHistory history = PointHistory.builder()
        .userUid(participantUser.getUid())
        .channelUid(calendar.getChannelUid())
        .pointType("SCHEDULE_EARN")
        .pointAmount(pointsToGrant)  // 양수로 지급
        .currentBalance(newBalance)
        .description("일정 참여 포인트 획득: " + calendar.getTitle())
        .referenceId(String.valueOf(calendar.getIdx()))
        .build();
pointHistoryRepository.save(history);
```

**일정 취소로 인한 환불**
```java
// Line 791-800
PointHistory history = PointHistory.builder()
        .userUid(participantUser.getUid())
        .channelUid(calendar.getChannelUid())
        .pointType("SCHEDULE_CANCELLED_REFUND")
        .pointAmount(participant.getPointsAmount())
        .currentBalance(newBalance)
        .description("일정 취소로 인한 환불: " + calendar.getTitle())
        .referenceId(String.valueOf(calendarIdx))
        .build();
pointHistoryRepository.save(history);
```

---

### 2️⃣ 장터 구매 포인트 히스토리 (MarketplacePurchaseService.java)

#### 개선된 `deductPoints()` 메서드

기존에는 단순히 "장터 상품 구매"로만 기록되었지만, 이제는 **상세한 정보**를 포함합니다:

```java
/**
 * 포인트 차감 (user 테이블의 전역 포인트 사용)
 */
private void deductPoints(String userUid, String channelUid, int amount, String productUid, String description) {
    User user = userRepository.findById(userUid)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

    // 현재 전역 포인트 조회 (user 테이블)
    int currentBalance = (user.getPoint() != null) ? user.getPoint() : 0;

    // 포인트 부족 체크
    if (currentBalance < amount) {
        throw new RuntimeException("포인트가 부족합니다 (보유: " + currentBalance + "P, 필요: " + amount + "P)");
    }

    // 새로운 잔액 계산
    int newBalance = currentBalance - amount;

    // 포인트 히스토리 생성 (기록용)
    PointHistory history = PointHistory.builder()
            .userUid(userUid)
            .channelUid(channelUid)
            .pointType("MARKETPLACE")
            .pointAmount(-amount)  // 음수로 차감
            .currentBalance(newBalance)
            .description(description)  // ✅ 상세한 설명 (상품명 포함)
            .referenceId(productUid)
            .build();

    pointHistoryRepository.save(history);

    // 사용자 테이블의 전역 포인트 업데이트
    user.setPoint(newBalance);
    userRepository.save(user);

    log.info("Points deducted: {} from user {} ({}P -> {}P) - {}", 
             amount, userUid, currentBalance, newBalance, description);
}
```

#### 적용된 모든 장터 구매 시나리오

**1. 온라인 장터 상품 구매** (`purchaseProduct()`)
```java
deductPoints(buyerUid, product.getChannelUid(), totalPrice, productUid, 
            "온라인 장터 상품 구매: " + product.getTitle());
```

**2. 오프라인 장터 즉시 구매** (`instantOfflinePurchase()`)
```java
deductPoints(buyerUid, product.getChannelUid(), totalPrice, productUid,
            "오프라인 장터 상품 구매: " + product.getTitle());
```

**3. 오프라인 장터 이름/연락처로 구매** (`deductPointForOfflineProduct()`)
```java
deductPoints(buyer.getUid(), product.getChannelUid(), request.getDeductPoints(), productUid,
            "오프라인 장터 상품 구매 (이름/연락처 입력): " + product.getTitle());
```

**4. 오프라인 장터 구매 처리** (`processOfflinePurchase()`)
```java
deductPoints(purchase.getBuyerUid(), product.getChannelUid(), 
             request.getPointAmount(), productUid,
             "오프라인 장터 상품 구매: " + product.getTitle());
```

**5. 거래 완료 (구매자)** (`buyerCompleteTrade()`)
```java
deductPoints(buyerUid, product.getChannelUid(), purchase.getTotalPrice(), purchase.getProductUid(),
            "온라인 장터 거래 완료: " + product.getTitle());
```

**6. 거래 확정 (판매자)** (`sellerConfirmTrade()`)
```java
deductPoints(purchase.getBuyerUid(), product.getChannelUid(), purchase.getTotalPrice(), purchase.getProductUid(),
            "온라인 장터 거래 확정: " + product.getTitle());
```

---

## 📊 포인트 히스토리 데이터 구조

### PointHistory 엔티티

```java
@Entity
@Table(name = "point_history")
public class PointHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String userUid;           // 사용자 UID
    private String channelUid;        // 채널 UID
    private String pointType;         // SCHEDULE, SCHEDULE_REFUND, MARKETPLACE 등
    private Integer pointAmount;      // 포인트 양 (양수: 적립, 음수: 차감)
    private Integer currentBalance;   // 현재 잔액
    private String description;       // 상세 설명
    private String referenceId;       // 참조 ID (일정 idx, 상품 uid)
    
    @CreationTimestamp
    private LocalDateTime createdAt;
}
```

### 포인트 타입 (pointType)

| pointType | 설명 | 포인트 변화 |
|-----------|------|------------|
| `SCHEDULE` | 일정 참여 (paid 타입) | 차감 (-) |
| `SCHEDULE_REFUND` | 일정 참여 취소 환불 | 적립 (+) |
| `SCHEDULE_EARN` | 일정 참여 포인트 획득 (earn 타입) | 적립 (+) |
| `SCHEDULE_CANCELLED_REFUND` | 일정 취소로 인한 환불 | 적립 (+) |
| `MARKETPLACE` | 장터 상품 구매 | 차감 (-) |

---

## 🎯 포인트 히스토리 표시 예시

### R포인트 내역 화면 (rpointHistory.vue)

**일정 참여 관련:**
- ✅ "일정 참여: 2025 신년 이벤트" - **차감** (-1,000P)
- ✅ "일정 참여 취소 환불: 2025 신년 이벤트" - **적립** (+1,000P)
- ✅ "일정 참여 포인트 획득: 봉사활동" - **적립** (+500P)
- ✅ "일정 취소로 인한 환불: 2025 신년 이벤트" - **적립** (+1,000P)

**장터 구매 관련:**
- ✅ "온라인 장터 상품 구매: 맥북 프로" - **차감** (-2,000,000P)
- ✅ "오프라인 장터 상품 구매: 신선한 사과" - **차감** (-5,000P)
- ✅ "오프라인 장터 상품 구매 (이름/연락처 입력): 딸기" - **차감** (-3,000P)
- ✅ "온라인 장터 거래 완료: 중고 책" - **차감** (-10,000P)
- ✅ "온라인 장터 거래 확정: 노트북" - **차감** (-500,000P)

---

## ✅ 테스트 시나리오

### 1. 일정 참여 (paid 타입)
1. 일정 목록에서 paid 타입 일정 찾기
2. "참여하기" 버튼 클릭
3. 포인트 차감 확인
4. **R포인트 내역**에서 "일정 참여: [일정명]" 항목 확인
5. 차감된 포인트가 정확히 기록되었는지 확인

### 2. 일정 참여 취소
1. 참여 중인 일정에서 "취소" 버튼 클릭
2. 포인트 환불 확인
3. **R포인트 내역**에서 "일정 참여 취소 환불: [일정명]" 항목 확인

### 3. 온라인 장터 상품 구매
1. 온라인 장터에서 상품 선택
2. "구매하기" 버튼 클릭
3. 포인트 차감 확인
4. **R포인트 내역**에서 "온라인 장터 상품 구매: [상품명]" 항목 확인

### 4. 오프라인 장터 상품 구매
1. 오프라인 장터에서 상품 선택
2. 판매자에게 이름/연락처 전달 또는 즉시 구매
3. 포인트 차감 확인
4. **R포인트 내역**에서 "오프라인 장터 상품 구매: [상품명]" 항목 확인

---

## 🎉 결론

**모든 포인트 사용 내역이 자동으로 포인트 히스토리에 기록됩니다!**

✅ **일정 참여** - paid/earn 타입 모두 기록  
✅ **일정 취소** - 환불 내역 기록  
✅ **온라인 장터** - 구매/거래 완료/거래 확정 모두 기록  
✅ **오프라인 장터** - 즉시 구매/이름 입력 구매 모두 기록  
✅ **상세한 설명** - 상품명, 일정명이 포함되어 어떤 내역인지 명확히 표시  

---

**작성일**: 2026-01-15  
**작성자**: GitHub Copilot  
**관련 파일**:
- Backend: `CalendarService.java`, `MarketplacePurchaseService.java`
- Frontend: `rpointHistory.vue`
- Entity: `PointHistory.java`
