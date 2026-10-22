package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionEstudianteCriteria;
import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.application.evaluacion.query.secondaryport.EvaluacionEstudianteQueryOutputPort;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository.mapper.EvaluacionEstudianteQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EvaluacionEstudianteQueryOutputAdapter implements EvaluacionEstudianteQueryOutputPort {

    private final EvaluacionEstudianteQueryRepository evaluacionEstudianteQueryRepository;
    private final EvaluacionEstudianteJpaSpecification specification;

    @Override
    public PaginatedResult<EvaluacionReadModel> consultarPorEstudiante(EvaluacionEstudianteCriteria criteria) {
        var pageable = PageableMapper.toPageable(
                criteria, EvaluacionEstudianteSortMapper::traducir, EvaluacionEstudianteSortMapper.RUTA_DESEMPATE);

        return PaginationMapper.toResult(
                evaluacionEstudianteQueryRepository.findAll(specification.desdeCriteria(criteria), pageable)
                        .map(EvaluacionEstudianteQueryMapper::toReadModel));
    }
}
