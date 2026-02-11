# Resumen Etapa 06 - Configuración Hibernate/JPA/MySQL + Logging

## ✅ Completado - Fecha: 03 de febrero de 2026

### 📦 Objetivo de la Etapa

Configurar el ambiente de persistencia con **JPA/Hibernate + MySQL + Logging** SIN usar Spring Framework. Esta etapa prepara la infraestructura para que en la Etapa 07 se agreguen las anotaciones @Entity y las interfaces Repository.

**Enfoque**: Configuración pura con archivos XML y properties (NO application.properties de Spring).

---

## 🎯 ¿Qué se implementó?

### 1️⃣ **Dependencias Maven** (pom.xml)

```xml
<dependencies>
    <!-- Hibernate Core 6.4.4.Final -->
    <!-- Jakarta Persistence API 3.1.0 -->
    <!-- MySQL Connector/J 8.3.0 -->
    <!-- SLF4J API 2.0.9 -->
    <!-- Logback Classic 1.4.14 -->
    <!-- Logback Core 1.4.14 -->
</dependencies>
```

**Versiones utilizadas**:
- ✅ Hibernate 6.4.4 (última versión estable)
- ✅ MySQL Connector 8.3.0 (compatible con MySQL 8.x)
- ✅ SLF4J + Logback (logging robusto)
- ✅ Jakarta Persistence API 3.1.0 (JPA specification)

**NO incluye**:
- ❌ Spring Framework
- ❌ Spring Boot
- ❌ Spring Data JPA

---

### 2️⃣ **persistence.xml** (Configuración JPA)

**Ubicación**: `src/main/resources/META-INF/persistence.xml`

**Características**:
```xml
<persistence-unit name="pps-persistence-unit" transaction-type="RESOURCE_LOCAL">
    <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
    
    <!-- Propiedades de conexión -->
    <property name="jakarta.persistence.jdbc.driver" value="com.mysql.cj.jdbc.Driver"/>
    <property name="jakarta.persistence.jdbc.url" value="jdbc:mysql://..."/>
    <property name="jakarta.persistence.jdbc.user" value="${DB_USER}"/>
    <property name="jakarta.persistence.jdbc.password" value="${DB_PASSWORD}"/>
    
    <!-- Propiedades de Hibernate -->
    <property name="hibernate.dialect" value="org.hibernate.dialect.MySQL8Dialect"/>
    <property name="hibernate.hbm2ddl.auto" value="update"/>
    <property name="hibernate.show_sql" value="true"/>
    <property name="hibernate.format_sql" value="true"/>
    
    <!-- Connection Pool - HikariCP -->
    <property name="hibernate.hikari.maximumPoolSize" value="20"/>
    <!-- ... más propiedades ... -->
</persistence-unit>
```

**Estrategias DDL**:
- `create`: Borra y recrea (desarrollo inicial)
- `update`: Actualiza esquema (desarrollo) ← **Usado en Etapa 06**
- `validate`: Solo valida (testing)
- `none`: No toca esquema (producción)

---

### 3️⃣ **Variables de Entorno** (.env)

**Archivo**: `.env.example` (template)

```properties
# Base de datos
DB_HOST=localhost
DB_PORT=3306
DB_NAME=pps_db
DB_USER=root
DB_PASSWORD=

# Hibernate
DB_DDL_AUTO=update
DB_SHOW_SQL=true
DB_POOL_SIZE=10

# Aplicación
APP_ENVIRONMENT=development

# Logging
LOG_LEVEL=DEBUG
```

**Seguridad**:
- ✅ `.env` está en `.gitignore`
- ✅ `.env.example` se incluye como template
- ✅ Cada desarrollador crea su propio `.env` local
- ✅ Credenciales NO se suben a Git

**Uso**:
```bash
# Copiar template
cp .env.example .env

# Editar con credenciales reales
nano .env

# Cargar en IDE o terminal
export $(cat .env | xargs)
```

---

### 4️⃣ **Logging** (logback.xml)

**Ubicación**: `src/main/resources/logback.xml`

**Appenders configurados**:

