package com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.AsesoriaRevisionItemEntity;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.PertenenciaRevisionItemEntity;
import com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.entity.RevisionItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RevisionItemCommandRepository extends JpaRepository<RevisionItemJpaEntity, UUID> {

    long countByItemId(UUID itemId);

    @Query("""
            SELECT new com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.PertenenciaRevisionItemEntity(
                i.fichaPerfilId,
                CASE WHEN EXISTS (
                    SELECT 1 FROM EstudianteFichaPerfilJpaEntity ef
                    WHERE ef.fichaPerfilId = i.fichaPerfilId AND ef.estudianteId = :estudiante
                ) THEN true ELSE false END,
                r.estadoRevision.id)
            FROM RevisionItemJpaEntity r
            JOIN ItemFichaPerfilJpaEntity i ON i.id = r.itemId
            WHERE r.id = :revisionItem
            """)
    Optional<PertenenciaRevisionItemEntity> obtenerPertenencia(@Param("revisionItem") UUID revisionItem,
                                                               @Param("estudiante") UUID estudiante);

    @Query("""
            SELECT new com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.AsesoriaRevisionItemEntity(
                i.fichaPerfilId,
                f.asesorFicha.id,
                r.estadoRevision.id)
            FROM RevisionItemJpaEntity r
            JOIN ItemFichaPerfilJpaEntity i ON i.id = r.itemId
            JOIN FichaPerfilJpaEntity f ON f.id = i.fichaPerfilId
            WHERE r.id = :revisionItem
            """)
    Optional<AsesoriaRevisionItemEntity> obtenerAsesoria(@Param("revisionItem") UUID revisionItem);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM RevisionItemJpaEntity r WHERE r.id = :id")
    int removerPorId(@Param("id") UUID id);

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE RevisionItemJpaEntity r SET r.estadoRevision.id = :estadoNuevo
            WHERE r.id = :id AND r.estadoRevision.id = :estadoActual
            """)
    int actualizarEstado(@Param("id") UUID id,
                         @Param("estadoActual") String estadoActual,
                         @Param("estadoNuevo") String estadoNuevo);
}
