package com.arquisoft.fichas.infrastructure.fichaperfil.query.adapter.out.persistence;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilJpaEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

/**
 * Fábrica de Specification componibles para FichaPerfilJpaEntity.
 *
 * Cada método devuelve un predicado independiente.
 * Combínalos con .and() / .or() / .not() a voluntad.
 *
 * AND implícito:  spec1.and(spec2)
 * OR implícito:   spec1.or(spec2)
 * Negación:       Specification.not(spec1)
 * Punto neutro:   Specification.where(null)  → sin filtro = todos los registros
 */
class FichaPerfilJpaSpecification {

    private FichaPerfilJpaSpecification() {}

    // -------------------------------------------------------------------------
    // Predicados individuales — cada uno filtra un solo campo
    // -------------------------------------------------------------------------

    static Specification<FichaPerfilJpaEntity> tituloContiene(String valor) {
        return (root, query, builder) -> builder.like(
                builder.lower(root.get("tituloProyecto")),
                "%" + valor.toLowerCase() + "%"
        );
    }

    static Specification<FichaPerfilJpaEntity> asesorIdEs(UUID id) {
        return (root, query, builder) -> builder.equal(
                root.get("asesorFicha").get("id"), id
        );
    }

    static Specification<FichaPerfilJpaEntity> asesorNombreContiene(String valor) {
        return (root, query, builder) -> builder.like(
                builder.lower(root.get("asesorFicha").get("nombre")),
                "%" + valor.toLowerCase() + "%"
        );
    }

    static Specification<FichaPerfilJpaEntity> asesorEmailContiene(String valor) {
        return (root, query, builder) -> builder.like(
                builder.lower(root.get("asesorFicha").get("email")),
                "%" + valor.toLowerCase() + "%"
        );
    }

    // -------------------------------------------------------------------------
    // Predicados compuestos — combinan varios campos con OR internamente
    // -------------------------------------------------------------------------

    /**
     * Busca el término en título O nombre del asesor O email del asesor.
     * Útil para un campo de búsqueda global tipo "buscador".
     *
     * SQL: WHERE LOWER(titulo) LIKE '%t%'
     *         OR LOWER(asesor.nombre) LIKE '%t%'
     *         OR LOWER(asesor.email) LIKE '%t%'
     */
    static Specification<FichaPerfilJpaEntity> terminoBusqueda(String termino) {
        return tituloContiene(termino)
                .or(asesorNombreContiene(termino))
                .or(asesorEmailContiene(termino));
    }

    // -------------------------------------------------------------------------
    // Composición desde criteria — AND entre todos los filtros activos
    // -------------------------------------------------------------------------

    /**
     * Construye la Specification final ANDeando cada filtro presente en criteria.
     *
     * Cada parámetro nulo/vacío se ignora (no agrega condición).
     * Los filtros activos se encadenan con AND entre sí.
     *
     * Ejemplo de SQL resultante con titulo + asesorId + termino activos:
     *   WHERE LOWER(titulo) LIKE '%web%'          -- tituloProyecto
     *     AND asesor.id = UUID                    -- asesorId
     *     AND (   LOWER(titulo) LIKE '%juan%'     -- termino (OR interno)
     *          OR LOWER(asesor.nombre) LIKE '%juan%'
     *          OR LOWER(asesor.email) LIKE '%juan%')
     */
    static Specification<FichaPerfilJpaEntity> desdeCriteria(FichaPerfilCriteria criteria) {
        // conjunction() = predicado neutro (1=1): no filtra nada y es seguro como punto de partida
        Specification<FichaPerfilJpaEntity> spec = (root, query, builder) -> builder.conjunction();

        if (tiene(criteria.getTituloProyecto())) {
            spec = spec.and(tituloContiene(criteria.getTituloProyecto()));
        }
        if (criteria.getAsesorId() != null) {
            spec = spec.and(asesorIdEs(criteria.getAsesorId()));
        }
        if (tiene(criteria.getAsesorNombre())) {
            spec = spec.and(asesorNombreContiene(criteria.getAsesorNombre()));
        }
        if (tiene(criteria.getAsesorEmail())) {
            spec = spec.and(asesorEmailContiene(criteria.getAsesorEmail()));
        }
        if (tiene(criteria.getTermino())) {
            spec = spec.and(terminoBusqueda(criteria.getTermino()));
        }

        return spec;
    }

    private static boolean tiene(String valor) {
        return valor != null && !valor.isBlank();
    }
}
