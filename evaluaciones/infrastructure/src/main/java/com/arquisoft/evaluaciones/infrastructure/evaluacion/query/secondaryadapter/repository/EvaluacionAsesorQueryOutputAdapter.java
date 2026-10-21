package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.application.evaluacion.query.secondaryport.EvaluacionAsesorQueryOutputPort;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository.mapper.EvaluacionAsesorQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EvaluacionAsesorQueryOutputAdapter implements EvaluacionAsesorQueryOutputPort {

    private final EvaluacionAsesorQueryRepository evaluacionAsesorQueryRepository;
    private final EvaluacionAsesorJpaSpecification specification;

    @Override
    public PaginatedResult<EvaluacionReadModel> consultarPorAsesor(EvaluacionAsesorCriteria criteria) {
        var pageable = PageableMapper.toPageable(
                criteria, EvaluacionAsesorSortMapper::traducir, EvaluacionAsesorSortMapper.RUTA_DESEMPATE);

        return PaginationMapper.toResult(
                evaluacionAsesorQueryRepository.findAll(specification.desdeCriteria(criteria), pageable)
                        .map(EvaluacionAsesorQueryMapper::toReadModel));
    }
}
