package com.arquisoft.artefactos.application.artefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarVersionRepositorioArtefactoCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID();

        // Act
        var command = CambiarVersionRepositorioArtefactoCommand.crear(artefacto, asesor, version.toString());

        // Assert
        assertThat(command.artefacto()).isEqualTo(artefacto);
        assertThat(command.asesor()).isEqualTo(asesor);
        assertThat(command.versionRepositorioArtefacto()).isEqualTo(version);
    }

    @Test
    void debeLanzar400_cuandoVersionNoEsUUID() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> CambiarVersionRepositorioArtefactoCommand.crear(artefacto, asesor, "no-es-uuid"))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting(e -> e.campo())
                            .containsExactly(ArtefactosFields.Artefacto.VERSION_REPOSITORIO_ARTEFACTO);
                });
    }

    @Test
    void debeAcumularErrores_cuandoFaltanArtefactoYAsesorYVersion() {
        // Act & Assert
        assertThatThrownBy(() -> CambiarVersionRepositorioArtefactoCommand.crear(null, null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting(e -> e.campo()).containsExactlyInAnyOrder(
                            ArtefactosFields.Artefacto.ID,
                            ArtefactosFields.Artefacto.ASESOR,
                            ArtefactosFields.Artefacto.VERSION_REPOSITORIO_ARTEFACTO);
                    assertThat(errores).extracting(e -> e.codigoError()).containsExactlyInAnyOrder(
                            ArtefactosCodes.Artefacto.ID_REQUERIDO,
                            ArtefactosCodes.Artefacto.ASESOR_REQUERIDO,
                            ArtefactosCodes.Artefacto.VERSION_REPOSITORIO_ARTEFACTO_REQUERIDA);
                });
    }
}
