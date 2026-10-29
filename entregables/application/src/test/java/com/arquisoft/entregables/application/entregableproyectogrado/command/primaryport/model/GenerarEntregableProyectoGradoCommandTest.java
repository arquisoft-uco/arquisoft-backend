package com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GenerarEntregableProyectoGradoCommandTest {

    @Test
    void debeCrear_cuandoLosDatosSonValidos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var command = GenerarEntregableProyectoGradoCommand.crear(proyecto, asesor);

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyecto);
        assertThat(command.asesor()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularErrores_cuandoFaltanProyectoYAsesor() {
        // Act & Assert
        assertThatThrownBy(() -> GenerarEntregableProyectoGradoCommand.crear(null, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex ->
                        assertThat(ex.getValidationResult().getErrores())
                                .extracting("codigoError")
                                .containsExactlyInAnyOrder(
                                        EntregablesCodes.EntregableProyectoGrado.PROYECTO_GRADO_REQUERIDO,
                                        EntregablesCodes.EntregableProyectoGrado.ASESOR_REQUERIDO));
    }
}
