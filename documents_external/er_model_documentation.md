
# Documentación del Modelo E-R – Tienda online de productos informáticos

## 1. Objetivo y alcance
Este documento describe el modelo entidad–relación propuesto para una tienda online de productos informáticos. El modelo está pensado para:

- Gestionar **usuarios registrados** y sus **roles**
- Permitir **carrito de compras como invitado (guest)**
- Exigir **registro/inicio de sesión para finalizar compra (checkout)**
- Administrar **catálogo de productos** y **categorías**
- Registrar **órdenes** (pedidos), **ítems** de la orden y **pagos**
- Manejar **estados** (catálogos) para órdenes y pagos
- Implementar **merge de carrito** cuando un invitado se registra y ya existe un carrito abierto del usuario

> Nota: La documentación está en español, pero los **nombres de tablas y campos** se mantienen en **inglés**.

---

## 2. Tablas (entidades) y su propósito
A continuación se explican todas las tablas del modelo, qué representa cada una, sus campos principales y consideraciones de diseño.

---

### 2.1 `ROLE`
**Propósito:** Define tipos de usuario o niveles de acceso (por ejemplo: admin, customer).

**Campos (principales):**
- `role_id` (PK)
- `name` (UNIQUE)
- `description`

**Consideraciones:**
- Mantener el catálogo de roles separado facilita administrar permisos sin cambiar la estructura de `USER`.

---

### 2.2 `USER`
**Propósito:** Almacena la información del usuario **registrado** que podrá realizar compras.

**Campos (principales):**
- `user_id` (PK)
- `role_id` (FK → `ROLE.role_id`)
- `email` (UNIQUE)
- `password_hash`
- `first_name`
- `last_name`
- `phone`
- `status`
- `created_at`

**Consideraciones:**
- `email` único evita cuentas duplicadas.
- `password_hash` almacena el hash de la contraseña (nunca la contraseña en texto plano).
- `status` permite bloquear/desactivar usuarios.

---

### 2.3 `ADDRESS`
**Propósito:** Guarda direcciones de envío y/o facturación asociadas a un usuario.

**Campos (principales):**
- `address_id` (PK)
- `user_id` (FK → `USER.user_id`)
- `type` (shipping/billing)
- `line1`, `line2`
- `city`, `state`, `country`, `postal_code`
- `is_default`

**Consideraciones:**
- Relación 1:N: un usuario puede tener múltiples direcciones.
- `type` diferencia entre dirección de envío y de facturación.

---

### 2.4 `USER_SESSION`
**Propósito:** Representa sesiones activas para identificar navegación y **carritos de invitado**.

**Campos (principales):**
- `session_id` (PK)
- `user_id` (FK → `USER.user_id`, NULLABLE)
- `session_token` (UNIQUE)
- `created_at`
- `expires_at`

**Consideraciones:**
- Si `user_id` es NULL, la sesión corresponde a un **invitado**.
- `session_token` permite mapear sesiones desde cookies/JWT/headers.

---

### 2.5 `CATEGORY`
**Propósito:** Organiza el catálogo en categorías, permitiendo jerarquía (categorías anidadas).

**Campos (principales):**
- `category_id` (PK)
- `parent_id` (FK → `CATEGORY.category_id`, NULLABLE)
- `name`
- `slug` (UNIQUE)

**Consideraciones:**
- `parent_id` habilita estructura tipo árbol (por ejemplo: “Computadores” → “Portátiles”).
- `slug` único facilita URLs amigables.

---

### 2.6 `PRODUCT`
**Propósito:** Representa los productos vendibles.

**Campos (principales):**
- `product_id` (PK)
- `category_id` (FK → `CATEGORY.category_id`)
- `sku` (UNIQUE)
- `name`
- `description`
- `price`
- `stock_qty`
- `is_active`
- `created_at`

**Consideraciones:**
- `sku` único soporta integración con inventario.
- `is_active` permite ocultar productos sin borrarlos.

---

### 2.7 `CART`
**Propósito:** Contenedor del carrito de compras (invitado o usuario registrado).

**Campos (principales):**
- `cart_id` (PK)
- `user_id` (FK → `USER.user_id`, NULLABLE)
- `session_id` (FK → `USER_SESSION.session_id`)
- `status` (open/converted/abandoned)
- `created_at`
- `updated_at`

**Consideraciones clave:**
- El carrito **siempre** está asociado a una sesión mediante `session_id`.
- Para invitado: `user_id = NULL`.
- Para usuario registrado: `user_id` se asigna cuando el usuario inicia sesión o se registra.
- `status = converted` cuando el carrito se transforma en una orden (`ORDER`).

---

### 2.8 `CART_ITEM`
**Propósito:** Detalle del carrito (productos y cantidades).

**Campos (principales):**
- `cart_item_id` (PK)
- `cart_id` (FK → `CART.cart_id`)
- `product_id` (FK → `PRODUCT.product_id`)
- `quantity`
- `unit_price`
- `added_at`

**Restricciones recomendadas:**
- UNIQUE (`cart_id`, `product_id`) para evitar el mismo producto duplicado en el mismo carrito.

**Consideraciones:**
- `unit_price` guarda el precio en el momento de agregar al carrito (congela precio para consistencia).

---

### 2.9 `ORDER_STATUS`
**Propósito:** Catálogo de estados posibles de una orden.

**Campos (principales):**
- `order_status_id` (PK)
- `name` (UNIQUE)

**Ejemplos:** pending, paid, shipped, cancelled.

---

### 2.10 `ORDER`
**Propósito:** Representa una compra finalizada (pedido).

