#!/bin/bash
# Simple Load Test for SalesForge API

API_URL=${1:-"http://localhost:8080"}
CONCURRENT_USERS=${2:-5}
REQUESTS_PER_USER=${3:-10}
TOTAL_REQUESTS=$((CONCURRENT_USERS * REQUESTS_PER_USER))

echo "🚀 SalesForge API Load Test"
echo "=========================="
echo "API URL: $API_URL"
echo "Concurrent Users: $CONCURRENT_USERS"
echo "Requests per User: $REQUESTS_PER_USER"
echo "Total Requests: $TOTAL_REQUESTS"
echo ""

# Test endpoints
endpoints=(
    "/actuator/health"
    "/api/v1/auth/health"
    "/v3/api-docs"
)

echo "📊 Starting load test..."
start_time=$(date +%s)

# Function to make requests
make_requests() {
    local user_id=$1
    local success_count=0
    local error_count=0
    
    for i in $(seq 1 $REQUESTS_PER_USER); do
        endpoint=${endpoints[$((RANDOM % ${#endpoints[@]}))]}
        
        response_code=$(curl -s -o /dev/null -w "%{http_code}" --max-time 5 "$API_URL$endpoint" 2>/dev/null || echo "000")
        
        if [ "$response_code" -ge 200 ] && [ "$response_code" -lt 300 ]; then
            ((success_count++))
        else
            ((error_count++))
        fi
        
        # Small delay to avoid overwhelming the server
        sleep 0.1
    done
    
    echo "User $user_id: $success_count success, $error_count errors"
}

# Start concurrent users
for user in $(seq 1 $CONCURRENT_USERS); do
    make_requests $user &
done

# Wait for all background processes
wait

end_time=$(date +%s)
duration=$((end_time - start_time))

echo ""
echo "=========================="
echo "🏁 Load Test Complete!"
echo "Total Duration: ${duration}s"
echo "Average RPS: $((TOTAL_REQUESTS / duration))"
echo ""
echo "✅ If no errors appeared above, the API handled the load successfully!"