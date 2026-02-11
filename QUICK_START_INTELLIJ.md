# Quick Start Guide - IntelliJ IDEA
## 🚀 Apertura Rápida en IntelliJ
### Opción 1: Desde IntelliJ
1. Abrir IntelliJ IDEA
2. **File → Open**
3. Navegar a: `/tmp/product-purchasing-system-spring`
4. Seleccionar el directorio y hacer clic en **OK**
### Opción 2: Desde Terminal
```bash
idea /tmp/product-purchasing-system-spring
```
---
## ⚙️ Configuración Automática
IntelliJ detectará automáticamente:
- ✅ **Proyecto Maven** (por `pom.xml`)
- ✅ **Java SDK 17** (configurado en `.idea/misc.xml`)
- ✅ **Git VCS** (configurado en `.idea/vcs.xml`)
- ✅ **Encoding UTF-8** (configurado en `.idea/encodings.xml`)
**Espera** a que IntelliJ:
- Indexe los archivos
- Descargue las dependencias Maven
- Configure el proyecto
Esto puede tomar 1-3 minutos la primera vez.
---
## 📋 Checklist Post-Apertura
### 1. Verificar SDK Java
**File → Project Structure → Project Settings → Project**
- **SDK:** Debe estar en Java 17
- **Language Level:** 17
Si no está configurado:
- Click en **SDK:** → Add SDK → Download JDK
- Seleccionar **Version 17** (Oracle OpenJDK o Amazon Corretto)
### 2. Recargar Proyecto Maven
**Click derecho en `pom.xml` → Maven → Reload Project**
Esto descargará todas las dependencias:
- Hibernate 6.4.4
- MySQL Connector 8.3.0
- SLF4J + Logback
- Jakarta Persistence API
### 3. Verificar Estructura del Proyecto
En el panel izquierdo deberías ver:
```
product-purchasing-system-spring
├── src/main/java/co/edu/cesde/pps/
│   ├── config/
│   ├── dto/
│   ├── enums/
│   ├── exception/
│   ├── mapper/
│   ├── model/
│   ├── repository/
│   ├── service/
│   └── util/
├── src/main/resources/
│   ├── META-INF/persistence.xml
│   ├── logback.xml
│   └── sql/
└── pom.xml
```
---
## 🗄️ Configurar Base de Datos
### 1. Copiar Variables de Entorno
Desde el terminal integrado de IntelliJ (**View → Tool Windows → Terminal**):
```bash
cp .env.example .env
nano .env  # o usar editor de IntelliJ
```
### 2. Editar `.env`
Configurar con tus credenciales MySQL:
```properties
DB_HOST=localhost
DB_PORT=3306
DB_NAME=pps_db
DB_USER=root
DB_PASSWORD=TU_PASSWORD_AQUI
DB_DDL_AUTO=update
DB_SHOW_SQL=true
APP_ENVIRONMENT=development
LOG_LEVEL=DEBUG
```
### 3. Crear Base de Datos
Desde terminal:
```bash
# Crear base de datos
mysql -u root -p -e "CREATE DATABASE pps_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
# Cargar schema
mysql -u root -p pps_db < src/main/resources/sql/schema.sql
# Cargar datos de prueba (opcional)
mysql -u root -p pps_db < src/main/resources/sql/data.sql
```
### 4. Configurar Variables de Entorno en Run Configuration
**Run → Edit Configurations → Add New Configuration → Application**
**Environment variables:**
```
DB_HOST=localhost;DB_PORT=3306;DB_NAME=pps_db;DB_USER=root;DB_PASSWORD=tu_password;DB_DDL_AUTO=update;DB_SHOW_SQL=true
```
O usar el plugin **EnvFile**:
1. Instalar plugin: **File → Settings → Plugins → Marketplace → "EnvFile"**
2. En Run Configuration: **Tab "EnvFile" → Enable EnvFile → Add `.env`**
---
## 🔨 Compilar el Proyecto
### Desde IntelliJ
**Build → Build Project** (Ctrl+F9 / Cmd+F9)
### Desde Terminal Integrado
```bash
mvn clean compile
```
**Resultado esperado:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: X s
```
---
## 🌿 Trabajar con Branches
### Ver Branches Disponibles
**Git → Branches** (o Alt+` → 4)
Verás:
- **main** (actual)
- **etapa01** - POJOs básicos
- **etapa02** - Relaciones
- **etapa03** - Exceptions
- **etapa04** - DTOs
- **etapa05** - Services
- **etapa06** - JPA/Hibernate
### Cambiar de Branch
1. **Git → Branches**
2. Seleccionar branch (ej: `etapa01`)
3. **Checkout**
IntelliJ actualizará automáticamente el proyecto.
### Comparar Branches
**Git → Show Git Log** (Alt+9)
- Ver historial completo
- Comparar commits entre branches
- Ver cambios introducidos en cada etapa
---
## 🧪 Testing Rápido
### Crear un Test Básico
1. **src/test/java/** → Click derecho → New → Java Class
2. Crear `ConfigTest.java`:
```java
package co.edu.cesde.pps.config;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class ConfigTest {
    @Test
    public void testDatabaseConfig() {
        DatabaseConfig config = new DatabaseConfig();
        assertNotNull(config);
    }
}
```
3. Click derecho en el test → **Run 'ConfigTest'**
---
## 🔍 Explorar el Código
### Clases Principales para Revisar
#### Modelos (Entities)
- `User.java` - Usuario del sistema
- `Product.java` - Producto
- `Cart.java` - Carrito de compras
- `Order.java` - Orden de compra
#### DTOs
- `UserDTO.java`
- `ProductDTO.java`
- `CartDTO.java`
#### Services
- `UserService.java`
- `ProductService.java`
- `CartService.java`
#### Mappers
- `UserMapper.java` - Entity ↔ DTO
- `ProductMapper.java`
#### Exceptions
- `EntityNotFoundException.java`
- `ValidationException.java`
---
## 📊 Herramientas Útiles de IntelliJ
### Database Tool Window
**View → Tool Windows → Database**
- Conectar a MySQL
- Explorar tablas
- Ejecutar queries
### Maven Tool Window
**View → Tool Windows → Maven**
- Ver dependencias
- Ejecutar goals (clean, compile, install)
- Ver dependency tree
### Git Tool Window
**View → Tool Windows → Git** (Alt+9)
- Ver historial de commits
- Branches
- Cambios locales
### Structure Window
**View → Tool Windows → Structure** (Alt+7)
- Ver métodos y campos de la clase actual
- Navegación rápida
---
## 🚨 Troubleshooting Común
### Problema: "Cannot resolve symbol"
**Solución:**
```bash
File → Invalidate Caches / Restart
```
### Problema: Maven no descarga dependencias
**Solución:**
```bash
mvn clean install -U
# o
Right-click pom.xml → Maven → Reload Project
```
### Problema: Java SDK no detectado
**Solución:**
```bash
File → Project Structure → Project SDK → Add SDK → Download JDK
# Seleccionar Java 17
```
### Problema: Git no detectado
**Solución:**
```bash
File → Settings → Version Control → Git
# Configurar path a git: /usr/bin/git
```
---
## 🎯 Próximos Pasos
Una vez que el proyecto compile correctamente:
1. **Familiarízate con la estructura**
   - Explora las carpetas
   - Lee los archivos SUMMARY de cada etapa
   - Revisa el código de las clases principales
2. **Experimenta con branches**
   - Checkout a `etapa01` para ver la primera versión
   - Compara con `etapa06` para ver la evolución
3. **Lee la documentación**
   - `DECISION_SPRING_VS_MANUAL.md` - Decisión arquitectónica
   - `POST_MIGRATION.md` - Guía detallada
   - `ETAPA0X_SUMMARY.md` - Detalles de cada etapa
4. **Prepárate para ETAPA07**
   - A partir de aquí se implementará Spring Framework
   - Spring Boot
   - Spring Data JPA
   - REST Controllers
---
## ✅ Checklist Final
- [ ] IntelliJ abrió el proyecto correctamente
- [ ] Java SDK 17 configurado
- [ ] Maven descargó todas las dependencias
- [ ] Proyecto compila sin errores
- [ ] `.env` configurado con credenciales MySQL
- [ ] Base de datos `pps_db` creada
- [ ] Puedes cambiar entre branches
- [ ] Entiendes la estructura del proyecto
---
**¡Listo para comenzar el desarrollo con Spring Framework!** 🚀
Para cualquier duda, consulta:
- `POST_MIGRATION.md` - Guía detallada
- `MIGRATION_SUCCESS_REPORT.md` - Reporte completo
- `DECISION_SPRING_VS_MANUAL.md` - Contexto arquitectónico

