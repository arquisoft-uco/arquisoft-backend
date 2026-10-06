package com.arquisoft.solicitudes.infrastructure.tiposolicitud.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.Collection;
import java.util.List;

public interface TipoSolicitudQueryRepository extends QueryRepository<TipoSolicitudJpaQueryEntity, String> {

    List<TipoSolicitudJpaQueryEntity> findByIdIn(Collection<String> ids);
}
