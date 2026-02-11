# Guía de Lombok (Opción B) — anotaciones usadas en ETAPA07

**Fecha de referencia:** 11 de febrero de 2026

Este documento describe las anotaciones de Lombok usadas en el paquete `co.edu.cesde.pps.model` durante la ETAPA07, con su definición y cómo se aplican en este proyecto.

> En este repositorio se usa **Lombok Opción B**: anotaciones explícitas, evitando `@Data`, para mantener control en entidades JPA.

---

## 1) ¿Qué es Lombok?

**Project Lombok** genera código (getters, setters, constructores, builder, etc.) durante compilación mediante *annotation processing*. Reduce boilerplate y mejora la legibilidad.

### Requisito importante

Para que funcione correctamente en el IDE:
- Debe estar habilitado **Annotation Processing**.

---

## 2) ¿Por qué NO usar `@Data` en entidades JPA?

`@Data` combina:
- `@Getter` + `@Setter`
- `@ToString`
- `@EqualsAndHashCode`
- `@RequiredArgsConstructor`

Esto puede ser riesgoso en entidades JPA por:
- `toString()` recursivo/carga LAZY accidental.
- `equals/hashCode` incluyendo relaciones/colecciones (problemas de performance y memoria).

Por eso se usa Opción B: control fino y seguro.

---

## 3) Anotaciones usadas (definición + uso)

### 3.1. `@Getter`

**Qué hace:**
- Genera getters para los campos.

**Uso en el proyecto:**
- Se aplica a nivel de clase en entidades (`model/*`).

**Ejemplo de intención:**
- Eliminar métodos `getX()` repetitivos.

---

### 3.2. `@Setter`

**Qué hace:**
- Genera setters para los campos.

**Uso en el proyecto:**
- Se aplica a nivel de clase.

**Nota importante (validaciones):**
- Cuando un setter requiere validación (por ejemplo `Product.setPrice`, `CartItem.setQuantity`), se implementa manualmente.
- El método manual tiene prioridad sobre el generado.

---

### 3.3. `@NoArgsConstructor`

**Qué hace:**
- Genera un constructor sin argumentos.

**Por qué es clave en JPA:**
- Hibernate/JPA requieren constructor vacío para instanciar entidades por reflexión.

---

### 3.4. `@AllArgsConstructor`

**Qué hace:**
- Genera un constructor con todos los campos.

**Uso en el proyecto:**
- Se usa en entidades simples (catálogos) o donde aporte valor.

**Advertencia práctica:**
- Muchas veces la capa de servicio usa constructores específicos de negocio.
- Si esos constructores deben mantenerse, se dejan/crean manualmente para compatibilidad.

---

### 3.5. `@Builder`

**Qué hace:**
- Genera el patrón Builder.

**Uso recomendado:**
- Construcción clara en pruebas o cuando hay muchos campos opcionales.

**Nota con JPA:**
- No reemplaza el `@NoArgsConstructor`; se usa como complemento.

---

### 3.6. `@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)`

**Qué hace:**
- Genera `equals()` y `hashCode()`.

**Parámetros usados:**
- `onlyExplicitlyIncluded = true`: SOLO incluye campos marcados con `@EqualsAndHashCode.Include`.
- `callSuper = false`: no mezcla lógica de clases padre.

**Uso en el proyecto:**
- Se incluye únicamente el ID (`@Id`) para evitar problemas con colecciones/relaciones.

---

### 3.7. `@EqualsAndHashCode.Include`

**Qué hace:**
- Marca un campo para incluirlo en `equals/hashCode` cuando `onlyExplicitlyIncluded=true`.

**Uso típico:**
- Campo PK (`userId`, `productId`, etc.).

---

### 3.8. `@ToString(onlyExplicitlyIncluded = true)`

**Qué hace:**
- Genera `toString()`.

**Parámetro usado:**
- `onlyExplicitlyIncluded = true`: imprime solo campos marcados con `@ToString.Include`.

**Por qué es importante en JPA:**
- Evita recursión por relaciones bidireccionales.
- Evita inicialización accidental de relaciones LAZY.

---

### 3.9. `@ToString.Include`

**Qué hace:**
- Marca campos que deben aparecer en el `toString()`.

**Uso en el proyecto:**
- IDs y campos simples (ej: `email`, `name`, `status`).

---

## 4) Buenas prácticas aplicadas en el modelo

1. Evitar `@Data` en entidades.
2. `equals/hashCode` basado en ID únicamente.
3. `toString` sin relaciones y sin colecciones.
4. Mantener explícitos los métodos de negocio (no reemplazarlos por Lombok).
5. Mantener callbacks JPA como:
   - `@PrePersist`
   - `@PreUpdate`

---

## 5) Anotaciones Lombok NO usadas (a propósito)

- `@Data`: evitada por riesgos en JPA.
- `@Value`: porque las entidades no son inmutables.
- `@RequiredArgsConstructor`: no es la estrategia principal aquí.

---

## 6) Relación con la ETAPA08

En ETAPA08 se espera:
- Migrar repositorios manuales a Spring Data JPA.
- Reemplazar transacciones manuales por `@Transactional`.

Las entidades ya están preparadas (boilerplate reducido y estructura más limpia).

