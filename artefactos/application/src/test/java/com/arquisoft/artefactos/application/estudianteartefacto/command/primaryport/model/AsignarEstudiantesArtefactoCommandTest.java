package com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class AsignarEstudiantesArtefactoCommandTest {

    @Test
    void debeCrearConEstudiantesEnOrden_cuandoDatosValidos() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var primero = UtilUUID.generarNuevoUUID();
        var segundo = UtilUUID.generarNuevoUUID();

        // Act
        var command = AsignarEstudiantesArtefactoCommand.crear(
                artefacto, asesor, List.of(primero.toString(), segundo.toString()));

        // Assert
        assertThat(command.artefacto()).isEqualTo(artefacto);
        assertThat(command.asesor()).isEqualTo(asesor);
        assertThat(command.estudiantes()).containsExactly(primero, segundo);
    }

    @Test
    void debeAcumularErrores_cuandoFaltanArtefactoYAsesorYLaListaEstaAusente() {
        // Act & Assert
        assertThatThrownBy(() -> AsignarEstudiantesArtefactoCommand.crear(null, null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(ValidationError::campo, ValidationError::codigoError)
                        .containsExactlyInAnyOrder(
                                tuple(ArtefactosFields.EstudianteArtefacto.ARTEFACTO,
                                        ArtefactosCodes.EstudianteArtefacto.ARTEFACTO_ID_REQUERIDO),
                                tuple(ArtefactosFields.EstudianteArtefacto.ASESOR,
                                        ArtefactosCodes.EstudianteArtefacto.ASESOR_REQUERIDO),
                                tuple(ArtefactosFields.EstudianteArtefacto.ESTUDIANTES,
                                        ArtefactosCodes.EstudianteArtefacto.ESTUDIANTES_REQUERIDOS)));
    }

    @Test
    void debeRechazar_cuandoLaListaEstaVacia() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> AsignarEstudiantesArtefactoCommand.crear(artefacto, asesor, new ArrayList<>()))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(ArtefactosCodes.EstudianteArtefacto.ESTUDIANTES_REQUERIDOS)));
    }

    @Test
    void debeRechazar_cuandoUnEstudianteNoEsUuid() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var estudiantes = List.of(UtilUUID.generarNuevoUUID().toString(), "no-es-uuid");

        // Act & Assert
        assertThatThrownBy(() -> AsignarEstudiantesArtefactoCommand.crear(artefacto, asesor, estudiantes))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> {
                            assertThat(error.campo()).isEqualTo(ArtefactosFields.EstudianteArtefacto.ESTUDIANTES);
                            assertThat(error.codigoError())
                                    .isEqualTo(ArtefactosCodes.EstudianteArtefacto.ESTUDIANTE_ID_REQUERIDO);
                        }));
    }
}
