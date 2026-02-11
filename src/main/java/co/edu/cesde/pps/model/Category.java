package co.edu.cesde.pps.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Category - Organiza el catálogo en categorías jerárquicas.
 *
 * Soporta estructura tipo árbol mediante auto-referencia (parent).
 * Ejemplo: "Computadores" → "Portátiles" → "Gaming"
 *
 * Campos:
 * - categoryId: Identificador único de la categoría (PK)
 * - parent: Categoría padre (N:1 con Category) - NULLABLE para categorías raíz
 * - name: Nombre de la categoría
 * - slug: URL-friendly identifier (UNIQUE) - para URLs amigables
 * - subcategories: Lista de subcategorías (1:N con Category)
 * - products: Lista de productos de esta categoría (1:N con Product)
 *
 * Relaciones:
 * - N:1 con Category (auto-referencia para jerarquía - muchas categorías tienen un padre)
 * - 1:N con Category (una categoría tiene muchas subcategorías)
 * - 1:N con Product (una categoría tiene muchos productos)
 *
 * NOTA: Los métodos de gestión bidireccional (addSubcategory, removeSubcategory) fueron movidos
 * a la capa de servicio (CategoryService) en etapa 05 para mantener el modelo limpio.
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long categoryId;

    // Auto-referencia: Relación padre-hijo (jerárquica)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent; // Nullable - NULL para categorías raíz

    @Column(name = "name", nullable = false, length = 100)
    @ToString.Include
    private String name;

    @Column(name = "slug", nullable = false, unique = true, length = 100)
    @ToString.Include
    private String slug;

    // Colecciones para relaciones 1:N
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> subcategories = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    /**
     * Verifica si es categoría raíz
     */
    public boolean isRootCategory() {
        return parent == null;
    }
}
