package com.arquisoft.mapas_ruta.application.planestimado.command.validator.impl;

import com.arquisoft.mapas_ruta.domain.planestimado.RemocionPlanEstimadoDomain;
import com.arquisoft.mapas_ruta.domain.planestimado.exception.PlanEstimadoNoEncontradoException;
import com.arquisoft.mapas_ruta.domain.planestimado.exception.PlanEstimadoTareaFinalizadaException;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverPlanEstimadoValidatorImplTest {

    private final RemoverPlanEstimadoValidatorImpl validator = new RemoverPlanEstimadoValidatorImpl();

    private static TareaDomain tarea(EstadoItem estado) {
        return TareaDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "Redactar capitulo", UtilUUID.generarNuevoUUID(), estado);
    }

    @Test
    void debePasar_cuandoPlanExiste() {
        // Arrange
        var remocion = RemocionPlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatCode(() -> validator.validar(remocion, true, tarea(EstadoItem.NUEVA))).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarPlanEstimadoNoEncontradoException_cuandoPlanNoExiste() {
        // Arrange
        var remocion = RemocionPlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(remocion, false, tarea(EstadoItem.NUEVA)))
                .isInstanceOf(PlanEstimadoNoEncontradoException.class);
    }

    @Test
    void debeLanzarTareaFinalizada_cuandoLaTareaEstaFinalizada() {
        // Arrange
        var remocion = RemocionPlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(remocion, true, tarea(EstadoItem.FINALIZADA)))
                .isInstanceOf(PlanEstimadoTareaFinalizadaException.class);
    }

    @Test
    void debeLanzarNoEncontrado_antesQueTareaFinalizada_cuandoPlanNoExiste() {
        // Arrange
        var remocion = RemocionPlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(remocion, false, tarea(EstadoItem.FINALIZADA)))
                .isInstanceOf(PlanEstimadoNoEncontradoException.class);
    }
}
