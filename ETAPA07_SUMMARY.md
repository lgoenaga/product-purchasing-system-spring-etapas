# Resumen Etapa 07 - Anotaciones JPA en Modelos

## ✅ Completado - Fecha: 4 de febrero de 2026

### 📦 Objetivo de la Etapa

Agregar anotaciones JPA (Jakarta Persistence API) a las 14 entidades del modelo para preparar la persistencia con Hibernate/MySQL en las siguientes etapas.

---

## 🎯 ¿Qué se implementó?

### 1️⃣ **Anotaciones JPA en 14 Entidades**

Todas las entidades del modelo ahora tienen anotaciones completas de JPA:

| Entidad | @Table | Relaciones | Características Especiales |
|---------|--------|------------|---------------------------|
| **Role** | roles | - | @PrePersist para createdAt, campo description agregado |
| **OrderStatus** | order_statuses | - | Entidad catálogo, campo description agregado |
| **PaymentMethod** | payment_methods | - | Entidad catálogo, campo description agregado |
| **PaymentStatus** | payment_statuses | - | Entidad catálogo, campo description agregado |
| **Category** | categories | @ManyToOne parent (self), @OneToMany subcategories, @OneToMany products | Auto-referencia jerárquica |
| **Product** | products | @ManyToOne category | @PrePersist, isActive default true |
| **User** | users | @ManyToOne role, @OneToMany addresses | @Enumerated UserStatus, @PrePersist |
| **Address** | addresses | @ManyToOne user | @Enumerated AddressType, @PrePersist |
| **UserSession** | user_sessions | @ManyToOne user (optional) | @PrePersist, soporta invitados (user = null) |
| **Cart** | carts | @ManyToOne user, session (optional), @OneToMany items | @PreUpdate para updatedAt, @Enumerated CartStatus |
| **CartItem** | cart_items | @ManyToOne cart, product | @PrePersist, precio congelado |
| **Order** | orders | @ManyToOne user, status, 2x Address, @OneToMany items | @PrePersist, múltiples relaciones |
| **OrderItem** | order_items | @ManyToOne order, product | Precio histórico para auditoría |
| **Payment** | payments | @ManyToOne order, method, status | @Enumerated Currency, transactionId |

---

### 2️⃣ **Tipos de Anotaciones Implementadas**

#### **Anotaciones de Clase**
- `@Entity` - Marca la clase como entidad JPA
- `@Table(name = "tabla")` - Mapeo a tabla de base de datos

#### **Anotaciones de ID**
- `@Id` - Marca la clave primaria
- `@GeneratedValue(strategy = GenerationType.IDENTITY)` - Auto-incremento
- `@Column(name = "campo")` - Mapeo de columna

#### **Anotaciones de Columna**
- `@Column(nullable = false)` - Campos obligatorios
- `@Column(unique = true)` - Campos únicos
- `@Column(length = N)` - Longitud máxima
- `@Column(precision = 10, scale = 2)` - Para BigDecimal (DECIMAL(10,2))
- `@Column(columnDefinition = "TEXT")` - Tipo de columna específico
- `@Column(updatable = false)` - Campos que no se pueden actualizar

#### **Anotaciones de Relaciones**