| Appender | Archivo | Nivel | Propósito |
|----------|---------|-------|-----------|
| CONSOLE | - | DEBUG | Salida en consola coloreada |
| FILE_INFO | logs/application.log | INFO | Log general de aplicación |
| FILE_ERROR | logs/error.log | ERROR | Solo errores |
| FILE_SQL | logs/sql.log | DEBUG | Queries SQL + bind params |

**Loggers configurados**:

| Logger | Nivel | Propósito |
|--------|-------|-----------|
| co.edu.cesde.pps | DEBUG | Código de aplicación |
| org.hibernate | INFO | Framework Hibernate |
| org.hibernate.SQL | DEBUG | SQL generado |
| org.hibernate.orm.jdbc.bind | TRACE | Bind parameters |
| org.hibernate.transaction | DEBUG | Transacciones |
| com.zaxxer.hikari | INFO | Connection pool |
| com.mysql.cj | WARN | MySQL connector |

**Rotación de logs**:
- Rotación diaria
- application.log: 30 días, max 1GB
- error.log: 90 días, max 500MB
- sql.log: 7 días, max 200MB

---

### 5️⃣ **Scripts SQL**

#### **schema.sql** (DDL)

**Ubicación**: `src/main/resources/sql/schema.sql`

**Contenido**:
- 14 tablas con relaciones completas
- Índices en columnas frecuentes
- Foreign keys con CASCADE
- Charset UTF-8 (utf8mb4)
- Engine InnoDB

**Tablas creadas**:
```sql
roles, users, addresses, categories, products, 
user_sessions, carts, cart_items, order_statuses, 
orders, order_items, payment_methods, payment_statuses, payments
```

#### **data.sql** (DML)

**Ubicación**: `src/main/resources/sql/data.sql`

**Datos iniciales**:
- 3 roles (ADMIN, CUSTOMER, MANAGER)
- 7 order statuses (PENDING, CONFIRMED, etc.)
- 5 payment methods (CREDIT_CARD, PAYPAL, etc.)
- 6 payment statuses (PENDING, COMPLETED, etc.)
- Jerarquía de categorías (Electronics, Clothing, Books, Home & Garden)
- 13 productos de ejemplo
- 4 usuarios de prueba con direcciones

**Uso**:
```bash
# Ejecutar DDL
mysql -u root -p < src/main/resources/sql/schema.sql

# Ejecutar DML
mysql -u root -p pps_db < src/main/resources/sql/data.sql
```

**Alternativa**:
- Dejar que Hibernate cree las tablas (`hbm2ddl.auto=update`)
- Solo ejecutar data.sql para datos iniciales

---

## 📁 Estructura de Archivos Creados/Modificados

```
product-purchasing-system/
├── pom.xml (actualizado con dependencias)
├── .env.example (nuevo - template)
├── .gitignore (actualizado - ignora .env)
├── CONFIG_SETUP.md (nuevo - guía de setup)
│
└── src/main/resources/
    ├── META-INF/
    │   └── persistence.xml (nuevo)
    ├── logback.xml (nuevo)
    └── sql/
        ├── schema.sql (nuevo)
        └── data.sql (nuevo)
```

**Total de archivos nuevos**: 6  
**Archivos modificados**: 2 (pom.xml, .gitignore)

---

## 🔧 Configuración por Ambiente

### Desarrollo (development)
```properties
DB_DDL_AUTO=update          # Auto-crea tablas
DB_SHOW_SQL=true            # Muestra SQL
LOG_LEVEL=DEBUG             # Logs detallados
hibernate.show_sql=true     # SQL en consola
hibernate.format_sql=true   # SQL formateado
```

### Staging (staging)
```properties
DB_DDL_AUTO=validate        # Solo valida
DB_SHOW_SQL=true            # Muestra SQL
LOG_LEVEL=INFO              # Logs normales
hibernate.show_sql=false    # Sin SQL
```

### Producción (production)
```properties
DB_DDL_AUTO=none            # No toca esquema
DB_SHOW_SQL=false           # Sin SQL
LOG_LEVEL=WARN              # Solo warnings/errors
hibernate.show_sql=false    # Sin SQL
hibernate.format_sql=false  # Sin formateo
```

