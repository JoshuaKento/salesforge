# SalesForge Sprint Action Plan
**Current Status: End of Tuesday (Core CRUD Complete) → Ready for Wednesday Tasks**

## 🎯 Sprint Overview
- **Timeline**: August 3-4, 2025 (2 days remaining)
- **Current Progress**: ✅ Monday (Design) + ✅ Tuesday (Core CRUD) = **66% Complete**
- **Next Phase**: Wednesday (Auth + CI/CD) → Thursday-Sunday (Polish + Deploy)

---

## 📋 **WEDNESDAY (Aug 3): Authentication & CI/CD** 
*Target: 6-8 hours of focused development*

### **Task W1: JWT Authentication Implementation** ⚡ *Priority: CRITICAL*
**Estimated Time: 3-4 hours**

#### **Sub-tasks:**
1. **Auth Controller & DTOs** (45 min)
   ```bash
   # Create files:
   - web/src/main/java/com/example/web/controller/AuthController.java
   - web/src/main/java/com/example/web/dto/LoginRequest.java
   - web/src/main/java/com/example/web/dto/LoginResponse.java
   - web/src/main/java/com/example/web/dto/RegisterRequest.java
   ```

2. **Auth Service Implementation** (60 min)
   ```bash
   # Create files:
   - security/src/main/java/com/example/security/service/AuthService.java
   - security/src/main/java/com/example/security/service/JwtService.java
   ```

3. **User Registration Logic** (45 min)
   - Password encoding with BCrypt
   - Duplicate email validation
   - Default role assignment

4. **Login Endpoint Testing** (30 min)
   ```bash
   # Test commands:
   ./gradlew :web:bootRun --args="--spring.profiles.active=dev"
   # POST http://localhost:8080/api/v1/auth/register
   # POST http://localhost:8080/api/v1/auth/login
   ```

5. **Integration Tests** (60 min)
   ```bash
   # Create:
   - web/src/test/java/com/example/web/integration/AuthControllerIT.java
   ```

**Success Criteria:**
- ✅ User can register with email/password
- ✅ User can login and receive JWT token
- ✅ Protected endpoints require valid JWT
- ✅ Tests pass with >90% coverage

---

### **Task W2: GitHub Actions CI/CD Pipeline** ⚡ *Priority: HIGH*
**Estimated Time: 2-3 hours**

#### **Sub-tasks:**
1. **Basic CI Workflow** (60 min)
   ```yaml
   # Create: .github/workflows/ci.yml
   # Features: Multi-OS build (Ubuntu + Windows)
   # Java 21 setup, Gradle build, test execution
   ```

2. **Docker Build Integration** (45 min)
   ```yaml
   # Add to CI: Docker image build and push
   # Create: Dockerfile in web module
   # Registry: GitHub Container Registry
   ```

3. **Environment Secrets** (30 min)
   ```bash
   # GitHub Secrets to configure:
   - DB_URL, DB_USERNAME, DB_PASSWORD
   - JWT_SECRET
   - DOCKER_REGISTRY_TOKEN
   ```

4. **Branch Protection Rules** (15 min)
   - Require PR reviews
   - Require status checks to pass
   - Restrict direct pushes to main

**Success Criteria:**
- ✅ CI runs on every push/PR
- ✅ Tests pass on Ubuntu + Windows
- ✅ Docker image builds successfully
- ✅ Automated deployment pipeline ready

---

## 📋 **THURSDAY (Aug 4): Polish & Frontend** 
*Target: 6-8 hours*

### **Task T1: API Documentation** ⚡ *Priority: HIGH*
**Estimated Time: 2 hours**

1. **OpenAPI Spec Generation** (60 min)
   - Configure Springdoc annotations
   - Generate JSON spec at `/v3/api-docs`
   - Swagger UI at `/swagger-ui.html`

2. **API Documentation** (60 min)
   ```markdown
   # Create: docs/API_DOCUMENTATION.md
   # Include: Endpoint examples, curl commands
   # Postman collection export
   ```

### **Task T2: Enhanced CRUD Operations** ⚡ *Priority: MEDIUM*
**Estimated Time: 3-4 hours**

1. **Lead Management Endpoints** (90 min)
   - GET /api/v1/leads (with pagination)
   - POST /api/v1/leads (create)
   - PUT /api/v1/leads/{id} (update)
   - DELETE /api/v1/leads/{id} (soft delete)

2. **Search & Filtering** (90 min)
   - Filter by status, source, date range
   - Search by company name, contact name
   - Sort by created date, status

3. **Integration Tests** (60 min)
   - Complete CRUD test coverage
   - Edge case validation
   - Performance benchmarks

