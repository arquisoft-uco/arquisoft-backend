package com.arquisoft.fichas.infrastructure.estadoficha.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EstadoFichaQueryRepository extends QueryRepository<EstadoFichaJpaQueryEntity, String> {

    @Query("""
            SELECT e FROM EstadoFichaJpaQueryEntity e
            WHERE e.id IN (SELECT r.estadoFichaId FROM EstadoFichaRolJpaQueryEntity r WHERE r.rol IN :roles)
            ORDER BY e.id
            """)
    List<EstadoFichaJpaQueryEntity> findByRolIn(@Param("roles") List<String> roles);
}
