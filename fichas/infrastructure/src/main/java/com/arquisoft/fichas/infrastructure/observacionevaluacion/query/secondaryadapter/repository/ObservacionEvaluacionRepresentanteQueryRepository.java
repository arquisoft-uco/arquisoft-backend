package com.arquisoft.fichas.infrastructure.observacionevaluacion.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface ObservacionEvaluacionRepresentanteQueryRepository
        extends QueryRepository<ObservacionEvaluacionRepresentanteJpaQueryEntity, UUID> {

    List<ObservacionEvaluacionRepresentanteJpaQueryEntity> findByEvaluacionFichaPerfilIdAndRepresentanteComiteIdOrderByObservacionAscIdAsc(
            UUID evaluacionFichaPerfilId, UUID representanteComiteId);
}