**@ManyToOne** (N:1):
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "foreign_key_id", nullable = false)
private ReferencedEntity entity;
```
- 18 relaciones @ManyToOne implementadas
- Fetch LAZY para optimización (carga bajo demanda)

**@OneToMany** (1:N):
```java
@OneToMany(mappedBy = "backReference", cascade = CascadeType.ALL, orphanRemoval = true)
private List<ChildEntity> children = new ArrayList<>();
```
- 5 relaciones @OneToMany implementadas
- CascadeType.ALL: operaciones en cascada
- orphanRemoval = true: eliminar huérfanos

#### **Anotaciones de Enums**
```java
@Enumerated(EnumType.STRING)
@Column(name = "status", length = 20)
private StatusEnum status;
```
- 4 campos @Enumerated: UserStatus, AddressType, CartStatus, Currency

#### **Callbacks de Ciclo de Vida**

**@PrePersist** (antes de insertar):
```java
@PrePersist
protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    // Valores por defecto
}
```
- 6 entidades usan @PrePersist

**@PreUpdate** (antes de actualizar):
```java
@PreUpdate
protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
}
```
- 1 entidad usa @PreUpdate (Cart)

---

### 3️⃣ **Relaciones Bidireccionales Implementadas**

1. **User ↔ Address**
   - User: `@OneToMany(mappedBy = "user")`
   - Address: `@ManyToOne` + `@JoinColumn(name = "user_id")`

2. **Category ↔ Category** (auto-referencia)
   - Parent: `@OneToMany(mappedBy = "parent")`
   - Child: `@ManyToOne` + `@JoinColumn(name = "parent_id")`

3. **Category ↔ Product**
   - Category: `@OneToMany(mappedBy = "category")`
   - Product: `@ManyToOne` + `@JoinColumn(name = "category_id")`

4. **Cart ↔ CartItem**
   - Cart: `@OneToMany(mappedBy = "cart")`
   - CartItem: `@ManyToOne` + `@JoinColumn(name = "cart_id")`

5. **Order ↔ OrderItem**
   - Order: `@OneToMany(mappedBy = "order")`
   - OrderItem: `@ManyToOne` + `@JoinColumn(name = "order_id")`

---

### 4️⃣ **Ajustes de Compatibilidad**

Para que el proyecto compile correctamente con las nuevas anotaciones, se realizaron ajustes en:

#### **OrderMapper.java**
- ✅ `toDTO()`: Actualizado para usar `order.getUser().getUserId()` en lugar de `order.getUserId()`
- ✅ `toDTO()`: Cargar información del usuario y estado desde entidades relacionadas
- ⚠️ `toEntity()`: Marcado como `@Deprecated` con `UnsupportedOperationException`
  - Razón: Order ahora requiere entidades completas (User, OrderStatus, Address) no IDs

#### **OrderService.java**
- ✅ Filtros actualizados para usar `o.getUser().getUserId()` en lugar de `o.getUserId()`
- ✅ Filtros actualizados para usar `o.getStatus().getOrderStatusId()` en lugar de `o.getOrderStatusId()`
- 📝 TODO agregados para Etapa 08 (implementación de repositories)

---

## 📊 Estadísticas

### **Entidades Anotadas**
- ✅ 14 entidades con anotaciones JPA completas
- ✅ 6 commits realizados en esta etapa
- ✅ 18 relaciones @ManyToOne
- ✅ 5 relaciones @OneToMany
- ✅ 4 campos @Enumerated
- ✅ 6 callbacks @PrePersist
- ✅ 1 callback @PreUpdate

### **Archivos Modificados**
1. Role.java
2. OrderStatus.java
3. PaymentMethod.java
4. PaymentStatus.java
5. Category.java
6. Product.java
7. User.java
8. Address.java
9. UserSession.java
10. Cart.java
11. CartItem.java
12. Order.java
13. OrderItem.java
14. Payment.java
15. OrderMapper.java (ajustes)
16. OrderService.java (ajustes)

### **Compilación**
- ✅ SUCCESS - Proyecto compila sin errores

---

## 🔄 Cambios Importantes en Order Entity

Order.java tuvo cambios significativos:

**Antes (Etapa 06)**:
```java
private Long userId;
private Long orderStatusId;
private Long shippingAddressId;
private Long billingAddressId;
```

**Después (Etapa 07)**:
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
private User user;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "order_status_id", nullable = false)
private OrderStatus status;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "shipping_address_id", nullable = false)
private Address shippingAddress;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "billing_address_id", nullable = false)
private Address billingAddress;
```

**Impacto**:
- ✅ Mayor integridad referencial
- ✅ Navegación directa entre entidades
- ✅ Soporte para Lazy Loading
- ⚠️ Mapper y Service requieren ajustes (completados)

---

## 📝 Notas Importantes

### **Fetch Type: LAZY**
Todas las relaciones @ManyToOne usan `FetchType.LAZY`:
- ✅ **Ventaja**: Solo carga la entidad relacionada cuando se accede explícitamente
- ✅ **Ventaja**: Reduce consultas innecesarias a la base de datos
- ⚠️ **Cuidado**: Requiere sesión JPA abierta al acceder

### **Cascade y OrphanRemoval**
Las relaciones @OneToMany usan:
```java
cascade = CascadeType.ALL, orphanRemoval = true
```
- ✅ **CascadeType.ALL**: Propaga persist, merge, remove, refresh, detach
- ✅ **orphanRemoval = true**: Elimina hijos huérfanos automáticamente

### **@Enumerated(EnumType.STRING)**
Se usa STRING en lugar de ORDINAL:
- ✅ **Ventaja**: Legible en base de datos
- ✅ **Ventaja**: No se rompe si se reordena el enum
- ⚠️ **Cuidado**: Ocupa más espacio

---

## ✅ Próximos Pasos: Etapa 08

**Crear las 14 interfaces Repository extendiendo JpaRepository**:
1. RoleRepository
2. UserRepository
3. AddressRepository
4. CategoryRepository
5. ProductRepository
6. UserSessionRepository
7. CartRepository
8. CartItemRepository
9. OrderStatusRepository
10. OrderRepository
11. OrderItemRepository
12. PaymentMethodRepository
13. PaymentStatusRepository
14. PaymentRepository

Cada repository tendrá métodos de búsqueda personalizados como:
- `findByEmail(String email)`
- `findByUserId(Long userId)`
- `findByStatus(CartStatus status)`
- etc.

---

## 🎉 Conclusión

**Estado: ETAPA 07 - 100% COMPLETADA**

- ✅ Todas las entidades tienen anotaciones JPA completas
- ✅ Relaciones bidireccionales implementadas correctamente
- ✅ Callbacks de ciclo de vida configurados
- ✅ Proyecto compila sin errores
- ✅ Ajustes de compatibilidad completados
- ✅ Listo para implementar Repositories en Etapa 08

---

**Autor:** Luis Goenaga  
**Proyecto:** Product Purchasing System - Backend II  
**Institución:** CESDE  
**Año:** 2026
