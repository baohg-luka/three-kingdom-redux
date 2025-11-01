# Cơ chế tự động của Liquibase

## Quy trình tự động khi chạy `make run`

Khi bạn chạy `make run` (hoặc `./gradlew bootRun`), đây là những gì xảy ra:

### 1. Spring Boot khởi động

- Spring Boot tự động phát hiện Liquibase dependency trong classpath
- Spring Boot tìm file changelog được cấu hình trong `application.properties`:
  ```properties
  spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.yaml
  spring.liquibase.enabled=true
  ```

### 2. Liquibase được khởi tạo

- Liquibase đọc master changelog (`db.changelog-master.yaml`)
- Đọc tất cả các file migration được include
- Kết nối với database

### 3. Liquibase kiểm tra database

- Kiểm tra bảng `databasechangelog` (nếu chưa có sẽ tự tạo)
- So sánh:
  - **Danh sách changeset trong code** (từ các file `.sql`)
  - **Danh sách changeset đã chạy** (trong bảng `databasechangelog`)

### 4. Tự động chạy migrations mới

- Liquibase tìm các changeset **chưa chạy**
- Tự động execute các changeset mới theo thứ tự
- Ghi lại vào bảng `databasechangelog` sau khi chạy thành công

### 5. Ứng dụng tiếp tục khởi động

- Sau khi migrations hoàn tất, Spring Boot tiếp tục khởi động
- JPA/Hibernate validate schema (do `spring.jpa.hibernate.ddl-auto=validate`)

## Ví dụ thực tế

### Scenario 1: Không có changeset mới

```
make run
→ Liquibase kiểm tra
→ Tất cả changeset đã chạy rồi
→ "Reading from public.databasechangelog"
→ Ứng dụng khởi động bình thường
```

### Scenario 2: Có changeset mới

```
1. Bạn tạo file: 002-create-users-table.sql
2. Thêm vào master changelog
3. make run
→ Liquibase kiểm tra
→ Phát hiện changeset mới: 002-create-users-table
→ "Running Changeset: 002-create-users-table"
→ "ChangeSet ... ran successfully"
→ Ghi vào databasechangelog
→ Ứng dụng khởi động
```

## Cách Liquibase biết changeset nào đã chạy?

Liquibase sử dụng **hash** của changeset để track:

- Mỗi changeset có một **hash** được tính từ nội dung file
- Hash được lưu trong bảng `databasechangelog`
- Nếu hash khác nhau → changeset mới hoặc đã thay đổi

**Điều này có nghĩa:**

- ✅ Thêm changeset mới → Tự động chạy
- ❌ Sửa changeset đã chạy → Báo lỗi (bảo vệ database)

## Cấu hình hiện tại

```properties
# Liquibase Configuration
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.yaml
spring.liquibase.enabled=true
```

- `spring.liquibase.enabled=true` → Liquibase được bật (mặc định là true)
- Nếu set `false` → Liquibase sẽ không chạy migrations

## Lưu ý quan trọng

1. **Migrations chỉ chạy khi ứng dụng khởi động**

   - Không chạy khi build (`./gradlew build`)
   - Chỉ chạy khi start ứng dụng (`make run` hoặc `./gradlew bootRun`)

2. **Idempotent**: Liquibase đảm bảo mỗi changeset chỉ chạy 1 lần

   - Nếu changeset đã chạy → Bỏ qua
   - Nếu chưa chạy → Tự động chạy

3. **Thứ tự quan trọng**

   - Migrations chạy theo thứ tự trong master changelog
   - Changeset ID cũng nên theo thứ tự (001, 002, 003...)

4. **Không sửa changeset đã chạy**
   - Nếu sửa nội dung changeset đã chạy → Liquibase sẽ báo lỗi hash mismatch
   - Giải pháp: Tạo changeset mới để sửa đổi
