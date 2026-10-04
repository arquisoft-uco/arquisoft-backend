package com.arquisoft.solicitudes.infrastructure.respuesta.query.secondaryadapter.repository;

import com.arquisoft.solicitudes.application.respuesta.query.criteria.RespuestaCriteria;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.solicitudes.application.respuesta.query.secondaryport.RespuestaQueryOutputPort;
import com.arquisoft.solicitudes.infrastructure.respuesta.query.secondaryadapter.repository.mapper.RespuestaQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RespuestaQueryOutputAdapter implements RespuestaQueryOutputPort {

    private final RespuestaQueryRepository respuestaQueryRepository;
    private final RespuestaJpaSpecification specification;

    @Override
    public PaginatedResult<RespuestaReadModel> consultar(RespuestaCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, RespuestaSortMapper::traducir);
        var spec = specification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                respuestaQueryRepository.findAll(spec, pageable)
                        .map(RespuestaQueryMapper::toReadModel));
    }
}
