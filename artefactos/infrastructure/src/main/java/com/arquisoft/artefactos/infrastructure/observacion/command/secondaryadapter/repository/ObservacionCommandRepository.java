package com.arquisoft.artefactos.infrastructure.observacion.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.observacion.command.secondaryadapter.entity.ObservacionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ObservacionCommandRepository extends JpaRepository<ObservacionJpaEntity, UUID> {

    boolean existsByRevisionAsesorAndDescripcion(UUID revisionAsesor, String descripcion);

    boolean existsByRevisionAsesor(UUID revisionAsesor);

    @NativeQuery("""
            SELECT EXISTS (
                SELECT 1
                FROM revision_asesor actual
                JOIN version_artefacto va ON va.id = actual.version_artefacto_id
                JOIN version_artefacto anterior ON anterior.artefacto_id = va.artefacto_id
                    AND anterior.version_artefacto = va.version_artefacto - 1
                JOIN revision_asesor previa ON previa.version_artefacto_id = anterior.id
                JOIN observacion o ON o.revision_asesor_id = previa.id
                WHERE actual.id = :revisionAsesor AND o.estado_observacion_id <> :estadoCerrada
            )
            """)
    boolean existenNoCerradasDeVersionAnterior(
            @Param("revisionAsesor") UUID revisionAsesor, @Param("estadoCerrada") String estadoCerrada);

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
