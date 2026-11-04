package com.arquisoft.artefactos.domain.versionartefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitudCargaVersionArtefactoDomainTest {

    private static final String TIPO_PDF = "application/pdf";

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();

        // Act
        var solicitud = SolicitudCargaVersionArtefactoDomain.crear(
                artefacto, solicitante, "  informe.pdf  ", TIPO_PDF, 1024L);

        // Assert
        assertThat(UtilUUID.esPorDefecto(solicitud.getCarga())).isFalse();
        assertThat(solicitud.getArtefacto()).isEqualTo(artefacto);
        assertThat(solicitud.getSolicitante()).isEqualTo(solicitante);
        assertThat(solicitud.getNombreArchivo()).isEqualTo("informe.pdf");
        assertThat(solicitud.getTipoContenido()).isEqualTo(TIPO_PDF);
        assertThat(solicitud.getTamanoBytes()).isEqualTo(1024L);
    }

    @Test
    void debeRechazar_cuandoExtensionNoPermitida() {
        // Act & Assert
        assertThatThrownBy(() -> SolicitudCargaVersionArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "datos.zip", "application/zip", 10L))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .contains(ArtefactosCodes.VersionArtefacto.TIPO_NO_PERMITIDO));
    }

    @Test
    void debeRechazar_cuandoTipoContenidoNoCoincideConExtension() {
        // Act & Assert
        assertThatThrownBy(() -> SolicitudCargaVersionArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "foto.png", TIPO_PDF, 10L))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .contains(ArtefactosCodes.VersionArtefacto.TIPO_CONTENIDO_NO_COINCIDE_EXTENSION));
    }

    @ParameterizedTest
    @ValueSource(strings = {"../etc/passwd.pdf", "a/b.pdf", "a\\b.pdf", "a\u0000b.pdf", "a\nb.pdf"})
    void debeRechazar_cuandoNombreArchivoConSeparador(String nombre) {
        // Act & Assert
        assertThatThrownBy(() -> SolicitudCargaVersionArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), nombre, TIPO_PDF, 10L))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .contains(ArtefactosCodes.VersionArtefacto.NOMBRE_ARCHIVO_INVALIDO));
    }

    @Test
    void debeAcumularFieldErrors_cuandoVariosCamposInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> SolicitudCargaVersionArtefactoDomain.crear(null, null, "  ", " ", 0L))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting("campo").contains(
                                ArtefactosFields.VersionArtefacto.ARTEFACTO,
                                ArtefactosFields.VersionArtefacto.SOLICITANTE,
                                ArtefactosFields.VersionArtefacto.NOMBRE_ARCHIVO,
                                ArtefactosFields.VersionArtefacto.TIPO_CONTENIDO,
                                ArtefactosFields.VersionArtefacto.TAMANO_BYTES));
    }
}
