package com.arquisoft.artefactos.application.proyectogrado.command.validator.impl;

import com.arquisoft.artefactos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.model.EstadoProyecto;
import com.arquisoft.artefactos.domain.proyectogrado.exception.ProyectoGradoCongeladoException;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProyectoGradoNoCongeladoValidatorImplTest {

    private final ProyectoGradoNoCongeladoValidatorImpl validator = new ProyectoGradoNoCongeladoValidatorImpl();

    @Test
    void debePasar_cuandoNoEstaCongelado() {
        // Arrange
        var proyecto = ProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "Proyecto", UtilFecha.VACIO, EstadoProyecto.EN_PROCESO, null);

        // Act & Assert
        assertThatCode(() -> validator.validar(proyecto)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoCongelado() {
        // Arrange
        var proyecto = ProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "Proyecto", Instant.parse("2026-11-01T10:00:00Z"), EstadoProyecto.EN_PROCESO, null);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(proyecto)).isInstanceOf(ProyectoGradoCongeladoException.class);
    }
}
