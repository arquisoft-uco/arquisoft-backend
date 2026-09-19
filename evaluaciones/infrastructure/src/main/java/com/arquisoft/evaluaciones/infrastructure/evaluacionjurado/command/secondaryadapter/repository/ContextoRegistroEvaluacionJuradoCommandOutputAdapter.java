package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.ContextoRegistroEvaluacionJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.ContextoRegistroEvaluacionJuradoEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Bloqueo pesimista explicito (FOR UPDATE OF e, ej) porque el JOIN entre evaluacion y
// evaluacion_jurado no se expresa con @Lock de Spring Data sobre una unica entidad. El
// futuro flujo de finalizacion debe adquirir el mismo bloqueo dentro de su propia
// transaccion.
@Component
public class ContextoRegistroEvaluacionJuradoCommandOutputAdapter
        implements ContextoRegistroEvaluacionJuradoOutputPort {

    private static final String SQL_CONTEXTO = """
            SELECT ej.id AS id, e.id AS evaluacion,
                   e.estado_evaluacion_id AS estado, e.entregable_id AS entregable
            FROM evaluacion_jurado ej
            JOIN evaluacion e ON e.id = ej.evaluacion_id
            WHERE ej.id = :evaluacionJurado
            FOR UPDATE OF e, ej
            """;

    @PersistenceContext(unitName = "evaluaciones")
    private EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public Optional<ContextoRegistroEvaluacionJuradoEntity> obtenerContextoBloqueado(UUID evaluacionJurado) {
        List<Object[]> filas = entityManager.createNativeQuery(SQL_CONTEXTO)
                .setParameter("evaluacionJurado", evaluacionJurado)
                .getResultList();

        if (filas.isEmpty()) {
            return Optional.empty();
        }

        Object[] fila = filas.get(0);
        return Optional.of(new ContextoRegistroEvaluacionJuradoEntity(
                (UUID) fila[0],
                (UUID) fila[1],
                (String) fila[2],
                (UUID) fila[3]));
    }
}
