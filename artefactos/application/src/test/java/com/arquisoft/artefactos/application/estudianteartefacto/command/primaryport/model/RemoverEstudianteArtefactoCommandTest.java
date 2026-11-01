package com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class RemoverEstudianteArtefactoCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var command = RemoverEstudianteArtefactoCommand.crear(artefacto, asesor, estudiante);

        // Assert
        assertThat(command.artefacto()).isEqualTo(artefacto);
        assertThat(command.asesor()).isEqualTo(asesor);
        assertThat(command.estudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeAcumularErrores_cuandoFaltanArtefactoAsesorYEstudiante() {
        // Act & Assert
        assertThatThrownBy(() -> RemoverEstudianteArtefactoCommand.crear(null, null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(ValidationError::campo, ValidationError::codigoError)
                        .containsExactlyInAnyOrder(
                                tuple(ArtefactosFields.EstudianteArtefacto.ARTEFACTO,
                                        ArtefactosCodes.EstudianteArtefacto.ARTEFACTO_ID_REQUERIDO),
                                tuple(ArtefactosFields.EstudianteArtefacto.ASESOR,
                                        ArtefactosCodes.EstudianteArtefacto.ASESOR_REQUERIDO),
                                tuple(ArtefactosFields.EstudianteArtefacto.ESTUDIANTE,
                                        ArtefactosCodes.EstudianteArtefacto.ESTUDIANTE_REQUERIDO)));
    }
}
