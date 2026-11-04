package com.arquisoft.entregables.application.entregableproyectogrado.command.validator.impl;

import com.arquisoft.entregables.domain.asesor.exception.AsesorNoVigenteException;
import com.arquisoft.entregables.domain.asesor.model.VigenciaAsesor;
import com.arquisoft.entregables.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.entregables.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GenerarEntregableProyectoGradoValidatorImplTest {

    private final GenerarEntregableProyectoGradoValidatorImpl validator =
            new GenerarEntregableProyectoGradoValidatorImpl();

    @Test
    void debePasar_cuandoElProyectoExisteYElAsesorEstaVigente() {
        // Arrange
        var proyecto = new ExistenciaProyectoGrado(UtilUUID.generarNuevoUUID(), true);
        var asesor = new VigenciaAsesor(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> validator.validar(proyecto, asesor)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarProyectoNoEncontrado_cuandoElProyectoNoExiste() {
        // Arrange
        var proyecto = new ExistenciaProyectoGrado(UtilUUID.generarNuevoUUID(), false);
        var asesor = new VigenciaAsesor(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(proyecto, asesor))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarAsesorNoVigente_cuandoElAsesorNoEstaVigente() {
        // Arrange
        var proyecto = new ExistenciaProyectoGrado(UtilUUID.generarNuevoUUID(), true);
        var asesor = new VigenciaAsesor(UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(proyecto, asesor))
                .isInstanceOf(AsesorNoVigenteException.class);
    }

    @Test
    void debePriorizarElProyecto_cuandoAmbasReglasFallan() {
        // Arrange
        var proyecto = new ExistenciaProyectoGrado(UtilUUID.generarNuevoUUID(), false);
        var asesor = new VigenciaAsesor(UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(proyecto, asesor))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }
}
