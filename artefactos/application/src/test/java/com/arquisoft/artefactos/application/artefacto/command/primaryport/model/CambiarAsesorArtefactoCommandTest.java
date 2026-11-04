package com.arquisoft.artefactos.application.artefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarAsesorArtefactoCommandTest {

    @Test
    void debeCrearCommand_cuandoLosDatosSonValidos() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var command = CambiarAsesorArtefactoCommand.crear(artefacto, asesor.toString());

        // Assert
        assertThat(command.artefacto()).isEqualTo(artefacto);
        assertThat(command.asesor()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularErrores_cuandoArtefactoEsNuloYAsesorEsInvalido() {
        // Act & Assert
        assertThatThrownBy(() -> CambiarAsesorArtefactoCommand.crear(null, "no-es-uuid"))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactlyInAnyOrder(ArtefactosFields.Artefacto.ID, ArtefactosFields.Artefacto.ASESOR));
    }

    @Test
    void debeFallar_cuandoAsesorEsNulo() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> CambiarAsesorArtefactoCommand.crear(artefacto, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactly(ArtefactosFields.Artefacto.ASESOR));
    }
}
