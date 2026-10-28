package com.arquisoft.artefactos.application.artefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsignarArtefactosCommandTest {

    @Test
    void debeCrearConVersionesEnOrden_cuandoDatosValidos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var primera = UtilUUID.generarNuevoUUID();
        var segunda = UtilUUID.generarNuevoUUID();

        // Act
        var command = AsignarArtefactosCommand.crear(
                proyecto.toString(), asesor, List.of(primera.toString(), segunda.toString()));

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyecto);
        assertThat(command.asesor()).isEqualTo(asesor);
        assertThat(command.versionesRepositorioArtefacto()).containsExactly(primera, segunda);
    }

    @Test
    void debeAcumularErrores_cuandoProyectoInvalidoYListaAusente() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> AsignarArtefactosCommand.crear("no-es-uuid", asesor, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting(error -> error.campo()).containsExactlyInAnyOrder(
                            ArtefactosFields.Artefacto.PROYECTO_GRADO,
                            ArtefactosFields.Artefacto.VERSIONES_REPOSITORIO_ARTEFACTO);
                    assertThat(errores).extracting(error -> error.codigoError()).containsExactlyInAnyOrder(
                            ArtefactosCodes.Artefacto.PROYECTO_GRADO_REQUERIDO,
                            ArtefactosCodes.Artefacto.VERSIONES_REQUERIDAS);
                });
    }

    @Test
    void debeRechazar_cuandoLaListaEstaVacia() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID().toString();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> AsignarArtefactosCommand.crear(proyecto, asesor, new ArrayList<>()))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(ArtefactosCodes.Artefacto.VERSIONES_REQUERIDAS)));
    }

    @Test
    void debeRechazar_cuandoUnaVersionNoEsUuid() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID().toString();
        var asesor = UtilUUID.generarNuevoUUID();
        var versiones = List.of(UtilUUID.generarNuevoUUID().toString(), "no-es-uuid");

        // Act & Assert
        assertThatThrownBy(() -> AsignarArtefactosCommand.crear(proyecto, asesor, versiones))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(ArtefactosCodes.Artefacto.VERSION_REPOSITORIO_ARTEFACTO_REQUERIDA)));
    }

    @Test
    void debeRechazar_cuandoUnaVersionSeRepite() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID().toString();
        var asesor = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        assertThatThrownBy(() -> AsignarArtefactosCommand.crear(proyecto, asesor, List.of(version, version)))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(ArtefactosCodes.Artefacto.VERSIONES_DUPLICADAS)));
    }
}