**Campos (principales):**
- `order_id` (PK)
- `order_number` (UNIQUE)
- `user_id` (FK → `USER.user_id`, NOT NULL)
- `order_status_id` (FK → `ORDER_STATUS.order_status_id`)
- `shipping_address_id` (FK → `ADDRESS.address_id`)
- `billing_address_id` (FK → `ADDRESS.address_id`)
- `subtotal`
- `tax`
- `shipping_cost`
- `total`
- `created_at`

**Consideraciones clave:**
- El checkout requiere usuario registrado → `user_id` **no puede ser NULL**.
- Se recomienda guardar totales en la orden para auditoría y reportes.

---

### 2.11 `ORDER_ITEM`
**Propósito:** Detalle de productos comprados en una orden.

**Campos (principales):**
- `order_item_id` (PK)
- `order_id` (FK → `ORDER.order_id`)
- `product_id` (FK → `PRODUCT.product_id`)
- `quantity`
- `unit_price`
- `line_total`

**Restricciones recomendadas:**
- UNIQUE (`order_id`, `product_id`).

**Consideraciones:**
- `unit_price` queda histórico (precio al momento de comprar).

---

### 2.12 `PAYMENT_STATUS`
**Propósito:** Catálogo de estados del pago.

**Campos (principales):**
- `payment_status_id` (PK)
- `name` (UNIQUE)

**Ejemplos:** pending, approved, rejected.

---

### 2.13 `PAYMENT_METHOD`
**Propósito:** Catálogo de métodos de pago.

**Campos (principales):**
- `payment_method_id` (PK)
- `name` (UNIQUE)

**Ejemplos:** credit_card, bank_transfer, cash_on_delivery.

---

### 2.14 `PAYMENT`
**Propósito:** Registra las transacciones de pago asociadas a una orden.

**Campos (principales):**
- `payment_id` (PK)
- `order_id` (FK → `ORDER.order_id`)
- `payment_method_id` (FK → `PAYMENT_METHOD.payment_method_id`)
- `payment_status_id` (FK → `PAYMENT_STATUS.payment_status_id`)
- `amount`
- `currency`
- `provider_reference`
- `paid_at`

**Consideraciones:**
- Permite múltiples pagos por orden (reintentos, pagos parciales, etc.).

---

## 3. Relaciones (resumen)
Relaciones principales del modelo:

- `ROLE` 1 — N `USER`
- `USER` 1 — N `ADDRESS`
- `USER` 1 — N `USER_SESSION`
- `CATEGORY` 1 — N `PRODUCT`
- `USER_SESSION` 1 — N `CART`
- `USER` 0..1 — N `CART` (un carrito puede estar sin usuario si es invitado)
- `CART` 1 — N `CART_ITEM`
- `PRODUCT` 1 — N `CART_ITEM`
- `USER` 1 — N `ORDER`
- `ORDER_STATUS` 1 — N `ORDER`
- `ORDER` 1 — N `ORDER_ITEM`
- `PRODUCT` 1 — N `ORDER_ITEM`
- `ORDER` 1 — N `PAYMENT`
- `PAYMENT_METHOD` 1 — N `PAYMENT`
- `PAYMENT_STATUS` 1 — N `PAYMENT`

---

## 4. Consideraciones de diseño

### 4.1 Carrito de invitado y checkout con registro
- El carrito del invitado se identifica por `session_id`.
- Para completar el checkout y crear una `ORDER`, el usuario debe estar registrado → `ORDER.user_id` es obligatorio.

### 4.2 Congelación de precios
- Se almacena `unit_price` tanto en `CART_ITEM` como en `ORDER_ITEM` para mantener consistencia del precio histórico.

### 4.3 Catálogos de estados
- `ORDER_STATUS` y `PAYMENT_STATUS` permiten evolucionar el flujo sin cambiar el esquema (solo insertando nuevos registros).

### 4.4 Integridad y unicidad
- `USER.email`, `PRODUCT.sku`, `USER_SESSION.session_token`, `CATEGORY.slug` deben ser únicos.
- Restricciones UNIQUE en (`cart_id`, `product_id`) y (`order_id`, `product_id`) simplifican el manejo de cantidades.

---

## 5. Política de *Cart Merge* (obligatoria)

### Caso
Si un usuario agrega productos como **invitado** (tiene un `CART` con `user_id = NULL`) y luego se **registra/inicia sesión**, puede ocurrir que:
- ya exista un carrito abierto del usuario (`CART` con `user_id = :user_id` y `status = 'open'`)

### Regla
En ese escenario, el sistema debe hacer **merge** (fusión) de carritos, quedando **un solo carrito abierto**.

### Pasos recomendados del merge
1. Encontrar el carrito de invitado por `session_id` y `status = 'open'`.
2. Encontrar el carrito abierto del usuario por `user_id` y `status = 'open'`.
3. Si ambos existen:
   - Para cada registro en `CART_ITEM` del carrito invitado:
     - Si el mismo `product_id` existe en el carrito del usuario → sumar `quantity`.
     - Si no existe → mover/insertar el ítem al carrito del usuario.
   - Definir una regla para `unit_price` (recomendación: conservar el más reciente o el del carrito del usuario, según política de negocio).
4. Marcar el carrito invitado como `status = 'abandoned'` o eliminarlo (según necesidad de auditoría).
5. Asegurar que el usuario continúe con el carrito resultante (el del usuario).

### Resultado
El usuario continúa su compra sin perder productos y sin duplicaciones, manteniendo **un único carrito activo**.

---

## 6. Fin del documento
