package com.arquisoft.solicitudes.infrastructure.tiposolicitud.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TipoSolicitudQueryRepository extends QueryRepository<TipoSolicitudJpaQueryEntity, String> {

    @Query("""
            SELECT t FROM TipoSolicitudJpaQueryEntity t
            WHERE t.id IN (SELECT r.tipoSolicitudId FROM TipoSolicitudRolJpaQueryEntity r WHERE r.rol IN :roles)
            ORDER BY t.id
            """)
    List<TipoSolicitudJpaQueryEntity> findByRolIn(@Param("roles") List<String> roles);
}
