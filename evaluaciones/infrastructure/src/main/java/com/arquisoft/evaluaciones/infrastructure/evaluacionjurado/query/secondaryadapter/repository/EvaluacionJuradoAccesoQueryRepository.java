package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface EvaluacionJuradoAccesoQueryRepository
        extends QueryRepository<EvaluacionJuradoJpaQueryEntity, UUID> {

    @Query(value = """
            SELECT EXISTS (
                SELECT 1
                FROM evaluacion_jurado ej
                JOIN evaluacion e ON e.id = ej.evaluacion_id
                JOIN entregable_proyecto_acceso epa ON epa.entregable_id = e.entregable_id AND epa.activo = true
                JOIN proyecto_estudiante_acceso pea ON pea.proyecto_id = epa.proyecto_id AND pea.activo = true
                WHERE ej.id = :evaluacionJurado AND pea.estudiante_id = :estudiante
            )
            """, nativeQuery = true)
    boolean existePropiedad(@Param("evaluacionJurado") UUID evaluacionJurado, @Param("estudiante") UUID estudiante);
}
