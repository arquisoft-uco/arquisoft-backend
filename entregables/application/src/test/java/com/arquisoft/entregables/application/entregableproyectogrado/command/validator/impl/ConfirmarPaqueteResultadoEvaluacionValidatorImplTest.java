package com.arquisoft.entregables.application.entregableproyectogrado.command.validator.impl;

import com.arquisoft.entregables.domain.entregableproyectogrado.ResultadoEvaluacionEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;
import com.arquisoft.entregables.domain.paqueteentregable.exception.PaqueteEntregableNoDisponibleException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConfirmarPaqueteResultadoEvaluacionValidatorImplTest {

    private final ConfirmarPaqueteResultadoEvaluacionValidatorImpl validator =
            new ConfirmarPaqueteResultadoEvaluacionValidatorImpl();

    @Test
    void debeLanzar_cuandoElResultadoEsAprobadoYNoHayPaquete() {
        // Arrange
        var aprobado = ResultadoEvaluacionEntregableProyectoGradoDomain.crear(
                UtilUUID.generarNuevoUUID(), "APROBADO_CON_OBSERVACIONES");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(aprobado, PaqueteEntregableDomain.VACIO))
                .isInstanceOf(PaqueteEntregableNoDisponibleException.class);
    }

    @Test
    void debePasar_cuandoElResultadoEsRechazadoSinPaqueteOAprobadoConPaquete() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var rechazado = ResultadoEvaluacionEntregableProyectoGradoDomain.crear(entregable, "RECHAZADO");
        var aprobado = ResultadoEvaluacionEntregableProyectoGradoDomain.crear(entregable, "APROBADO");
        var paquete = PaqueteEntregableDomain.reconstruir(UtilUUID.generarNuevoUUID(), entregable, "entregables",
                "paquetes/a.zip", "a.zip", 10L, Instant.now());

        // Act & Assert
        assertThatCode(() -> validator.validar(rechazado, PaqueteEntregableDomain.VACIO)).doesNotThrowAnyException();
        assertThatCode(() -> validator.validar(aprobado, paquete)).doesNotThrowAnyException();
    }
}
