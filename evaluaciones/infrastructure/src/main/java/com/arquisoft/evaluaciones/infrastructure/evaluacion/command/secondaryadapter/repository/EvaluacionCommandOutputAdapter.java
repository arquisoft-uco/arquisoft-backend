package com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.EvaluacionOutputPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.util.UUID;

// Solo transporte SQL: la decision de a que estado transicionar ya la tomo el dominio
// (InicioEvaluacionDomain). Participa en la misma transaccion/bloqueo que el SELECT ... FOR
// UPDATE de ContextoRegistroEvaluacionJuradoCommandOutputAdapter.
@Component
public class EvaluacionCommandOutputAdapter implements EvaluacionOutputPort {

    private static final String SQL_ACTUALIZAR_ESTADO =
            "UPDATE evaluacion SET estado_evaluacion_id = :estado WHERE id = :evaluacion";

    @PersistenceContext(unitName = "evaluaciones")
    private EntityManager entityManager;

    @Override
    public void actualizarEstado(UUID evaluacion, String estado) {
        entityManager.createNativeQuery(SQL_ACTUALIZAR_ESTADO)
                .setParameter("estado", estado)
                .setParameter("evaluacion", evaluacion)
                .executeUpdate();
    }
}
