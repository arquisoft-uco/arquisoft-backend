package com.arquisoft.mapas_ruta.application.tarea.command.validator.impl;

import com.arquisoft.mapas_ruta.domain.tarea.RemocionTareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.exception.TareaNoEncontradaException;
import com.arquisoft.mapas_ruta.domain.tarea.exception.TareaNoRemovibleException;
import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverInformacionTareaValidatorImplTest {

    private final RemoverInformacionTareaValidatorImpl validator = new RemoverInformacionTareaValidatorImpl();

    private static TareaDomain tarea(EstadoItem estado) {
        return TareaDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "Redactar capitulo", UtilUUID.generarNuevoUUID(), estado);
    }

    @Test
    void debePasar_cuandoTareaExisteYNoFinalizada() {
        // Arrange
        var remocion = RemocionTareaDomain.crear(UtilUUID.generarNuevoUUID());
        var tarea = tarea(EstadoItem.EN_PROGRESO);

        // Act & Assert
        assertThatCode(() -> validator.validar(remocion, tarea)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarTareaNoEncontrada_cuandoNoExiste() {
        // Arrange
        var remocion = RemocionTareaDomain.crear(UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(remocion, TareaDomain.VACIO))
                .isInstanceOf(TareaNoEncontradaException.class);
    }

    @Test
    void debeLanzarTareaNoRemovible_cuandoFinalizada() {
        // Arrange
        var remocion = RemocionTareaDomain.crear(UtilUUID.generarNuevoUUID());
        var tarea = tarea(EstadoItem.FINALIZADA);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(remocion, tarea))
                .isInstanceOf(TareaNoRemovibleException.class);
    }
}
