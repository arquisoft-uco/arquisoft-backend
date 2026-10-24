package com.arquisoft.fichas.infrastructure.estadoficha.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ActorRolNegocioQueryRepository
        extends QueryRepository<ActorRolNegocioJpaQueryEntity, ActorRolNegocioJpaQueryEntity.Clave> {

    @Query("SELECT a.rol FROM ActorRolNegocioJpaQueryEntity a WHERE a.actorId = :actor ORDER BY a.rol")
    List<String> findRolesByActor(@Param("actor") UUID actor);
}
