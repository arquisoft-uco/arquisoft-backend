package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SubirVersionRepositorioArtefactoCommandTest {

    @Test
    void debeCrearRecortandoTextos_cuandoValido() {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();
        var repositorio = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();

        // Act
        var command = SubirVersionRepositorioArtefactoCommand.crear(
                carga.toString(), repositorio, solicitante, "  Formato A ", true);

        // Assert
        assertThat(command.carga()).isEqualTo(carga);
        assertThat(command.repositorio()).isEqualTo(repositorio);
        assertThat(command.solicitante()).isEqualTo(solicitante);
        assertThat(command.nombreDocumento()).isEqualTo("Formato A");
        assertThat(command.nuevoDocumento()).isTrue();
    }

    @Test
    void debeAcumularErrores_cuandoFaltanDatos() {
        // Act & Assert
        assertThatThrownBy(() -> SubirVersionRepositorioArtefactoCommand.crear(
                "no-es-uuid", null, null, " ", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting("campo")
                        .contains(
                                RepositorioArtefactosFields.VersionRepositorioArtefacto.CARGA,
                                RepositorioArtefactosFields.VersionRepositorioArtefacto.REPOSITORIO,
                                RepositorioArtefactosFields.VersionRepositorioArtefacto.SOLICITANTE,
                                RepositorioArtefactosFields.VersionRepositorioArtefacto.NOMBRE_DOCUMENTO,
                                RepositorioArtefactosFields.VersionRepositorioArtefacto.NUEVO_DOCUMENTO));
    }

    @Test
    void debeRechazar_cuandoNuevoDocumentoEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> SubirVersionRepositorioArtefactoCommand.crear(
                UtilUUID.generarNuevoUUID().toString(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "Formato", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .contains(RepositorioArtefactosCodes.VersionRepositorioArtefacto.NUEVO_DOCUMENTO_REQUERIDO));
    }

    @Test
    void debeRechazar_cuandoNombreDocumentoSuperaElMaximo() {
        // Act & Assert
        assertThatThrownBy(() -> SubirVersionRepositorioArtefactoCommand.crear(
                UtilUUID.generarNuevoUUID().toString(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "n".repeat(101), false))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .contains(RepositorioArtefactosCodes.VersionRepositorioArtefacto
                                .NOMBRE_DOCUMENTO_DEMASIADO_LARGO));
    }
}
