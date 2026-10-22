package com.arquisoft.artefactos.domain.versionrepositorioartefacto;

import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VersionRepositorioArtefactoDomainTest {

    private static final String TIPO_PDF = "application/pdf";

    private static DatosArchivo archivoValido() {
        return new DatosArchivo("plantilla.pdf", TIPO_PDF, 2048L);
    }

    @Test
    void debeCrear_cuandoTodoValido() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var repositorio = UtilUUID.generarNuevoUUID();

        // Act
        var version = VersionRepositorioArtefactoDomain.crear(
                id, "  Plantilla de tesis  ", 3, repositorio, " Repositorio base ", archivoValido());

        // Assert
        assertThat(version.getId()).isEqualTo(id);
        assertThat(version.getNombreDocumento()).isEqualTo("Plantilla de tesis");
        assertThat(version.getVersionDocumento()).isEqualTo(3);
        assertThat(version.getRepositorioArtefacto()).isEqualTo(repositorio);
        assertThat(version.getNombreRepositorio()).isEqualTo("Repositorio base");
        assertThat(version.getNombreArchivo()).isEqualTo("plantilla.pdf");
        assertThat(version.getTipoContenido()).isEqualTo(TIPO_PDF);
        assertThat(version.getTamanoBytes()).isEqualTo(2048L);
    }

    @Test
    void debeAcumularFieldErrors_cuandoFaltanTodosLosDatos() {
        // Act & Assert
        assertThatThrownBy(() -> VersionRepositorioArtefactoDomain.crear(
                null, " ", 0, null, " ", new DatosArchivo(" ", " ", 0L)))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(8);
                    assertThat(errores).extracting(error -> error.campo()).containsExactlyInAnyOrder(
                            ArtefactosFields.VersionRepositorioArtefacto.ID,
                            ArtefactosFields.VersionRepositorioArtefacto.NOMBRE_DOCUMENTO,
                            ArtefactosFields.VersionRepositorioArtefacto.VERSION_DOCUMENTO,
                            ArtefactosFields.VersionRepositorioArtefacto.REPOSITORIO_ARTEFACTO,
                            ArtefactosFields.VersionRepositorioArtefacto.NOMBRE_REPOSITORIO,
                            ArtefactosFields.VersionRepositorioArtefacto.NOMBRE_ARCHIVO,
                            ArtefactosFields.VersionRepositorioArtefacto.TIPO_CONTENIDO,
                            ArtefactosFields.VersionRepositorioArtefacto.TAMANO_BYTES);
                });
    }

    @Test
    void debeAceptar_limitesMaximos() {
        // Arrange
        var archivo = new DatosArchivo("a".repeat(255), "t".repeat(100), 1L);

        // Act & Assert
        assertThatCode(() -> VersionRepositorioArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), "d".repeat(100), 1, UtilUUID.generarNuevoUUID(), "r".repeat(100),
                archivo))
                .doesNotThrowAnyException();
    }

    @Test
    void debeRechazar_nombreDocumentoDe101() {
        // Act & Assert
        assertThatThrownBy(() -> VersionRepositorioArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), "d".repeat(101), 1, UtilUUID.generarNuevoUUID(), "Repo",
                archivoValido()))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(ArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_DOCUMENTO_DEMASIADO_LARGO)));
    }

    @Test
    void debeAcumularFieldErrors_cuandoNombreRepositorioYTipoContenidoSuperanElMaximo() {
        // Arrange
        var archivo = new DatosArchivo("plantilla.pdf", "t".repeat(101), 10L);

        // Act & Assert
        assertThatThrownBy(() -> VersionRepositorioArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), "Doc", 1, UtilUUID.generarNuevoUUID(), "r".repeat(101), archivo))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                ArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_REPOSITORIO_DEMASIADO_LARGO,
                                ArtefactosCodes.VersionRepositorioArtefacto.TIPO_CONTENIDO_DEMASIADO_LARGO));
    }

    @Test
    void debeRechazar_nombreArchivoDe256() {
        // Arrange
        var archivo = new DatosArchivo("a".repeat(256), TIPO_PDF, 10L);

        // Act & Assert
        assertThatThrownBy(() -> VersionRepositorioArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), "Doc", 1, UtilUUID.generarNuevoUUID(), "Repo", archivo))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(ArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_ARCHIVO_DEMASIADO_LARGO)));
    }

    @Test
    void debeRechazar_versionDocumentoMenorAUno() {
        // Act & Assert
        assertThatThrownBy(() -> VersionRepositorioArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), "Doc", 0, UtilUUID.generarNuevoUUID(), "Repo", archivoValido()))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(ArtefactosCodes.VersionRepositorioArtefacto.VERSION_DOCUMENTO_INVALIDA)));
    }

    @Test
    void debeRechazar_tamanoBytesNoPositivo() {
        // Arrange
        var archivo = new DatosArchivo("plantilla.pdf", TIPO_PDF, 0L);

        // Act & Assert
        assertThatThrownBy(() -> VersionRepositorioArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), "Doc", 1, UtilUUID.generarNuevoUUID(), "Repo", archivo))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(ArtefactosCodes.VersionRepositorioArtefacto.TAMANO_BYTES_INVALIDO)));
    }
}
