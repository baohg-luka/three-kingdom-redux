#!/bin/bash

# Script để tạo Liquibase migration file mới (SQL format)
# Usage: ./scripts/create-migration.sh <number> <description>
# Example: ./scripts/create-migration.sh 003 create-orders-table

if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <number> <description>"
    echo "Example: $0 003 create-orders-table"
    exit 1
fi

NUMBER=$1
DESCRIPTION=$2
FILENAME="${NUMBER}-${DESCRIPTION}.sql"
FILEPATH="src/main/resources/db/changelog/changes/${FILENAME}"

# Check if file already exists
if [ -f "$FILEPATH" ]; then
    echo "Error: File $FILEPATH already exists!"
    exit 1
fi

# Create file from SQL template
cat > "$FILEPATH" << EOF
-- liquibase formatted sql

-- changeset three-kingdom-team:${NUMBER}-${DESCRIPTION}
-- comment: Describe what this changeset does

-- Add your SQL changes here
-- Example:
-- CREATE TABLE table_name (
--     id BIGSERIAL PRIMARY KEY,
--     name VARCHAR(255) NOT NULL
-- );

EOF

echo "✓ Created migration file: $FILEPATH"
echo ""
echo "Next steps:"
echo "1. Edit $FILEPATH and add your SQL changes"
echo "2. Add this line to db/changelog/db.changelog-master.yaml:"
echo "   - include:"
echo "       file: db/changelog/changes/${FILENAME}"
echo "3. Test with: make docker-up && make run"
