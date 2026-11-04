package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitarCargaVersionRepositorioArtefactoCommandTest {

    @Test
    void debeCrearRecortandoTextos_cuandoValido() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();

        // Act
        var command = SolicitarCargaVersionRepositorioArtefactoCommand.crear(
                repositorio, solicitante, "  formato.pdf ", " application/pdf ", 2048L);

        // Assert
        assertThat(command.repositorio()).isEqualTo(repositorio);
        assertThat(command.solicitante()).isEqualTo(solicitante);
        assertThat(command.nombreArchivo()).isEqualTo("formato.pdf");
        assertThat(command.tipoContenido()).isEqualTo("application/pdf");
        assertThat(command.tamanoBytes()).isEqualTo(2048L);
    }

    @Test
    void debeAcumularErrores_cuandoFaltanDatos() {
        // Act & Assert
        assertThatThrownBy(() -> SolicitarCargaVersionRepositorioArtefactoCommand.crear(
                null, null, " ", " ", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting("campo")
                        .contains(
                                RepositorioArtefactosFields.VersionRepositorioArtefacto.REPOSITORIO,
                                RepositorioArtefactosFields.VersionRepositorioArtefacto.SOLICITANTE,
                                RepositorioArtefactosFields.VersionRepositorioArtefacto.NOMBRE_ARCHIVO,
                                RepositorioArtefactosFields.VersionRepositorioArtefacto.TIPO_CONTENIDO,
                                RepositorioArtefactosFields.VersionRepositorioArtefacto.TAMANO_BYTES));
    }

    @Test
    void debeRechazar_cuandoTamanoNoEsPositivo() {
        // Act & Assert
        assertThatThrownBy(() -> SolicitarCargaVersionRepositorioArtefactoCommand.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "formato.pdf", "application/pdf", 0L))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .contains(RepositorioArtefactosCodes.VersionRepositorioArtefacto.TAMANO_BYTES_INVALIDO));
    }

    @Test
    void debeRechazar_cuandoLongitudesSuperanElMaximo() {
        // Act & Assert
        assertThatThrownBy(() -> SolicitarCargaVersionRepositorioArtefactoCommand.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "a".repeat(256), "t".repeat(101), 10L))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .contains(
                                RepositorioArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_ARCHIVO_DEMASIADO_LARGO,
                                RepositorioArtefactosCodes.VersionRepositorioArtefacto
                                        .TIPO_CONTENIDO_DEMASIADO_LARGO));
    }
}
