package com.arquisoft.mapas_ruta.application.planestimado.command.validator.impl;

import com.arquisoft.mapas_ruta.domain.planestimado.ModificacionPlanEstimadoDomain;
import com.arquisoft.mapas_ruta.domain.planestimado.exception.PlanEstimadoNoEncontradoException;
import com.arquisoft.mapas_ruta.domain.planestimado.exception.PlanEstimadoTareaFinalizadaException;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificarPlanEstimadoValidatorImplTest {

    private final ModificarPlanEstimadoValidatorImpl validator = new ModificarPlanEstimadoValidatorImpl();

    private static TareaDomain tarea(EstadoItem estado) {
        return TareaDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "Redactar capitulo", UtilUUID.generarNuevoUUID(), estado);
    }

    @Test
    void debePasar_cuandoPlanExiste() {
        // Arrange
        var modificacion = ModificacionPlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID(), "Texto");

        // Act & Assert
        assertThatCode(() -> validator.validar(modificacion, true, tarea(EstadoItem.NUEVA))).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoPlanNoExiste() {
        // Arrange
        var modificacion = ModificacionPlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID(), "Texto");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, false, tarea(EstadoItem.NUEVA)))
                .isInstanceOf(PlanEstimadoNoEncontradoException.class);
    }

    @Test
    void debeLanzarTareaFinalizada_cuandoLaTareaEstaFinalizada() {
        // Arrange
        var modificacion = ModificacionPlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID(), "Texto");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, true, tarea(EstadoItem.FINALIZADA)))
                .isInstanceOf(PlanEstimadoTareaFinalizadaException.class);
    }

    @Test
    void debeLanzarNoEncontrado_antesQueTareaFinalizada_cuandoPlanNoExiste() {
        // Arrange
        var modificacion = ModificacionPlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID(), "Texto");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, false, tarea(EstadoItem.FINALIZADA)))
                .isInstanceOf(PlanEstimadoNoEncontradoException.class);
    }
}
