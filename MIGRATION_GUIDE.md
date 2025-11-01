# Hướng dẫn Migration với Liquibase (SQL format)

Project này sử dụng **SQL format** cho tất cả migrations - đơn giản, quen thuộc và dễ viết.

## Quy trình chuẩn khi thêm Entity mới

### Bước 1: Tạo Entity class với JPA annotations

Tạo entity trong package `com.example.three_kingdom_backend.entity` (hoặc package entities của bạn)

Ví dụ:

```java
package com.example.three_kingdom_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

### Bước 2: Tạo ChangeLog file mới cho migration

**Cách 1: Dùng script tự động (Khuyến nghị)**

```bash
./scripts/create-migration.sh 002 create-users-table
```

**Cách 2: Tạo thủ công**
Tạo file mới trong `src/main/resources/db/changelog/changes/` với format:

- `002-<mô-tả-ngắn>.sql` (SQL format)

Ví dụ: `002-create-users-table.sql`

### Bước 3A: Viết ChangeSet trong file SQL (Khuyến nghị)

**Format SQL - Đơn giản và quen thuộc:**

File: `002-create-users-table.sql`

```sql
-- liquibase formatted sql

-- changeset three-kingdom-team:002-create-users-table
-- comment: Create users table

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_email ON users (email);
```

**Lưu ý:**

- Dòng đầu tiên phải có: `-- liquibase formatted sql`
- Format changeset: `-- changeset author:changeset-id`
- Format comment: `-- comment: description`
- Có thể viết nhiều SQL statements trong một changeset

### Bước 4: Thêm vào Master Changelog

Mở file `db/changelog/db.changelog-master.yaml` và thêm vào cuối:

```yaml
databaseChangeLog:
  - include:
      file: db/changelog/changes/001-initial-schema.sql
  - include:
      file: db/changelog/changes/002-create-users-table.sql
  # Thêm các file khác ở đây
```

**Lưu ý:** Master changelog luôn là file YAML, nhưng include các file SQL.

### Bước 5: Test Migration

1. Start database: `make docker-up`
2. Run application: `make run`
3. Liquibase sẽ tự động chạy migration khi ứng dụng khởi động
4. Kiểm tra database: `docker exec three-kingdom-postgres psql -U three_kingdom_user -d three_kingdom_db -c "\dt"`

## Quy tắc đặt tên ChangeSet ID

- Format: `XXX-description` (ví dụ: `002-create-users-table`)
- Mỗi ChangeSet ID phải unique trong toàn bộ changelog
- Nên dùng số thứ tự để dễ theo dõi
- Description nên rõ ràng, dễ hiểu

## Các loại ChangeSet thường dùng (SQL format)

### 1. Create Table

```sql
-- liquibase formatted sql

-- changeset three-kingdom-team:003-create-orders-table
-- comment: Create orders table

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_amount DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### 2. Add Column

```sql
-- liquibase formatted sql

-- changeset three-kingdom-team:004-add-phone-to-users
-- comment: Add phone column to users table

ALTER TABLE users ADD COLUMN phone VARCHAR(20);
```

### 3. Create Index

```sql
-- liquibase formatted sql

-- changeset three-kingdom-team:005-create-username-index
-- comment: Create index on username column

CREATE INDEX idx_users_username ON users (username);
```

### 4. Add Foreign Key

```sql
-- liquibase formatted sql

-- changeset three-kingdom-team:006-add-foreign-key-orders-user
-- comment: Add foreign key constraint

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_user
    FOREIGN KEY (user_id) REFERENCES users(id);
```

### 5. Modify Column

```sql
-- liquibase formatted sql

-- changeset three-kingdom-team:007-modify-email-column
-- comment: Increase email column size

ALTER TABLE users ALTER COLUMN email TYPE VARCHAR(500);
```

### 6. Custom SQL (Update data)

```sql
-- liquibase formatted sql

-- changeset three-kingdom-team:008-update-user-status
-- comment: Set default status for existing users

UPDATE users
SET status = 'active'
WHERE status IS NULL;
```

### 7. Drop Table

```sql
-- liquibase formatted sql

-- changeset three-kingdom-team:009-drop-old-table
-- comment: Drop deprecated table

DROP TABLE IF EXISTS old_table_name;
```

### 8. Rename Column

```sql
-- liquibase formatted sql

-- changeset three-kingdom-team:010-rename-column
-- comment: Rename column name

ALTER TABLE users RENAME COLUMN old_name TO new_name;
```

## Best Practices

1. **Mỗi ChangeSet chỉ làm một việc**: Tạo một table, thêm một column, tạo một index...
2. **Luôn có comment**: Giải thích rõ ChangeSet này làm gì
3. **Test trước khi commit**: Chạy migration trên local trước khi push
4. **Không sửa ChangeSet đã chạy**: Nếu cần sửa, tạo ChangeSet mới
5. **Backup database trước migration quan trọng**: Đặc biệt khi xóa data hoặc modify structure lớn
6. **Sử dụng preConditions**: Để migration có thể chạy lại an toàn

Ví dụ preConditions trong SQL (sử dụng IF NOT EXISTS):

```sql
-- liquibase formatted sql

-- changeset three-kingdom-team:011-create-table-safe
-- comment: Create table if not exists

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);
```

Hoặc có thể kiểm tra bằng cách thêm preConditions comment (Liquibase sẽ tự xử lý):

```sql
-- liquibase formatted sql
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'users'

-- changeset three-kingdom-team:011-create-table-safe
-- comment: Create table if not exists

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);
```

## Rollback (nếu cần)

Có thể thêm rollback trong SQL changeset:

```sql
-- liquibase formatted sql

-- changeset three-kingdom-team:012-create-temp-table
-- comment: Create temp table
-- rollback DROP TABLE temp_table;

CREATE TABLE temp_table (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255)
);
```

## Tips

- Copy từ `TEMPLATE.sql` để bắt đầu nhanh
- Dùng script `./scripts/create-migration.sh` để tạo file mới
- Luôn test migration trên local trước
- Review changelog trước khi merge PR
- SQL format đơn giản hơn YAML, dễ viết và quen thuộc
