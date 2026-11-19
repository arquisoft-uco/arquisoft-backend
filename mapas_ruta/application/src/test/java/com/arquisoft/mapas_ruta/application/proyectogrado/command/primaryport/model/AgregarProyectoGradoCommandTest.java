package com.arquisoft.mapas_ruta.application.proyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarProyectoGradoCommandTest {

    @Test
    void debeConvertirLosUuid_cuandoLosDatosSonValidos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var ficha = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-01T10:00:00Z");

        // Act
        var command = AgregarProyectoGradoCommand.crear(
                proyecto.toString(), ficha.toString(), "Titulo", coordinador.toString(), ocurridoEn);

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyecto);
        assertThat(command.fichaPerfil()).isEqualTo(ficha);
        assertThat(command.tituloProyecto()).isEqualTo("Titulo");
        assertThat(command.coordinador()).isEqualTo(coordinador);
        assertThat(command.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeLanzar400_cuandoDatoMalformado() {
        // Act & Assert
        assertThatThrownBy(() -> AgregarProyectoGradoCommand.crear("no-uuid", null, " ", "otro", null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        MapasRutaCodes.ProyectoGrado.ID_REQUERIDO,
                                        MapasRutaCodes.ProyectoGrado.FICHA_PERFIL_REQUERIDA,
                                        MapasRutaCodes.ProyectoGrado.TITULO_PROYECTO_REQUERIDO,
                                        MapasRutaCodes.ProyectoGrado.COORDINADOR_REQUERIDO,
                                        MapasRutaCodes.ProyectoGrado.OCURRIDO_EN_REQUERIDO));
    }
}
