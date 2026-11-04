package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.model;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.model.AgregarVersionRepositorioArtefactoCommand.DatosArchivo;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarVersionRepositorioArtefactoCommandTest {

    private static final String TIPO_PDF = "application/pdf";

    @Test
    void debeCrear_cuandoValido() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var repositorio = UtilUUID.generarNuevoUUID();
        var archivo = new DatosArchivo(" plantilla.pdf ", TIPO_PDF, 2048L);

        // Act
        var command = AgregarVersionRepositorioArtefactoCommand.crear(
                id.toString(), 2, " Plantilla ", repositorio.toString(), " Repositorio ", archivo);

        // Assert
        assertThat(command.id()).isEqualTo(id);
        assertThat(command.nombreDocumento()).isEqualTo("Plantilla");
        assertThat(command.versionDocumento()).isEqualTo(2);
        assertThat(command.repositorioArtefacto()).isEqualTo(repositorio);
        assertThat(command.nombreRepositorio()).isEqualTo("Repositorio");
        assertThat(command.archivo().nombreArchivo()).isEqualTo("plantilla.pdf");
        assertThat(command.archivo().tipoContenido()).isEqualTo(TIPO_PDF);
        assertThat(command.archivo().tamanoBytes()).isEqualTo(2048L);
    }

    @Test
    void debeCrear_cuandoLosTextosEstanEnElLimite() {
        // Arrange
        var archivo = new DatosArchivo("a".repeat(255), "t".repeat(100), 1L);
        var id = UtilUUID.generarNuevoUUID().toString();
        var repositorio = UtilUUID.generarNuevoUUID().toString();

        // Act
        var command = AgregarVersionRepositorioArtefactoCommand.crear(
                id, 1, "d".repeat(100), repositorio, "r".repeat(100), archivo);

        // Assert
        assertThat(command.nombreDocumento()).hasSize(100);
        assertThat(command.nombreRepositorio()).hasSize(100);
        assertThat(command.archivo().nombreArchivo()).hasSize(255);
        assertThat(command.archivo().tipoContenido()).hasSize(100);
        assertThat(command.archivo().tamanoBytes()).isEqualTo(1L);
    }

    @Test
    void debeRechazar_cuandoIdNoEsUuid() {
        // Arrange
        var archivo = new DatosArchivo("plantilla.pdf", TIPO_PDF, 10L);
        var repositorio = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        assertThatThrownBy(() -> AgregarVersionRepositorioArtefactoCommand.crear(
                "no-es-uuid", 1, "Doc", repositorio, "Repo", archivo))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> {
                            assertThat(error.campo()).isEqualTo(ArtefactosFields.VersionRepositorioArtefacto.ID);
                            assertThat(error.codigoError())
                                    .isEqualTo(ArtefactosCodes.VersionRepositorioArtefacto.ID_REQUERIDO);
                        }));
    }

    @Test
    void debeAcumularErrores_cuandoFaltanDatos() {
        // Arrange
        var archivo = new DatosArchivo(null, null, 0L);

        // Act & Assert
        assertThatThrownBy(() -> AgregarVersionRepositorioArtefactoCommand.crear(
                null, 0, null, null, null, archivo))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.campo())
                        .containsExactlyInAnyOrder(
                                ArtefactosFields.VersionRepositorioArtefacto.ID,
                                ArtefactosFields.VersionRepositorioArtefacto.NOMBRE_DOCUMENTO,
                                ArtefactosFields.VersionRepositorioArtefacto.VERSION_DOCUMENTO,
                                ArtefactosFields.VersionRepositorioArtefacto.REPOSITORIO_ARTEFACTO,
                                ArtefactosFields.VersionRepositorioArtefacto.NOMBRE_REPOSITORIO,
                                ArtefactosFields.VersionRepositorioArtefacto.NOMBRE_ARCHIVO,
                                ArtefactosFields.VersionRepositorioArtefacto.TIPO_CONTENIDO,
                                ArtefactosFields.VersionRepositorioArtefacto.TAMANO_BYTES));
    }

    @Test
    void debeRechazar_cuandoTamanoNuloYTextosDemasiadoLargos() {
        // Arrange
        var archivo = new DatosArchivo("a".repeat(256), "t".repeat(101), null);
        var id = UtilUUID.generarNuevoUUID().toString();
        var repositorio = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        assertThatThrownBy(() -> AgregarVersionRepositorioArtefactoCommand.crear(
                id, null, "d".repeat(101), repositorio, "r".repeat(101), archivo))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                ArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_DOCUMENTO_DEMASIADO_LARGO,
                                ArtefactosCodes.VersionRepositorioArtefacto.VERSION_DOCUMENTO_REQUERIDA,
                                ArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_REPOSITORIO_DEMASIADO_LARGO,
                                ArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_ARCHIVO_DEMASIADO_LARGO,
                                ArtefactosCodes.VersionRepositorioArtefacto.TIPO_CONTENIDO_DEMASIADO_LARGO,
                                ArtefactosCodes.VersionRepositorioArtefacto.TAMANO_BYTES_REQUERIDO));
    }
}
