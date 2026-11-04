package com.arquisoft.mapas_ruta.infrastructure.tarea.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.tarea.query.criteria.TareaMapaRutaCriteria;
import com.arquisoft.mapas_ruta.application.tarea.query.readmodel.TareaReadModel;
import com.arquisoft.mapas_ruta.application.tarea.query.secondaryport.TareaQueryOutputPort;
import com.arquisoft.mapas_ruta.infrastructure.tarea.query.secondaryadapter.repository.mapper.TareaQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TareaQueryOutputAdapter implements TareaQueryOutputPort {

    private final TareaQueryRepository tareaQueryRepository;
    private final TareaJpaSpecification specification;

    @Override
    public Optional<TareaReadModel> consultarPorId(UUID tarea) {
        return tareaQueryRepository.findById(tarea)
                .map(TareaQueryMapper::toReadModel);
    }

    @Override
    public PaginatedResult<TareaReadModel> consultarPorMapaRuta(TareaMapaRutaCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, TareaSortMapper::traducir);
        var spec = specification.deLaMapaRuta(criteria.getMapaRuta())
                .and(specification.desdeCriteria(criteria));

        return PaginationMapper.toResult(
                tareaQueryRepository.findAll(spec, pageable)
                        .map(TareaQueryMapper::toReadModel));
    }
}
