package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.AnalisisArchivoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.exception.TipoArchivoNoCoincideException;
import com.arquisoft.artefactos.domain.versionartefacto.model.CoincidenciaTipoArchivo;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TipoArchivoCoincideRuleImplTest {

    private final TipoArchivoCoincideRuleImpl regla = new TipoArchivoCoincideRuleImpl();

    private static AnalisisArchivoDomain analisis(String tipoDetectado) {
        return AnalisisArchivoDomain.reconstruir(tipoDetectado, "etag");
    }

    @Test
    void debePasar_cuandoMarkdownSeDetectaComoTextoPlano() {
        // Arrange
        var coincidencia = new CoincidenciaTipoArchivo("leeme.md", "text/markdown", analisis("text/plain"));

        // Act & Assert
        assertThatCode(() -> regla.validar(coincidencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoContentTypeAlmacenadoDifiere() {
        // Arrange
        var coincidencia = new CoincidenciaTipoArchivo("a.pdf", "image/png", analisis("application/pdf"));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(coincidencia))
                .isInstanceOf(TipoArchivoNoCoincideException.class)
                .extracting("codigoError")
                .isEqualTo(ArtefactosCodes.VersionArtefacto.TIPO_ARCHIVO_NO_COINCIDE);
    }

    @Test
    void debeLanzar_cuandoDetectadoFueraDelConjunto() {
        // Arrange
        var coincidencia = new CoincidenciaTipoArchivo("foto.png", "image/png", analisis("application/zip"));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(coincidencia)).isInstanceOf(TipoArchivoNoCoincideException.class);
    }
}