---

## 📦 Commits Realizados (6 commits)

```bash
1. build: add Hibernate, JPA, MySQL and Logging dependencies
2. config: add persistence.xml for JPA configuration
3. config: add environment variables template and update gitignore
4. config: add logback.xml for logging configuration
5. config: add SQL scripts for database initialization
6. docs: add CONFIG_SETUP guide for environment configuration
```

---

## ✅ Verificación de Configuración

### Checklist de validación:

- [ ] **MySQL instalado y corriendo**
  ```bash
  mysql --version
  sudo systemctl status mysql  # Linux
  ```

- [ ] **Base de datos creada**
  ```sql
  CREATE DATABASE pps_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  ```

- [ ] **Archivo .env configurado**
  ```bash
  cp .env.example .env
  nano .env  # Ajustar credenciales
  ```

- [ ] **Dependencias Maven descargadas**
  ```bash
  mvn clean install
  ```

- [ ] **Proyecto compila sin errores**
  ```bash
  mvn clean compile
  # BUILD SUCCESS
  ```

- [ ] **Variables de entorno cargadas**
  ```bash
  # En IDE o terminal
  export DB_HOST=localhost
  export DB_USER=root
  # etc...
  ```

---

## 🚫 Lo que NO se implementó (intencionalmente)

Esta etapa es **SOLO configuración**, por lo tanto NO incluye:

- ❌ Anotaciones `@Entity` en modelos
- ❌ Anotaciones `@Table`, `@Column`, `@Id`, etc.
- ❌ Anotaciones `@ManyToOne`, `@OneToMany`, etc.
- ❌ Interfaces `Repository`
- ❌ Anotaciones `@Service` en servicios
- ❌ Anotaciones `@Transactional`
- ❌ EntityManagerFactory programático
- ❌ Spring Framework o Spring Boot
- ❌ application.properties (es de Spring)

**Razón**: Separar configuración (Etapa 06) de implementación (Etapa 07) permite:
- Validar configuración independientemente
- Commits más limpios y específicos
- Fácil rollback si hay problemas
- Mejor comprensión del setup

---

## 🎯 Beneficios de esta Configuración

### 1️⃣ **Sin Spring Framework**
- Menor overhead
- Aprendizaje de JPA puro
- Mayor control sobre configuración
- Útil para proyectos legacy o no-Spring

### 2️⃣ **Variables de Entorno**
- Seguridad: credenciales fuera del código
- Flexibilidad: diferentes ambientes sin cambiar código
- Buenas prácticas: separación de configuración

### 3️⃣ **Logging Robusto**
- Múltiples appenders (consola + archivos)
- Rotación automática
- Logs separados por tipo (general, error, SQL)
- Control fino de niveles por paquete

### 4️⃣ **Scripts SQL de Referencia**
- Documentación de esquema
- Útil para producción (DDL manual)
- Datos iniciales reproducibles
- Independiente de Hibernate

### 5️⃣ **Preparación para JPA**
- persistence.xml listo
- Dialecto MySQL8 configurado
- Connection pool configurado
- Solo falta agregar @Entity

---

## 🔍 Cómo Funciona la Configuración

### 1. Carga de Variables de Entorno

```bash
# Terminal
export DB_HOST=localhost
export DB_USER=root

# O desde .env
source .env
export $(cat .env | xargs)
```

### 2. Hibernate Lee persistence.xml

```xml
<property name="jakarta.persistence.jdbc.url" 
          value="jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:pps_db}?..."/>
```

**Nota**: Las variables `${...}` se resolverán programáticamente en DatabaseConfig (Etapa 07).

### 3. Logback Lee logback.xml

```xml
<logger name="co.edu.cesde.pps" level="DEBUG"/>
<logger name="org.hibernate.SQL" level="DEBUG"/>
```

### 4. Hibernate Crea Tablas

Con `hibernate.hbm2ddl.auto=update`:
- Lee entidades anotadas con @Entity (Etapa 07)
- Compara con esquema existente
- Crea/actualiza tablas automáticamente

---

## 🛠️ Troubleshooting

