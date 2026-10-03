package com.arquisoft.fichas.infrastructure.observacionitem.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.observacionitem.command.secondaryport.entity.ContextoObservacionItemEntity;
import com.arquisoft.fichas.infrastructure.observacionitem.command.secondaryadapter.entity.ObservacionItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ObservacionItemCommandRepository extends JpaRepository<ObservacionItemJpaEntity, UUID> {

    long countByRevisionItemIdAndObservacion(UUID revisionItemId, String observacion);

    @Query("""
            SELECT new com.arquisoft.fichas.application.observacionitem.command.secondaryport.entity.ContextoObservacionItemEntity(
                r.id, r.estadoRevision.id, f.id, f.asesorFicha.id)
            FROM ObservacionItemJpaEntity o
            JOIN RevisionItemJpaEntity r ON r.id = o.revisionItemId
            JOIN ItemFichaPerfilJpaEntity i ON i.id = r.itemId
            JOIN FichaPerfilJpaEntity f ON f.id = i.fichaPerfilId
            WHERE o.id = :observacionItem
            """)
    Optional<ContextoObservacionItemEntity> obtenerContexto(@Param("observacionItem") UUID observacionItem);

    @Query("""
            SELECT COUNT(o2)
            FROM ObservacionItemJpaEntity o
            JOIN ObservacionItemJpaEntity o2 ON o2.revisionItemId = o.revisionItemId
            WHERE o.id = :observacionItem AND o2.id <> :observacionItem AND o2.observacion = :observacion
            """)
    long contarOtrasIgualesEnRevision(@Param("observacionItem") UUID observacionItem,
                                      @Param("observacion") String observacion);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ObservacionItemJpaEntity o SET o.observacion = :observacion WHERE o.id = :id")
    int actualizarObservacion(@Param("id") UUID id, @Param("observacion") String observacion);
}