### **Task T3: Basic Frontend (Optional)** ⚡ *Priority: LOW*
**Estimated Time: 2-3 hours**

```bash
# If time permits, create simple React app:
mkdir frontend && cd frontend
npx create-react-app salesforge-ui --template typescript
# Basic login form + leads table
```

---

## 📋 **FRIDAY (Aug 4 Evening): Deployment & Final Polish**
*Target: 3-4 hours*

### **Task F1: Production Deployment** ⚡ *Priority: HIGH*
**Estimated Time: 2-3 hours**

1. **Render Deployment** (90 min)
   ```yaml
   # Create: render.yaml
   # Services: Web service + PostgreSQL
   # Environment variables configuration
   ```

2. **Database Migration** (45 min)
   - Flyway production migrations
   - Sample data insertion
   - Connection testing

3. **Production Testing** (30 min)
   - Health checks
   - API endpoint verification
   - Load testing with curl

### **Task F2: Documentation & Marketing** ⚡ *Priority: MEDIUM*
**Estimated Time: 1-2 hours**

1. **README Enhancement** (60 min)
   ```markdown
   # Update: README.md
   # Add: Live demo link, API examples
   # Include: Architecture diagrams, screenshots
   ```

2. **LinkedIn Post Draft** (30 min)
   - Sprint story narrative
   - Technical highlights
   - Live demo link + GitHub repo
   - Call-to-action for feedback

---

## 🛠️ **Development Commands Cheatsheet**

### **Start Development Server:**
```bash
cd "F:/a_Devenv/Sales Forge/salesforge/my-sfa-app"
./gradlew :web:bootRun --args="--spring.profiles.active=dev"
```

### **Run Tests:**
```bash
./gradlew test                    # All tests
./gradlew :web:test              # Web module only
./gradlew test --tests="*AuthControllerIT"  # Specific test
```

### **Build & Deploy:**
```bash
./gradlew build                  # Full build
./gradlew assemble              # Compile without tests
docker build -t salesforge .    # Docker image
docker-compose up -d            # Full stack
```

### **Database Commands:**
```bash
./gradlew flywayMigrate         # Run migrations
./gradlew flywayClean           # Clean database
./gradlew flywayInfo            # Migration status
```

---

## 🎯 **Success Metrics & Quality Gates**

### **Code Quality:**
- [ ] >90% test coverage (JaCoCo report)
- [ ] All integration tests passing
- [ ] No critical security vulnerabilities
- [ ] Clean code standards (SonarQube)

### **Functionality:**
- [ ] User registration & login working
- [ ] CRUD operations for Leads
- [ ] JWT authentication protecting endpoints
- [ ] API documentation accessible

### **DevOps:**
- [ ] CI/CD pipeline functional
- [ ] Docker containers building
- [ ] Production deployment successful
- [ ] Health checks reporting green

---

## ⚡ **Quick Start Commands**

### **Immediate Next Actions:**
```bash
# 1. Start implementing auth endpoints
cd "F:/a_Devenv/Sales Forge/salesforge"
mkdir -p my-sfa-app/web/src/main/java/com/example/web/controller
touch my-sfa-app/web/src/main/java/com/example/web/controller/AuthController.java

# 2. Create GitHub workflow
mkdir -p .github/workflows
touch .github/workflows/ci.yml

# 3. Test current build
cd my-sfa-app && ./gradlew clean build
```

---

## 🚨 **Risk Mitigation**

### **High-Risk Areas:**
1. **Database Connection Issues** → Use Testcontainers for local testing
2. **JWT Configuration Complexity** → Start with simple implementation
3. **CI/CD Pipeline Failures** → Test locally before pushing
4. **Deployment Environment Issues** → Have rollback plan ready

### **Time Management:**
- **Critical Path**: Auth → CI/CD → Deployment
- **Nice-to-Have**: Advanced features, UI polish
- **Buffer Time**: 25% contingency for debugging

---

## 📞 **Support Resources**

### **Documentation:**
- Spring Security Reference: https://docs.spring.io/spring-security/reference/
- Testcontainers Guide: https://testcontainers.com/guides/
- GitHub Actions: https://docs.github.com/en/actions

### **Debugging:**
- Application logs: `./gradlew :web:bootRun --debug`
- Test failures: `./gradlew test --info`
- Docker issues: `docker logs [container-id]`

---

**📝 Last Updated:** August 3, 2025  
**📍 Current Status:** Ready to implement JWT authentication  
**⏱️ Estimated Completion:** August 4, 2025 evening

---
*🤖 Generated with [Claude Code](https://claude.ai/code)*