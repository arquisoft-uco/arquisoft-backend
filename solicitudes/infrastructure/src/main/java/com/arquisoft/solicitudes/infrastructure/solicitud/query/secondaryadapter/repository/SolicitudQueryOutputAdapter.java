package com.arquisoft.solicitudes.infrastructure.solicitud.query.secondaryadapter.repository;

import com.arquisoft.solicitudes.application.solicitud.query.criteria.SolicitudCriteria;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.solicitudes.application.solicitud.query.secondaryport.SolicitudQueryOutputPort;
import com.arquisoft.solicitudes.infrastructure.solicitud.query.secondaryadapter.repository.mapper.SolicitudQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SolicitudQueryOutputAdapter implements SolicitudQueryOutputPort {

    private final SolicitudQueryRepository solicitudQueryRepository;
    private final SolicitudJpaSpecification specification;

    @Override
    public PaginatedResult<SolicitudReadModel> consultar(SolicitudCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, SolicitudSortMapper::traducir);
        var spec = specification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                solicitudQueryRepository.findAll(spec, pageable)
                        .map(SolicitudQueryMapper::toReadModel));
    }
}
