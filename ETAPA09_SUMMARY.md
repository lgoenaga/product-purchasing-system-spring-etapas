# Resumen Etapa 09 - EntityManagerFactory + TransactionManager (JPA Manual)

## ✅ Completado - Fecha: 4 de febrero de 2026

### 📦 Objetivo de la Etapa

Implementar la base de infraestructura para persistencia **sin Spring**, centralizando:

- Creación y ciclo de vida de `EntityManagerFactory`
- Creación de `EntityManager` por operación
- Manejo transaccional manual (commit/rollback)

Esto habilita las próximas etapas:
- **ETAPA 10**: Implementación de Repositories (EntityManager + JPQL)
- **ETAPA 11**: Refactor de Services para usar repositories reales

---

## 🎯 ¿Qué se implementó?

### 1️⃣ `JpaConfig` (configuración central JPA)

**Archivo:** `src/main/java/co/edu/cesde/pps/config/JpaConfig.java`

Responsabilidades:
- Mantener un **singleton thread-safe** de `EntityManagerFactory`
- Proveer `EntityManager` nuevo por operación
- Permitir cierre explícito con `close()`

Características:
- Usa el `persistence-unit` configurado en `META-INF/persistence.xml`
- Sobrescribe propiedades JDBC con variables de entorno a través de `DatabaseConfig`:
  - `jakarta.persistence.jdbc.url`
  - `jakarta.persistence.jdbc.user`
  - `jakarta.persistence.jdbc.password`
  - `jakarta.persistence.jdbc.driver`

Esto asegura consistencia con el enfoque del proyecto:
- **Nada hardcodeado**
- Configuración por **variables de entorno**

---

### 2️⃣ `TransactionManager` (manejo transaccional manual)

**Archivo:** `src/main/java/co/edu/cesde/pps/util/TransactionManager.java`

Provee wrappers para ejecutar operaciones con EntityManager:

#### ✅ Operación con transacción (con retorno)
```java
TransactionManager.executeInTransaction(em -> {
    // usar repos/JPQL con em
    return result;
});
```

#### ✅ Operación con transacción (void)
```java
TransactionManager.executeInTransaction(em -> {
    // acciones persistentes
});
```

#### ✅ Operación de solo lectura
```java
TransactionManager.executeReadOnly(em -> {
    // queries sin tx
    return data;
});
```

Manejo de errores:
- `commit()` si no hay errores
- `rollback()` automático si ocurre excepción
- No se oculta la excepción original si rollback falla
- Cierre de `EntityManager` garantizado en `finally`

---

## ✅ Validación

Compilación:
```bash
mvn clean compile
```

**Resultado esperado:** ✅ BUILD SUCCESS

---

## 🔄 Próxima Etapa: ETAPA 10

### Implementación de Repositories (Impl)

Se crearán las implementaciones en:
- `co.edu.cesde.pps.repository.impl.*`

Patrón:
- Cada `RepositoryImpl` recibe un `EntityManager` por constructor
- Se usan métodos JPA estándar (`persist`, `merge`, `find`, `createQuery`)
- Query methods implementados con JPQL

Ejemplo de uso (a implementar en ETAPA 10):
```java
TransactionManager.executeInTransaction(em -> {
    UserRepository userRepo = new UserRepositoryImpl(em);
    // ...
    return userRepo.save(user);
});
```

---

## 🛠️ Comandos Git (trazabilidad)

```bash
# Desde etapa08
git checkout etapa08
git pull origin etapa08

# Crear rama
git checkout -b etapa09
git push -u origin etapa09

# Commits
git add src/main/java/co/edu/cesde/pps/config/JpaConfig.java
git commit -m "feat: add JpaConfig with EntityManagerFactory"

git add src/main/java/co/edu/cesde/pps/util/TransactionManager.java
git commit -m "feat: add TransactionManager for manual transactions"

git add ETAPA09_SUMMARY.md
git commit -m "docs: add ETAPA09_SUMMARY"

# Build gate
mvn clean compile

# Push
git push origin etapa09
```

---

**Rama**: `etapa09`  
**Estado**: ✅ Completada  
**Autor**: Luis Goenaga  
**Proyecto**: Product Purchasing System - Backend II  
**Institución**: CESDE
