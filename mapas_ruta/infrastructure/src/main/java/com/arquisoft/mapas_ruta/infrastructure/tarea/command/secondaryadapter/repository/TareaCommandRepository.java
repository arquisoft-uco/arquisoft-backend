package com.arquisoft.mapas_ruta.infrastructure.tarea.command.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.infrastructure.tarea.command.secondaryadapter.entity.TareaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface TareaCommandRepository extends JpaRepository<TareaJpaEntity, UUID> {

    boolean existsByMapaRutaIdAndDescripcion(UUID mapaRutaId, String descripcion);

    boolean existsByMapaRutaIdAndDescripcionAndIdNot(UUID mapaRutaId, String descripcion, UUID id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE TareaJpaEntity t SET t.descripcion = :descripcion, t.asesorId = :asesorId WHERE t.id = :id")
    int actualizarInformacion(
            @Param("id") UUID id,
            @Param("descripcion") String descripcion,
            @Param("asesorId") UUID asesorId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE TareaJpaEntity t SET t.estadoItemId = :estadoItem WHERE t.id = :id")
    int actualizarEstadoItem(@Param("id") UUID id, @Param("estadoItem") String estadoItem);
}
