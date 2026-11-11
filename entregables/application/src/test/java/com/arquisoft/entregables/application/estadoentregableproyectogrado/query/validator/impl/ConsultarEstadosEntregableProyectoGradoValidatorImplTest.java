package com.arquisoft.entregables.application.estadoentregableproyectogrado.query.validator.impl;

import com.arquisoft.entregables.domain.entregableproyectogrado.exception.AsesorNoPropietarioEntregableProyectoGradoException;
import com.arquisoft.entregables.domain.entregableproyectogrado.exception.EntregableProyectoGradoNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEstadosEntregableProyectoGradoValidatorImplTest {

    private final ConsultarEstadosEntregableProyectoGradoValidatorImpl validator =
            new ConsultarEstadosEntregableProyectoGradoValidatorImpl();

    @Test
    void debePasar_cuandoAsesorEsElDelEntregable() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(UtilUUID.generarNuevoUUID(), asesor, asesor))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoEntregableNoExisteAunqueElSolicitanteDifiera() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(UtilUUID.generarNuevoUUID(),
                UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.generarNuevoUUID()))
                .isInstanceOf(EntregableProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoPropietario_cuandoAsesorNoEsElDelEntregable() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID()))
                .isInstanceOf(AsesorNoPropietarioEntregableProyectoGradoException.class);
    }
}
