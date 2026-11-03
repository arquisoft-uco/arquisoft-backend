package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionDelJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.application.evaluacion.query.secondaryport.EvaluacionDelJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository.mapper.EvaluacionDelJuradoQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EvaluacionDelJuradoQueryOutputAdapter implements EvaluacionDelJuradoQueryOutputPort {

    private final EvaluacionDelJuradoQueryRepository evaluacionDelJuradoQueryRepository;
    private final EvaluacionDelJuradoJpaSpecification specification;

    @Override
    public PaginatedResult<EvaluacionReadModel> consultarPorJurado(EvaluacionDelJuradoCriteria criteria) {
        var pageable = PageableMapper.toPageable(
                criteria, EvaluacionDelJuradoSortMapper::traducir, EvaluacionDelJuradoSortMapper.RUTA_DESEMPATE);

        return PaginationMapper.toResult(
                evaluacionDelJuradoQueryRepository.findAll(specification.desdeCriteria(criteria), pageable)
                        .map(EvaluacionDelJuradoQueryMapper::toReadModel));
    }
}
