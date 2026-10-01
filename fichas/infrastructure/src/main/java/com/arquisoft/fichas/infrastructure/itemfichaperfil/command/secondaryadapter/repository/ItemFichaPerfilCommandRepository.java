package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.entity.PertenenciaItemFichaPerfilEntity;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.command.secondaryadapter.entity.ItemFichaPerfilJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ItemFichaPerfilCommandRepository extends JpaRepository<ItemFichaPerfilJpaEntity, UUID> {

    boolean existsByFichaPerfilIdAndTipoItemId(UUID fichaPerfilId, String tipoItemId);

    @Query("SELECT i.fichaPerfilId FROM ItemFichaPerfilJpaEntity i WHERE i.id = :id")
    Optional<UUID> obtenerFichaPerfilId(@Param("id") UUID id);

    @Query("""
            SELECT new com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.entity.PertenenciaItemFichaPerfilEntity(
                i.fichaPerfilId,
                CASE WHEN EXISTS (
                    SELECT 1 FROM EstudianteFichaPerfilJpaEntity ef
                    WHERE ef.fichaPerfilId = i.fichaPerfilId AND ef.estudianteId = :estudiante
                ) THEN true ELSE false END,
                e.id, e.estadoFicha.id, e.fechaActualizacion)
            FROM ItemFichaPerfilJpaEntity i
            LEFT JOIN EstadoFichaPerfilJpaEntity e
                ON e.fichaPerfilId = i.fichaPerfilId
                AND NOT EXISTS (
                    SELECT 1 FROM EstadoFichaPerfilJpaEntity e2
                    WHERE e2.fichaPerfilId = i.fichaPerfilId
                    AND (e2.fechaActualizacion > e.fechaActualizacion
                        OR (e2.fechaActualizacion = e.fechaActualizacion AND e2.id > e.id)))
            WHERE i.id = :item
            """)
    Optional<PertenenciaItemFichaPerfilEntity> obtenerPertenencia(@Param("item") UUID item,
                                                                  @Param("estudiante") UUID estudiante);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ItemFichaPerfilJpaEntity i SET i.contenido = :contenido WHERE i.id = :id")
    int actualizarContenido(@Param("id") UUID id, @Param("contenido") String contenido);
}
