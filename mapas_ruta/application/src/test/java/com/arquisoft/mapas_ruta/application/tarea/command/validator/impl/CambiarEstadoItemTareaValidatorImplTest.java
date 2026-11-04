package com.arquisoft.mapas_ruta.application.tarea.command.validator.impl;

import com.arquisoft.mapas_ruta.domain.tarea.CambioEstadoItemTareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.exception.TareaNoEncontradaException;
import com.arquisoft.mapas_ruta.domain.tarea.exception.TransicionEstadoItemNoPermitidaException;
import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarEstadoItemTareaValidatorImplTest {

    private final CambiarEstadoItemTareaValidatorImpl validator = new CambiarEstadoItemTareaValidatorImpl();

    private static TareaDomain tarea(EstadoItem estado) {
        return TareaDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "Redactar capitulo", UtilUUID.generarNuevoUUID(), estado);
    }

    @Test
    void debePasar_cuandoTodoEsValido() {
        // Arrange
        var cambio = CambioEstadoItemTareaDomain.crear(UtilUUID.generarNuevoUUID(), "EN_PROGRESO");
        var tarea = tarea(EstadoItem.NUEVA);

        // Act & Assert
        assertThatCode(() -> validator.validar(cambio, tarea)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarTareaNoEncontrada_primero() {
        // Arrange
        var cambio = CambioEstadoItemTareaDomain.crear(UtilUUID.generarNuevoUUID(), "EN_PROGRESO");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, TareaDomain.VACIO))
                .isInstanceOf(TareaNoEncontradaException.class);
    }

    @Test
    void debeLanzarTransicionNoPermitida_cuandoLaTareaExisteYElCambioNoEsValido() {
        // Arrange
        var cambio = CambioEstadoItemTareaDomain.crear(UtilUUID.generarNuevoUUID(), "NUEVA");
        var tarea = tarea(EstadoItem.EN_PROGRESO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, tarea))
                .isInstanceOf(TransicionEstadoItemNoPermitidaException.class);
    }
}
