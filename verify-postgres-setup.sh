#!/bin/bash OR powershell

# ============================================
# PostgreSQL Setup Verification Script
# DOSW-Library
# ============================================

echo "🔍 DOSW-Library PostgreSQL Setup Verification"
echo "=============================================="
echo ""

# Check 1: PostgreSQL Installation
echo "✓ Checking PostgreSQL installation..."
if command -v psql &> /dev/null; then
    psql_version=$(psql --version)
    echo "✅ PostgreSQL installed: $psql_version"
else
    echo "❌ PostgreSQL NOT found. Install from: https://www.postgresql.org/download/windows/"
    exit 1
fi

# Check 2: PostgreSQL Service Running
echo ""
echo "✓ Checking PostgreSQL service..."
if pg_isready -h localhost -p 5432 > /dev/null 2>&1; then
    echo "✅ PostgreSQL service is running on port 5432"
else
    echo "❌ PostgreSQL service NOT running. Start it manually or restart the service."
    exit 1
fi

# Check 3: Database exists
echo ""
echo "✓ Checking for dosw_library_db..."
if psql -U postgres -tc "SELECT 1 FROM pg_database WHERE datname = 'dosw_library_db'" | grep -q 1; then
    echo "✅ Database 'dosw_library_db' exists"
else
    echo "❌ Database 'dosw_library_db' NOT found. Create it with:"
    echo "   psql -U postgres -c \"CREATE DATABASE dosw_library_db;\""
    exit 1
fi

# Check 4: application.yaml exists
echo ""
echo "✓ Checking application.yaml..."
if [ -f "src/main/resources/application.yaml" ]; then
    echo "✅ application.yaml found"
else
    echo "❌ application.yaml NOT found in src/main/resources/"
    exit 1
fi

# Check 5: Maven can build
echo ""
echo "✓ Checking Maven..."
if mvn --version > /dev/null 2>&1; then
    maven_version=$(mvn --version | head -n 1)
    echo "✅ Maven available: $maven_version"
else
    echo "❌ Maven NOT found."
    exit 1
fi

echo ""
echo "=============================================="
echo "✅ ALL CHECKS PASSED!"
echo "=============================================="
echo ""
echo "Next step: Run 'mvn clean spring-boot:run'"
echo ""
