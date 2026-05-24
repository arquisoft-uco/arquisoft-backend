package com.arquisoft.shared.postgres.query;

import com.arquisoft.shared.postgres.exception.FiltroInvalidoException;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.QueryCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;

/**
 * Clase abstracta que traduce un árbol de {@link NodoFiltro} a una {@link Specification} JPA.
 *
 * Principios aplicados:
 *
 *   SRP — esta clase solo recorre el árbol y combina predicados; no sabe nada
 *          sobre campos concretos ni tipos de dato (eso es responsabilidad de CampoSpec).
 *   OCP — agregar soporte a un nuevo contexto requiere crear una subclase que declare
 *          su mapa de campos; esta clase no se modifica.
 *   LSP — cualquier subclase puede sustituir a QueryJpaSpecification sin cambiar el
 *          comportamiento desde el punto de vista del OutputAdapter.
 *   DIP — depende de la abstracción CampoSpec<E>, no de implementaciones concretas.
 *
 * Uso:
 * <pre>
 *   class ProyectoJpaSpecification extends QueryJpaSpecification<ProyectoJpaEntity> {
 *       private static final Map<String, CampoSpec<ProyectoJpaEntity>> CAMPOS = Map.of(
 *           "titulo", CampoSpec.texto(root -> root.get("titulo")),
 *           "estado", CampoSpec.texto(root -> root.get("estado"))
 *       );
 *       {@literal @}Override
 *       protected Map<String, CampoSpec<ProyectoJpaEntity>> camposPermitidos() { return CAMPOS; }
 *   }
 * </pre>
 */
public abstract class QueryJpaSpecification<E> {

    protected abstract Map<String, CampoSpec<E>> camposPermitidos();

    public final Specification<E> desdeCriteria(QueryCriteria criteria) {
        if (!criteria.tieneFiltros()) {
            return (root, query, cb) -> null;
        }
        return especDesdeNodo(criteria.getRaiz());
    }

    private Specification<E> especDesdeNodo(NodoFiltro nodo) {
        return switch (nodo) {
            case NodoFiltro.Predicado p -> especDesdePredicado(p);
            case NodoFiltro.Grupo g     -> especDesdeGrupo(g);
        };
    }

    private Specification<E> especDesdeGrupo(NodoFiltro.Grupo grupo) {
        List<NodoFiltro> nodos = grupo.nodos();
        if (nodos.isEmpty()) {
            return (root, query, cb) -> null;
        }
        Specification<E> acc = especDesdeNodo(nodos.get(0));
        for (int i = 1; i < nodos.size(); i++) {
            Specification<E> sig = especDesdeNodo(nodos.get(i));
            acc = grupo.conector() == FiltroConector.OR
                    ? acc.or(sig)
                    : acc.and(sig);
        }
        return acc;
    }

    private Specification<E> especDesdePredicado(NodoFiltro.Predicado predicado) {
        CampoSpec<E> spec = camposPermitidos().get(predicado.campo());
        if (spec == null) {
            throw new FiltroInvalidoException(
                    "Campo de filtro desconocido: '" + predicado.campo() +
                    "'. Campos disponibles: " + camposPermitidos().keySet()
            );
        }
        return spec.construirSpec(predicado.operador(), predicado.valor());
    }
}
