package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.AnalisisArchivoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.TipoArchivoNoCoincideException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.CoincidenciaTipoArchivo;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TipoArchivoCoincideRuleImplTest {

    private static final String TIPO_DOCX =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    private static final String TIPO_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final TipoArchivoCoincideRuleImpl regla = new TipoArchivoCoincideRuleImpl();

    private static AnalisisArchivoDomain analisis(String tipoDetectado) {
        return AnalisisArchivoDomain.reconstruir(tipoDetectado, "h".repeat(64), "etag");
    }

    @Test
    void debePasar_cuandoCoincidenLosTres() {
        // Arrange
        var coincidencia = new CoincidenciaTipoArchivo("formato.docx", TIPO_DOCX, analisis(TIPO_DOCX));

        // Act & Assert
        assertThatCode(() -> regla.validar(coincidencia)).doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoTikaAgregaParametros() {
        // Arrange
        var coincidencia = new CoincidenciaTipoArchivo("a.pdf", "application/pdf",
                analisis("application/pdf; version=1.4"));

        // Act & Assert
        assertThatCode(() -> regla.validar(coincidencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoExcelRenombradoAJpg() {
        // Arrange
        var coincidencia = new CoincidenciaTipoArchivo("foto.jpg", "image/jpeg", analisis(TIPO_XLSX));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(coincidencia))
                .isInstanceOf(TipoArchivoNoCoincideException.class)
                .extracting("codigoError")
                .isEqualTo(RepositorioArtefactosCodes.VersionRepositorioArtefacto.TIPO_ARCHIVO_NO_COINCIDE);
    }

    @Test
    void debeLanzar_cuandoPdfConContenidoDeOtroTipo() {
        // Arrange
        var coincidencia = new CoincidenciaTipoArchivo("a.pdf", "application/pdf", analisis(TIPO_XLSX));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(coincidencia)).isInstanceOf(TipoArchivoNoCoincideException.class);
    }

    @Test
    void debeLanzar_cuandoTipoAlmacenadoDistinto() {
        // Arrange
        var coincidencia = new CoincidenciaTipoArchivo("formato.docx", "application/pdf", analisis(TIPO_DOCX));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(coincidencia)).isInstanceOf(TipoArchivoNoCoincideException.class);
    }

    @ParameterizedTest
    @CsvSource({
            "formato.docx,application/vnd.openxmlformats-officedocument.wordprocessingml.document,"
                    + "application/vnd.ms-word.document.macroenabled.12",
            "formato.xlsx,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,"
                    + "application/vnd.ms-excel.sheet.macroenabled.12",
            "formato.pptx,application/vnd.openxmlformats-officedocument.presentationml.presentation,"
                    + "application/vnd.ms-powerpoint.presentation.macroenabled.12"
    })
    void debeLanzar_cuandoOoxmlEsEnRealidadConMacros(String nombre, String tipoAlmacenado, String tipoDetectado) {
        // Arrange
        var coincidencia = new CoincidenciaTipoArchivo(nombre, tipoAlmacenado, analisis(tipoDetectado));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(coincidencia))
                .isInstanceOf(TipoArchivoNoCoincideException.class)
                .extracting("codigoError")
                .isEqualTo(RepositorioArtefactosCodes.VersionRepositorioArtefacto.TIPO_ARCHIVO_NO_COINCIDE);
    }

    @Test
    void debeLanzar_cuandoElAnalisisEstaVacio() {
        // Arrange
        var coincidencia = new CoincidenciaTipoArchivo("a.pdf", "application/pdf", AnalisisArchivoDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(coincidencia)).isInstanceOf(TipoArchivoNoCoincideException.class);
    }

    @Test
    void debeLanzar_cuandoLaExtensionNoEstaPermitida() {
        // Arrange
        var coincidencia = new CoincidenciaTipoArchivo("a.zip", "application/zip", analisis("application/zip"));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(coincidencia)).isInstanceOf(TipoArchivoNoCoincideException.class);
    }
}
