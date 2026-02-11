# ETAPA07 - Migración a Spring Boot + Lombok (Base del Proyecto)

## Objetivo
En esta etapa se realiza el primer salto formal hacia **Spring Boot** y se introduce **Lombok (Opción B)** para reducir boilerplate en el modelo (entidades JPA), manteniendo la misma estructura por etapas y commits granulares.

La meta es dejar el proyecto compilando correctamente con:
- Dependencias Spring Boot (JPA, Web, Validation)
- Lombok configurado (annotation processing en Maven)
- Clase principal `@SpringBootApplication`
- Configuración de base de datos por **profiles** con `application.properties`

> Nota: En esta etapa aún existen servicios/repositorios basados en `EntityManager` y `TransactionManager`. La migración a Spring Data JPA y `@Transactional` se abordará en ETAPA08.

---

## Cambios realizados

### 1) Dependencias y build (pom.xml)
- Se adoptó `spring-boot-starter-parent`.
- Se agregaron starters:
  - `spring-boot-starter-data-jpa`
  - `spring-boot-starter-web`
  - `spring-boot-starter-validation`
- Se agregó `mysql-connector-j` en `runtime`.
- Se agregó `lombok` con `scope=provided`.
- Se configuró `maven-compiler-plugin` con `annotationProcessorPaths` para Lombok.

### 2) Refactor de entidades (model) usando Lombok (Opción B)
Se refactorizaron entidades para eliminar:
- constructores redundantes
- getters/setters manuales
- equals/hashCode/toString manuales

**Estrategia aplicada:**
- `@Getter`, `@Setter`
- `@NoArgsConstructor`
- `@AllArgsConstructor` y `@Builder` en entidades donde aporta valor
- `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` incluyendo únicamente el ID
- `@ToString(onlyExplicitlyIncluded = true)` evitando navegación de relaciones

**Importante:**
- Se preservaron métodos de negocio (por ejemplo `calculateTotal`, `isAvailable`, etc.).
- Se preservaron callbacks JPA (`@PrePersist`, `@PreUpdate`).
- En entidades donde los **servicios existentes** dependían de constructores específicos, se mantuvieron/reenlazaron constructores de conveniencia para no romper compatibilidad.

### 3) Aplicación Spring Boot
- Se creó la clase principal:
  - `src/main/java/co/edu/cesde/pps/ProductPurchasingSystemApplication.java`

### 4) Configuración con profiles (application.properties)
Se añadieron:
- `src/main/resources/application.properties`
- `src/main/resources/application-dev.properties`
- `src/main/resources/application-prod.properties`

Con perfil por defecto: `dev`.

---

## Archivos creados/modificados

### Modificados
- `pom.xml`
- `src/main/java/co/edu/cesde/pps/model/*.java` (entidades)
- `src/main/java/co/edu/cesde/pps/service/UserService.java` (ajuste por eliminación de constructor específico)

### Creados
- `src/main/java/co/edu/cesde/pps/ProductPurchasingSystemApplication.java`
- `src/main/resources/application.properties`
- `src/main/resources/application-dev.properties`
- `src/main/resources/application-prod.properties`
- `ETAPA07_SUMMARY.md`

---

## Verificación

### Compilación
- `mvn clean compile` ✅

### Qué puede correr y qué falta
- La aplicación **puede iniciar** como Spring Boot, pero para ejecutarse completamente necesitará:
  - una base de datos MySQL accesible según el profile activo
  - (en ETAPA08) migración final desde `persistence.xml`/`TransactionManager` a configuración Spring + Spring Data JPA

---

## Archivos que comienzan a ser obsoletos (se eliminarán en ETAPA08)
Con Spring Boot, estos componentes quedarán reemplazados progresivamente:
- `src/main/resources/META-INF/persistence.xml`
- `src/main/java/co/edu/cesde/pps/config/JpaConfig.java`
- `src/main/java/co/edu/cesde/pps/config/DatabaseConfig.java`
- `src/main/java/co/edu/cesde/pps/util/TransactionManager.java`
- `src/main/java/co/edu/cesde/pps/repository/impl/*`

---

## Commits (granulares)
Esta etapa se trabajó con commits pequeños y por grupos lógicos:
- Dependencias Spring Boot + Lombok
- Entidades de catálogo
- Entidades de usuario
- Entidades de productos
- Entidades de carrito
- Entidades de órdenes/pagos
- App principal + configuración por profiles
- Documentación de etapa

---

## Siguiente etapa (ETAPA08)
- Migrar configuración JPA a Spring Boot completamente (eliminar `persistence.xml`).
- Reemplazar `TransactionManager` por `@Transactional`.
- Migrar repositorios manuales a Spring Data JPA.
- Definir estructura inicial de controllers/DTOs para exponer endpoints.

