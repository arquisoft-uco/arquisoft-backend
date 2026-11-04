package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto;

import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitudCargaVersionRepositorioArtefactoDomainTest {

    private static final String TIPO_PDF = "application/pdf";
    private static final String TIPO_DOCX =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    @Test
    void debeCrear_conCargaGenerada() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();

        // Act
        var solicitud = SolicitudCargaVersionRepositorioArtefactoDomain.crear(
                repositorio, solicitante, "  plantilla.pdf  ", TIPO_PDF, 1024L);

        // Assert
        assertThat(solicitud.getCarga()).isNotNull();
        assertThat(solicitud.getRepositorio()).isEqualTo(repositorio);
        assertThat(solicitud.getSolicitante()).isEqualTo(solicitante);
        assertThat(solicitud.getNombreArchivo()).isEqualTo("plantilla.pdf");
        assertThat(solicitud.getTipoContenido()).isEqualTo(TIPO_PDF);
        assertThat(solicitud.getTamanoBytes()).isEqualTo(1024L);
    }

    @Test
    void debeAcumularFieldErrors_cuandoNombreVacioTipoVacioYTamanoNulo() {
        // Act & Assert
        assertThatThrownBy(() -> SolicitudCargaVersionRepositorioArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "  ", " ", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("campo").contains(
                            RepositorioArtefactosFields.VersionRepositorioArtefacto.NOMBRE_ARCHIVO,
                            RepositorioArtefactosFields.VersionRepositorioArtefacto.TIPO_CONTENIDO,
                            RepositorioArtefactosFields.VersionRepositorioArtefacto.TAMANO_BYTES);
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"guia.md", "viejo.doc", "viejo.xls", "viejo.ppt", "macro.docm", "macro.xlsm",
            "macro.pptm", "sin-extension", "foto.jpg"})
    void debeRechazar_cuandoExtensionFueraDeListaBlanca(String nombre) {
        // Act & Assert
        assertThatThrownBy(() -> SolicitudCargaVersionRepositorioArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), nombre, TIPO_PDF, 10L))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .contains(RepositorioArtefactosCodes.VersionRepositorioArtefacto.TIPO_NO_PERMITIDO));
    }

    @Test
    void debeRechazar_cuandoTipoNoCorrespondeALaExtension() {
        // Act & Assert
        assertThatThrownBy(() -> SolicitudCargaVersionRepositorioArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "plantilla.docx", TIPO_PDF, 10L))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .contains(RepositorioArtefactosCodes.VersionRepositorioArtefacto
                                .TIPO_CONTENIDO_NO_COINCIDE_EXTENSION));
    }

    @ParameterizedTest
    @ValueSource(strings = {"../etc/passwd.pdf", "a/b.pdf", "a\\b.pdf", "a\u0000b.pdf", "a\nb.pdf"})
    void debeRechazar_cuandoNombreTieneSeparadorDeRutaOControl(String nombre) {
        // Act & Assert
        assertThatThrownBy(() -> SolicitudCargaVersionRepositorioArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), nombre, TIPO_PDF, 10L))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .contains(RepositorioArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_ARCHIVO_INVALIDO));
    }

    @Test
    void debeAceptar_extensionEnMayusculas() {
        // Act
        var solicitud = SolicitudCargaVersionRepositorioArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "PLANTILLA.DOCX", TIPO_DOCX, 10L);

        // Assert
        assertThat(solicitud.getNombreArchivo()).isEqualTo("PLANTILLA.DOCX");
    }

    @Test
    void debeRechazar_cuandoNombreArchivoSuperaElMaximo() {
        // Arrange
        var nombre = "a".repeat(252) + ".pdf";

        // Act & Assert
        assertThatThrownBy(() -> SolicitudCargaVersionRepositorioArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), nombre, TIPO_PDF, 10L))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .contains(RepositorioArtefactosCodes.VersionRepositorioArtefacto
                                .NOMBRE_ARCHIVO_DEMASIADO_LARGO));
    }

    @Test
    void debeRechazar_cuandoTamanoNoEsPositivo() {
        // Act & Assert
        assertThatThrownBy(() -> SolicitudCargaVersionRepositorioArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "plantilla.pdf", TIPO_PDF, 0L))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .contains(RepositorioArtefactosCodes.VersionRepositorioArtefacto.TAMANO_BYTES_INVALIDO));
    }
}
