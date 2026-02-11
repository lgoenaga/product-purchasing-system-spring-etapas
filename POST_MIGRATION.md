# Post-Migration Setup Guide
## Quick Start
### 1. Clone the Repository
```bash
git clone git@github.com:lgoenaga/product-purchasing-system-spring.git
cd product-purchasing-system-spring
```
### 2. View Available Branches
```bash
git branch -a
# You should see: main, etapa01, etapa02, etapa03, etapa04, etapa05, etapa06
```
### 3. Open in IntelliJ IDEA
- Open IntelliJ IDEA
- File → Open → Select the `product-purchasing-system-spring` directory
- IntelliJ will auto-detect Maven project and import dependencies
- Wait for Maven indexing to complete
### 4. Configure Environment
```bash
# Copy the example environment file
cp .env.example .env
# Edit .env with your MySQL credentials
nano .env  # or use your preferred editor
```
Required variables in `.env`:
```properties
DB_HOST=localhost
DB_PORT=3306
DB_NAME=pps_db
DB_USER=root
DB_PASSWORD=your_password_here
DB_DDL_AUTO=update
```
### 5. Setup Database
```bash
# Create database
mysql -u root -p -e "CREATE DATABASE pps_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
# Load schema and data (optional)
mysql -u root -p pps_db < src/main/resources/sql/schema.sql
mysql -u root -p pps_db < src/main/resources/sql/data.sql
```
### 6. Build the Project
```bash
mvn clean compile
```
## Working with Branches
### Checkout Different Stages
```bash
# Work with ETAPA01
git checkout etapa01
# Work with ETAPA06 (latest)
git checkout etapa06
# Return to main
git checkout main
```
## Next Steps - ETAPA07+
Starting from ETAPA07, this project will:
1. Add Spring Boot parent POM
2. Migrate to Spring Data JPA repositories
3. Implement @Transactional service layer
4. Add REST API controllers
5. Integrate Spring Security (optional)
See `DECISION_SPRING_VS_MANUAL.md` for the complete Spring migration plan.
## Documentation
- `documents_external/` - ER diagrams and model documentation (local only, not in Git)
- `ETAPA0X_SUMMARY.md` - Detailed summary of each development stage
- `CONFIG_SETUP.md` - Environment configuration guide
## Troubleshooting
### Maven Dependencies Not Resolving
```bash
mvn clean install -U
```
### IntelliJ Not Recognizing Java Classes
- File → Invalidate Caches / Restart
- Right-click `pom.xml` → Maven → Reload Project
### Database Connection Issues
- Verify MySQL is running: `sudo systemctl status mysql`
- Check `.env` file has correct credentials
- Ensure database `pps_db` exists
