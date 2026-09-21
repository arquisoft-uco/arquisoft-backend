package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.secondaryadapter.entity.ObservacionItemJuradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ObservacionItemJuradoCommandRepository
        extends JpaRepository<ObservacionItemJuradoJpaEntity, UUID> {

    boolean existsByEvaluacionCuantitativaJuradoIdAndDescripcion(UUID evaluacionCuantitativaJuradoId, String descripcion);

    @Query("""
            select count(o) > 0 from ObservacionItemJuradoJpaEntity o
            where o.id <> :observacion
              and o.descripcion = :descripcion
              and o.evaluacionCuantitativaJuradoId = (
                    select a.evaluacionCuantitativaJuradoId from ObservacionItemJuradoJpaEntity a
                    where a.id = :observacion)
            """)
    boolean existeOtraConDescripcion(
            @Param("observacion") UUID observacion, @Param("descripcion") String descripcion);

    @Modifying(clearAutomatically = true)
    @Query("update ObservacionItemJuradoJpaEntity o set o.descripcion = :descripcion where o.id = :id")
    int actualizarDescripcion(@Param("id") UUID id, @Param("descripcion") String descripcion);
}
