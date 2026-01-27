#!/bin/bash
set -e

echo "📦 Seeding MongoDB catalog..."

mongoimport \
    --db "$MONGO_INITDB_DATABASE" \
    --collection categories \
    --file /docker-entrypoint-initdb.d/categories.json \
    --jsonArray

mongoimport \
    --db "$MONGO_INITDB_DATABASE" \
    --collection products \
    --file /docker-entrypoint-initdb.d/products.json \
    --jsonArray

echo "✅ MongoDB catalog seeded successfully."