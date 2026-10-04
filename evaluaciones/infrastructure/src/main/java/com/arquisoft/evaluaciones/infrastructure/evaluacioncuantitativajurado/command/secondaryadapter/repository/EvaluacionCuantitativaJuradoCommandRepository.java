package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.secondaryadapter.entity.EvaluacionCuantitativaJuradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import java.util.UUID;

public interface EvaluacionCuantitativaJuradoCommandRepository
        extends JpaRepository<EvaluacionCuantitativaJuradoJpaEntity, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE EvaluacionCuantitativaJuradoJpaEntity e SET e.puntaje = :puntaje WHERE e.id = :id")
    int actualizarPuntaje(@Param("id") UUID id, @Param("puntaje") Integer puntaje);

    boolean existsByItemId(UUID itemId);

    @Query("select e.id from EvaluacionCuantitativaJuradoJpaEntity e "
            + "where e.evaluacionJuradoId = :evaluacionJurado and e.id in :evaluaciones")
    Set<UUID> findIdsPorEvaluacionJurado(
            @Param("evaluacionJurado") UUID evaluacionJurado, @Param("evaluaciones") Set<UUID> evaluaciones);

    @Modifying(clearAutomatically = true)
    @Query("delete from EvaluacionCuantitativaJuradoJpaEntity e "
            + "where e.evaluacionJuradoId = :evaluacionJurado and e.id in :evaluaciones")
    void eliminarPorIds(
            @Param("evaluacionJurado") UUID evaluacionJurado, @Param("evaluaciones") Set<UUID> evaluaciones);
}
