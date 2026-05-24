package com.arquisoft.fichas.infrastructure.fichaperfil.query.adapter.out.persistence;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilJpaEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

class FichaPerfilJpaSpecification {

    private FichaPerfilJpaSpecification() {}

    static Specification<FichaPerfilJpaEntity> desdeCriteria(FichaPerfilCriteria criteria) {
        return (root, query, builder) -> {
            List<Predicate> predicados = new ArrayList<>();

            if (criteria.getTituloProyecto() != null && !criteria.getTituloProyecto().isBlank()) {
                predicados.add(builder.like(
                        builder.lower(root.get("tituloProyecto")),
                        "%" + criteria.getTituloProyecto().toLowerCase() + "%"
                ));
            }

            if (criteria.getAsesorId() != null) {
                predicados.add(builder.equal(
                        root.get("asesorFicha").get("id"),
                        criteria.getAsesorId()
                ));
            }

            return builder.and(predicados.toArray(new Predicate[0]));
        };
    }
}
