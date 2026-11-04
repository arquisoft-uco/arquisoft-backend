package com.arquisoft.entregables.application.artefactoentregable.query.validator.impl;

import com.arquisoft.entregables.domain.entregableproyectogrado.exception.AsesorNoPropietarioEntregableProyectoGradoException;
import com.arquisoft.entregables.domain.entregableproyectogrado.exception.EntregableProyectoGradoNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarArtefactosEntregableValidatorImplTest {

    private final ConsultarArtefactosEntregableValidatorImpl validator = new ConsultarArtefactosEntregableValidatorImpl();

    @Test
    void debePasar_cuandoElEntregableEsDelAsesor() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(UtilUUID.generarNuevoUUID(), asesor, asesor))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoElAsesorAsignadoEsElPorDefecto() {
        // Arrange
        var porDefecto = UtilUUID.obtenerUUIDPorDefecto();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(UtilUUID.generarNuevoUUID(), porDefecto, porDefecto))
                .isInstanceOf(EntregableProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarAsesorNoPropietario_cuandoElEntregableEsDeOtroAsesor() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID()))
                .isInstanceOf(AsesorNoPropietarioEntregableProyectoGradoException.class);
    }
}
