package com.arquisoft.usuarios.infrastructure.estudiante.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.estudiante.query.criteria.EstudianteCriteria;
import com.arquisoft.usuarios.application.estudiante.query.criteria.EstudianteVigenteCriteria;
import com.arquisoft.usuarios.application.estudiante.query.readmodel.EstudianteReadModel;
import com.arquisoft.usuarios.application.estudiante.query.readmodel.EstudianteVigenteReadModel;
import com.arquisoft.usuarios.application.estudiante.query.secondaryport.EstudianteQueryOutputPort;
import com.arquisoft.usuarios.infrastructure.estudiante.query.secondaryadapter.repository.mapper.EstudianteQueryMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstudianteQueryOutputAdapter implements EstudianteQueryOutputPort {

    private final EstudianteQueryRepository estudianteQueryRepository;
    private final EstudianteVigenteQueryRepository estudianteVigenteQueryRepository;
    private final EstudianteJpaSpecification estudianteSpecification;
    private final EstudianteVigenteJpaSpecification estudianteVigenteSpecification;

    @Override
    public PaginatedResult<EstudianteReadModel> consultarTodos(EstudianteCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, EstudianteSortMapper::traducir);
        var spec = estudianteSpecification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                estudianteQueryRepository.findAll(spec, pageable)
                        .map(EstudianteQueryMapper::toReadModel));
    }

    @Override
    public PaginatedResult<EstudianteVigenteReadModel> consultarVigentes(EstudianteVigenteCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, EstudianteVigenteSortMapper::traducir);
        var spec = estudianteVigenteSpecification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                estudianteVigenteQueryRepository.findAll(spec, pageable)
                        .map(EstudianteQueryMapper::toReadModel));
    }
}
