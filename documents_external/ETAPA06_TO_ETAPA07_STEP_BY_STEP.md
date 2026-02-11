# ETAPA06 → ETAPA07: migración a Spring Boot + Lombok (paso a paso)

**Fecha de referencia:** 11 de febrero de 2026  
**Rama de trabajo:** `etapa07` (creada desde `etapa06`)  

Este documento explica cómo replicar los cambios realizados para pasar de la **ETAPA06** a la **ETAPA07**, siguiendo el mismo estilo del proyecto: **trabajo por ramas**, **tareas por etapa** y **commits granulares**.

---

## 0) Prerrequisitos

- Java 17
- Maven
- Git
- IDE: habilitar *Annotation Processing* para que Lombok funcione correctamente.

---

## 1) Crear la rama de la etapa

Desde la raíz del proyecto:

```bash
git checkout etapa06
git checkout -b etapa07
```

---

## 2) Actualizar `pom.xml` (Spring Boot + Lombok)

### 2.1. Objetivo

- Adoptar `spring-boot-starter-parent`.
- Agregar starters:
  - `spring-boot-starter-data-jpa`
  - `spring-boot-starter-web`
  - `spring-boot-starter-validation`
  - `spring-boot-starter-test` (test scope)
- Agregar MySQL driver: `mysql-connector-j` (runtime)
- Agregar Lombok:
  - dependencia `lombok` con `scope=provided`
  - `maven-compiler-plugin` con `annotationProcessorPaths` para Lombok
- Mantener Java 17.

### 2.2. Commit granular

```text
build: add Spring Boot 3.1.9 and Lombok 1.18.30 dependencies
```

---

## 3) Refactor de entidades con Lombok (Opción B)

### 3.1. Estrategia aplicada (Opción B)

En lugar de usar `@Data`, se usaron anotaciones explícitas para controlar mejor el comportamiento en entidades JPA:

- `@Getter`, `@Setter`
- `@NoArgsConstructor`
- `@AllArgsConstructor` (solo donde aplica)
- `@Builder` (solo donde aplica)
- `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` + `@EqualsAndHashCode.Include` en el ID
- `@ToString(onlyExplicitlyIncluded = true)` + `@ToString.Include` en campos seguros

**Reglas prácticas usadas en el proyecto:**

- Mantener métodos de negocio (por ejemplo: `calculateTotal()`, `isAvailable()`, etc.).
- Mantener callbacks JPA (`@PrePersist`, `@PreUpdate`).
- Evitar incluir relaciones y colecciones en `toString()`.
- `equals/hashCode` basado en ID únicamente.

---

## 4) Refactor por grupos + commits granulares

### 4.1. Grupo A: catálogos (simples)

Archivos:
- `model/Role.java`
- `model/OrderStatus.java`
- `model/PaymentStatus.java`
- `model/PaymentMethod.java`

Commit:
```text
refactor: apply Lombok to 4 catalog entities (Role, OrderStatus, PaymentStatus, PaymentMethod)
```

### 4.2. Grupo B: usuario

Archivos:
- `model/User.java`
- `model/Address.java`
- `model/UserSession.java`

Puntos clave:
- Preservar métodos helper:
  - `User.getDefaultAddress()`
  - `User.getFullName()`
  - `UserSession.isGuestSession()`
  - `UserSession.isExpired()`
- Inicializar colecciones (ej: `addresses = new ArrayList<>()`) para evitar NPE.
- Mantener `@PrePersist`.

**Ajuste adicional necesario:**
- `UserService` estaba construyendo `User` con un constructor que dejó de existir tras el refactor.
- Se reemplazó por `new User()` + setters.

Commit:
```text
refactor: apply Lombok to User, Address, UserSession
```

### 4.3. Grupo C: productos

Archivos:
- `model/Category.java`
- `model/Product.java`

Puntos clave:
- Preservar:
  - `Category.isRootCategory()`
  - `Product.isAvailable()`
- Mantener validaciones en setters críticos de `Product` (precio y stock).

Commit:
```text
refactor: apply Lombok to Category and Product
```

### 4.4. Grupo D: carrito

Archivos:
- `model/Cart.java`
- `model/CartItem.java`

Puntos clave:
- Preservar:
  - `Cart.isGuestCart()`, `Cart.isOpen()`, `Cart.calculateTotal()`, `Cart.touch()`
  - `CartItem.calculateSubtotal()`
- Mantener callbacks `@PrePersist` y `@PreUpdate`.
- **Compatibilidad con servicios existentes:**
  - `CartService` usa `new CartItem(cart, product, quantity, price)`
  - Se conservó/restauró el constructor de conveniencia en `CartItem`.

Commit:
```text
refactor: apply Lombok to Cart and CartItem
```

### 4.5. Grupo E: órdenes y pagos

Archivos:
- `model/Order.java`
- `model/OrderItem.java`
- `model/Payment.java`

Puntos clave:
- Preservar:
  - `Order.calculateTotal()`
  - `OrderItem.calculateLineTotal()`
  - `Payment.isRefund()`
- Mantener validaciones en setters.
- **Compatibilidad con servicios existentes:**
  - `OrderService` usa `new Order(orderNumber, user, status, shipping, billing)`
  - `OrderService` usa `new OrderItem(order, product, qty, unitPrice)`
  - Se conservaron/restauraron constructores de conveniencia.

Commit:
```text
refactor: apply Lombok to Order, OrderItem, Payment
```

---

## 5) Crear la aplicación Spring Boot

Crear archivo:
- `src/main/java/co/edu/cesde/pps/ProductPurchasingSystemApplication.java`

Con:
- `@SpringBootApplication`
- `main()` llamando a `SpringApplication.run(...)`

---

## 6) Configurar base de datos con profiles (`application.properties`)

Crear/editar:
- `src/main/resources/application.properties`
- `src/main/resources/application-dev.properties`
- `src/main/resources/application-prod.properties`

Regla:
- Por defecto en `application.properties`: `spring.profiles.active=dev`
- Dev: configuración local (pps_db, user_pps)
- Prod: configuración por variables de entorno

Commit:
```text
feat: create Spring Boot application and profile-based config
```

---

## 7) Documentación de la etapa

Crear/actualizar:
- `ETAPA07_SUMMARY.md`

Commit:
```text
docs: add ETAPA07 summary and update stage status
```

---

## 8) Validación

Compilación:
```bash
mvn clean compile
```

Pruebas:
```bash
mvn test
```

---

## 9) Lista final de commits granulares (ETAPA07)

> Nota: estos mensajes corresponden a los commits realizados durante la etapa.

1. `build: add Spring Boot 3.1.9 and Lombok 1.18.30 dependencies`
2. `refactor: apply Lombok to 4 catalog entities (Role, OrderStatus, PaymentStatus, PaymentMethod)`
3. `refactor: apply Lombok to User, Address, UserSession`
4. `refactor: apply Lombok to Category and Product`
5. `refactor: apply Lombok to Cart and CartItem`
6. `refactor: apply Lombok to Order, OrderItem, Payment`
7. `feat: create Spring Boot application and profile-based config`
8. `docs: add ETAPA07 summary and update stage status`

---

## 10) ¿Qué queda para ETAPA08?

- Reemplazar `TransactionManager` por `@Transactional`.
- Migrar repositorios manuales a Spring Data JPA.
- Eliminar/retirar `persistence.xml` y configs manuales (`JpaConfig`, `DatabaseConfig`).
- Preparar controllers REST.

