#!/bin/bash
# Production Health Check Script for SalesForge API

set -e

API_URL=${1:-"http://localhost:8080"}
TIMEOUT=${2:-10}

echo "🔍 SalesForge API Health Check"
echo "================================"
echo "API URL: $API_URL"
echo "Timeout: ${TIMEOUT}s"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to check endpoint
check_endpoint() {
    local endpoint=$1
    local description=$2
    local expected_status=${3:-200}
    
    echo -n "📡 Checking $description... "
    
    response=$(curl -s -w "\n%{http_code}" --max-time $TIMEOUT "$API_URL$endpoint" 2>/dev/null || echo "000")
    status_code=$(echo "$response" | tail -1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$status_code" = "$expected_status" ]; then
        echo -e "${GREEN}✅ OK ($status_code)${NC}"
        return 0
    else
        echo -e "${RED}❌ FAILED ($status_code)${NC}"
        if [ "$status_code" != "000" ] && [ -n "$body" ]; then
            echo "   Response: $body"
        fi
        return 1
    fi
}

# Function to check JSON endpoint
check_json_endpoint() {
    local endpoint=$1
    local description=$2
    local key_check=$3
    
    echo -n "🔍 Checking $description... "
    
    response=$(curl -s --max-time $TIMEOUT "$API_URL$endpoint" 2>/dev/null || echo "")
    
    if [ -n "$response" ]; then
        if echo "$response" | jq -e "$key_check" > /dev/null 2>&1; then
            echo -e "${GREEN}✅ OK${NC}"
            return 0
        else
            echo -e "${YELLOW}⚠️  PARTIAL (response received but key missing)${NC}"
            echo "   Response: $response"
            return 1
        fi
    else
        echo -e "${RED}❌ FAILED (no response)${NC}"
        return 1
    fi
}

# Start health checks
echo "Starting health checks..."
echo ""

failed_checks=0

# Basic health check
if check_endpoint "/actuator/health" "Application Health"; then
    echo "   ✓ Application is running"
else
    ((failed_checks++))
fi
echo ""

# Database health
if check_json_endpoint "/actuator/health" "Database Health" '.components.db.status == "UP"'; then
    echo "   ✓ Database connection is healthy"
else
    ((failed_checks++))
fi
echo ""

# Auth endpoints
if check_endpoint "/api/v1/auth/health" "Auth Service Health"; then
    echo "   ✓ Authentication service is available"
else
    ((failed_checks++))
fi
echo ""

# API documentation
if check_endpoint "/swagger-ui.html" "API Documentation" 200; then
    echo "   ✓ API documentation is accessible"
else
    ((failed_checks++))
fi
echo ""

# OpenAPI spec
if check_endpoint "/v3/api-docs" "OpenAPI Specification"; then
    echo "   ✓ OpenAPI specification is available"
else
    ((failed_checks++))
fi
echo ""

# Test API endpoints (should require auth - expect 401)
if check_endpoint "/api/v1/leads" "Protected API (Leads)" 401; then
    echo "   ✓ API security is working (unauthorized access blocked)"
else
    echo "   ⚠️  API security check inconclusive"
    ((failed_checks++))
fi
echo ""

# Summary
echo "================================"
if [ $failed_checks -eq 0 ]; then
    echo -e "${GREEN}🎉 All health checks passed! ($failed_checks failures)${NC}"
    echo -e "${GREEN}✅ SalesForge API is healthy and ready for production${NC}"
    exit 0
else
    echo -e "${RED}❌ Health check failed! ($failed_checks failures)${NC}"
    echo -e "${RED}🚨 SalesForge API has issues that need attention${NC}"
    exit 1
fi