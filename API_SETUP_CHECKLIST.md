# Checklist để API `/api/matches/{id}/commands` hoạt động

## ✅ Đã có sẵn

### 1. Code Components

- ✅ `MatchCommandController` - Controller xử lý request
- ✅ `MatchCommandService` - Service logic
- ✅ `CommandMapper` - Map DTO → Command
- ✅ `CommandDto` - DTO cho request
- ✅ `CommandResult` - Response record
- ✅ `MatchCommand` interface và implementations (`RaiseTroops`, `SpendGold`)
- ✅ `CommandType` enum
- ✅ `Simulator` - Business logic engine
- ✅ `MatchAggregate` - Aggregate root
- ✅ `Validation` - Validation logic
- ✅ Domain Events (`GoldSpent`, `TroopsRaised`, `DomainEvent`)
- ✅ Store entities (`MatchEvent`, `MatchEventTx`, `MatchEventKey`)
- ✅ Repositories (`MatchRepository`, `MatchDetailRepository`, `KingdomInfoRepository`, `MatchEventRepository`, `MatchEventTxRepository`)

### 2. Database

- ✅ Migration `009-create-match-tables.sql` - Tạo tables: matches, match_details, kingdom_info, buildings
- ✅ Migration `011-create-match-event-tables.sql` - Tạo tables: match_event_tx, match_events
- ✅ Migration `012-set-tx-id-default-sequence.sql` - Set default cho tx_id
- ✅ Migration `013-complete-tx-id-sequence-setup.sql` - Complete sequence setup

## ⚠️ Cần kiểm tra/thêm

### 1. Security Configuration

**File:** `src/main/java/com/example/three_kingdom_backend/config/SecurityConfig.java`

**Vấn đề:** API endpoint `/api/matches/{id}/commands` hiện tại yêu cầu authentication (`.anyRequest().authenticated()`)

**Giải pháp:** Có 2 options:

**Option A: Cho phép authenticated users (khuyến nghị)**

- Không cần thay đổi gì, API sẽ yêu cầu JWT token
- Client cần gửi `Authorization: Bearer <token>` header

**Option B: Cho phép public access (cho testing)**
Thêm vào SecurityConfig:

```java
.requestMatchers("/api/matches/**/commands").permitAll()
```

### 2. ObjectMapper Bean

**File:** `src/main/java/com/example/three_kingdom_backend/config/JacksonConfig.java` (cần tạo)

**Vấn đề:** `MatchCommandService` inject `ObjectMapper`, Spring Boot tự động tạo bean nhưng có thể cần config

**Giải pháp:** Tạo config class (optional, Spring Boot auto-config đủ):

```java
@Configuration
public class JacksonConfig {
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
            .findAndRegisterModules()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
```

### 3. Database Connection

**File:** `src/main/resources/application.properties`

**Cần đảm bảo:**

- ✅ Database đang chạy (PostgreSQL)
- ✅ Connection string đúng
- ✅ Migrations đã được apply

**Kiểm tra:**

```bash
# Start database
make docker-up

# Check migrations
# Khi start app, Liquibase sẽ tự động apply migrations
```

### 4. Test Data (Optional)

Để test API, cần có:

- ✅ Match record trong database
- ✅ MatchDetail record
- ✅ KingdomInfo records (WEI, SHU, WU)

**Có thể tạo test data bằng:**

- SQL script
- Test controller endpoint
- Hoặc insert trực tiếp vào DB

## 📋 Testing Steps

1. **Start database:**

   ```bash
   make docker-up
   ```

2. **Start application:**

   ```bash
   make run
   ```

3. **Test API với curl:**

   ```bash
   # Nếu cần authentication, lấy token trước:
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"user","password":"pass"}'

   # Gọi command API:
   curl -X POST http://localhost:8080/api/matches/1/commands \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <token>" \
     -H "Idempotency-Key: test-key-123" \
     -d '{
       "type": "RAISE_TROOPS",
       "matchId": 1,
       "actor": "WEI",
       "idempotencyKey": "test-key-123",
       "data": {"amount": 2}
     }'
   ```

## 🔍 Potential Issues

1. **Missing Match/MatchDetail data:** API sẽ throw `IllegalStateException` nếu không tìm thấy
2. **Sequence not initialized:** Nếu `match_event_tx_id_seq` chưa được tạo, sẽ lỗi
3. **Foreign key constraints:** Đảm bảo match_id tồn tại trong matches table

## ✅ Summary

**Để API hoạt động ngay:**

1. ✅ Code đã đầy đủ
2. ✅ Database migrations đã có
3. ⚠️ Cần start database và apply migrations
4. ⚠️ Cần có test data (Match, MatchDetail, KingdomInfo)
5. ⚠️ Cần JWT token nếu dùng authentication (hoặc permitAll endpoint)

**Minimal setup để test:**

- Start DB: `make docker-up`
- Start app: `make run` (migrations tự apply)
- Insert test data vào matches, match_details, kingdom_info
- Gọi API với JWT token hoặc permitAll endpoint
