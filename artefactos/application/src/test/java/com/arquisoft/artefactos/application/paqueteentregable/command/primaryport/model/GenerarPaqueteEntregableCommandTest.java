package com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GenerarPaqueteEntregableCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act
        var command = GenerarPaqueteEntregableCommand.crear(entregable.toString(), proyecto.toString(), 2);

        // Assert
        assertThat(command.entregableProyectoGrado()).isEqualTo(entregable);
        assertThat(command.proyectoGrado()).isEqualTo(proyecto);
        assertThat(command.versionEntregable()).isEqualTo(2);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoUuidInvalidoOVersionCero() {
        // Act & Assert
        assertThatThrownBy(() -> GenerarPaqueteEntregableCommand.crear("no-uuid", null, 0))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex -> {
                    var resultado = ex.getValidationResult();
                    assertThat(resultado.tieneErroresDeCampo(
                            ArtefactosFields.PaqueteEntregable.ENTREGABLE_PROYECTO_GRADO)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(ArtefactosFields.PaqueteEntregable.PROYECTO_GRADO)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(
                            ArtefactosFields.PaqueteEntregable.VERSION_ENTREGABLE)).isTrue();
                });
    }
}
