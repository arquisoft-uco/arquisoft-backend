package com.arquisoft.artefactos.infrastructure.observacion.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.observacion.command.secondaryadapter.entity.ObservacionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ObservacionCommandRepository extends JpaRepository<ObservacionJpaEntity, UUID> {

    boolean existsByRevisionAsesorAndDescripcion(UUID revisionAsesor, String descripcion);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ObservacionJpaEntity o SET o.estado = :estadoDestino "
            + "WHERE o.revisionAsesor = :revisionAsesor AND o.estado = :estadoOrigen")
    int avanzarEstadoPorRevisionAsesor(
            @Param("revisionAsesor") UUID revisionAsesor,
            @Param("estadoOrigen") String estadoOrigen,
            @Param("estadoDestino") String estadoDestino);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ObservacionJpaEntity o SET o.estado = :estado WHERE o.id = :observacion")
    int actualizarEstado(@Param("observacion") UUID observacion, @Param("estado") String estado);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ObservacionJpaEntity o SET o.estado = :estadoDestino "
            + "WHERE o.id = :observacion AND o.estado = :estadoActual")
    int devolverEstado(
            @Param("observacion") UUID observacion,
            @Param("estadoActual") String estadoActual,
            @Param("estadoDestino") String estadoDestino);
}