### Error: "Access denied for user 'root'@'localhost'"
```
Causa: Contraseña incorrecta o usuario no existe
Solución: 
1. Verificar DB_USER y DB_PASSWORD en .env
2. Resetear password de MySQL si es necesario
```

### Error: "Unknown database 'pps_db'"
```
Causa: Base de datos no creada
Solución:
CREATE DATABASE pps_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Error: "Cannot resolve symbol 'JpaRepository'"
```
Causa: Dependencias no descargadas o no es problema aún
Solución: Este es normal en Etapa 06 (sin interfaces Repository)
```

### Error: "Communications link failure"
```
Causa: MySQL no está corriendo
Solución:
sudo systemctl start mysql  # Linux
mysql.server start          # macOS
```

### Advertencia: "HikariPool-1 - Failed to validate connection"
```
Causa: Credenciales incorrectas o MySQL inaccesible
Solución: Verificar .env y que MySQL esté corriendo
```

---

## 📚 Archivos de Configuración Explicados

### persistence.xml
- **Ubicación**: `src/main/resources/META-INF/`
- **Propósito**: Configuración de JPA (sin Spring)
- **Clave**: Define persistence-unit para EntityManagerFactory

### logback.xml
- **Ubicación**: `src/main/resources/`
- **Propósito**: Configuración de logging (SLF4J + Logback)
- **Clave**: Appenders, loggers, niveles, rotación

### .env.example
- **Ubicación**: Raíz del proyecto
- **Propósito**: Template de variables de entorno
- **Clave**: NO contiene credenciales reales

### schema.sql
- **Ubicación**: `src/main/resources/sql/`
- **Propósito**: DDL de referencia
- **Clave**: Opcional si usas `hbm2ddl.auto=update`

### data.sql
- **Ubicación**: `src/main/resources/sql/`
- **Propósito**: Datos iniciales
- **Clave**: Roles, statuses, categorías, productos de ejemplo

---

## 🚀 Próximos Pasos: Etapa 07

Con la configuración lista, la Etapa 07 implementará:

### 1️⃣ Anotaciones JPA en Entidades
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;
    
    // ...más anotaciones...
}
```

### 2️⃣ Interfaces Repository
```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByStatus(UserStatus status);
}
```

### 3️⃣ Actualizar Services
```java
@Service
public class UserService {
    
    private final UserRepository userRepository;
    
    @Autowired
    public UserService(UserRepository userRepository, UserMapper mapper) {
        this.userRepository = userRepository;
        // ...
    }
    
    @Transactional
    public UserDTO registerUser(...) {
        // Reemplazar usersInMemory por userRepository
        userRepository.save(user);
    }
}
```

### 4️⃣ EntityManagerFactory (si no usas Spring Data)
```java
EntityManagerFactory emf = Persistence.createEntityManagerFactory("pps-persistence-unit");
EntityManager em = emf.createEntityManager();
```

---

## 🌿 Estado de Git

- **Rama actual:** `etapa06`
- **Rama base:** `etapa05`
- **Commits:** 6 commits
- **Estado:** Listo para push a GitHub
- **Repositorio:** `https://github.com/lgoenaga/product-purchasing-system`

---

## ✨ Conclusión

La **Etapa 06** ha configurado exitosamente el ambiente de persistencia:

- ✅ **Dependencias Maven**: Hibernate, JPA, MySQL, Logging
- ✅ **persistence.xml**: Configuración JPA estándar (sin Spring)
- ✅ **Variables de entorno**: .env para credenciales seguras
- ✅ **Logging**: SLF4J + Logback con múltiples appenders
- ✅ **Scripts SQL**: schema.sql y data.sql de referencia
- ✅ **Guía de setup**: CONFIG_SETUP.md completo
- ✅ **Sin errores de compilación**: BUILD SUCCESS

El proyecto está **100% listo para la Etapa 07**, donde se agregarán las anotaciones @Entity y las interfaces Repository para tener persistencia real funcionando.

**Separación exitosa de configuración vs implementación!** 🎉

---

**Autor:** Luis Goenaga  
**Proyecto:** Product Purchasing System - Backend II  
**Institución:** CESDE  
**Año:** 2026
