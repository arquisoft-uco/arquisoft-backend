package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.secondaryadapter.entity.ObservacionItemAsesorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ObservacionItemAsesorCommandRepository
        extends JpaRepository<ObservacionItemAsesorJpaEntity, UUID> {

    boolean existsByEvaluacionCuantitativaAsesorIdAndDescripcion(UUID evaluacionCuantitativaAsesorId, String descripcion);

    @Query("select count(o) > 0 from ObservacionItemAsesorJpaEntity o "
            + "where o.descripcion = :descripcion and o.id <> :observacion and o.evaluacionCuantitativaAsesorId = ("
            + "select propia.evaluacionCuantitativaAsesorId from ObservacionItemAsesorJpaEntity propia "
            + "where propia.id = :observacion)")
    boolean existeOtraConDescripcion(
            @Param("observacion") UUID observacion, @Param("descripcion") String descripcion);

    @Modifying(clearAutomatically = true)
    @Query("update ObservacionItemAsesorJpaEntity o set o.descripcion = :descripcion where o.id = :id")
    void actualizarDescripcion(@Param("id") UUID id, @Param("descripcion") String descripcion);
}
