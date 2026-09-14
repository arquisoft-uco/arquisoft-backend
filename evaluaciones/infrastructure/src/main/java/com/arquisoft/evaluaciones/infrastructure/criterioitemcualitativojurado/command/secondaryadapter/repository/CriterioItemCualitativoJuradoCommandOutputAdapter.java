package com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativojurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.command.secondaryport.CriterioItemCualitativoJuradoOutputPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

// SQL nativo escalar sobre la tabla existente criterio_item_cualitativo_jurado: el lado
// comando no tiene (ni debe tener) un JpaEntity propio de esta tabla de solo lectura, y no
// puede importar el JpaQueryEntity del lado query (aislamiento CQRS).
@Component
public class CriterioItemCualitativoJuradoCommandOutputAdapter implements CriterioItemCualitativoJuradoOutputPort {

    private static final String SQL_IDS_EXISTENTES =
            "SELECT id FROM criterio_item_cualitativo_jurado WHERE id IN (:ids)";

    @PersistenceContext(unitName = "evaluaciones")
    private EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public Set<UUID> consultarIdsExistentes(Set<UUID> ids) {
        if (ids.isEmpty()) {
            return Set.of();
        }
        List<UUID> filas = entityManager.createNativeQuery(SQL_IDS_EXISTENTES)
                .setParameter("ids", ids)
                .getResultList();
        return new HashSet<>(filas);
    }
}
