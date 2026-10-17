package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.maparuta.query.criteria.MapaRutaCriteria;
import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaReadModel;
import com.arquisoft.mapas_ruta.application.maparuta.query.secondaryport.MapaRutaQueryOutputPort;
import com.arquisoft.mapas_ruta.infrastructure.maparuta.query.secondaryadapter.repository.mapper.MapaRutaQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MapaRutaQueryOutputAdapter implements MapaRutaQueryOutputPort {

    private final MapaRutaQueryRepository mapaRutaQueryRepository;
    private final MapaRutaJpaSpecification specification;

    @Override
    public PaginatedResult<MapaRutaReadModel> consultarTodos(MapaRutaCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, MapaRutaSortMapper::traducir);
        var spec = specification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                mapaRutaQueryRepository.findAll(spec, pageable)
                        .map(MapaRutaQueryMapper::toReadModel));
    }
}
