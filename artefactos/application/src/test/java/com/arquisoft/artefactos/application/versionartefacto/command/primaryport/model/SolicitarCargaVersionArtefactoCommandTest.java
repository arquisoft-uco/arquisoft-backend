package com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitarCargaVersionArtefactoCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();

        // Act
        var command = SolicitarCargaVersionArtefactoCommand.crear(
                artefacto, solicitante, "  informe.pdf ", " application/pdf ", 1024L);

        // Assert
        assertThat(command.artefacto()).isEqualTo(artefacto);
        assertThat(command.solicitante()).isEqualTo(solicitante);
        assertThat(command.nombreArchivo()).isEqualTo("informe.pdf");
        assertThat(command.tipoContenido()).isEqualTo("application/pdf");
        assertThat(command.tamanoBytes()).isEqualTo(1024L);
    }

    @Test
    void debeAcumularErrores400_cuandoDatosInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> SolicitarCargaVersionArtefactoCommand.crear(null, null, " ", " ", 0L))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting("campo").contains(
                                ArtefactosFields.VersionArtefacto.ARTEFACTO,
                                ArtefactosFields.VersionArtefacto.SOLICITANTE,
                                ArtefactosFields.VersionArtefacto.NOMBRE_ARCHIVO,
                                ArtefactosFields.VersionArtefacto.TIPO_CONTENIDO,
                                ArtefactosFields.VersionArtefacto.TAMANO_BYTES));
    }
}
