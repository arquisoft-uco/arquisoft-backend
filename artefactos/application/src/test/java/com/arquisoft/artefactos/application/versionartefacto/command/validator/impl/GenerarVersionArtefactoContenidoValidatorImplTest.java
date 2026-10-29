package com.arquisoft.artefactos.application.versionartefacto.command.validator.impl;

import com.arquisoft.artefactos.domain.versionartefacto.AnalisisArchivoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.CargaPendienteDomain;
import com.arquisoft.artefactos.domain.versionartefacto.GeneracionVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.exception.CargaModificadaException;
import com.arquisoft.artefactos.domain.versionartefacto.exception.TipoArchivoNoCoincideException;
import com.arquisoft.artefactos.domain.versionartefacto.model.CargaAnalizada;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GenerarVersionArtefactoContenidoValidatorImplTest {

    private static final String TIPO = "application/pdf";
    private static final String ETAG = "etag-1";

    private final GenerarVersionArtefactoContenidoValidatorImpl validator =
            new GenerarVersionArtefactoContenidoValidatorImpl();

    private GeneracionVersionArtefactoDomain generacion;
    private CargaPendienteDomain carga;

    @BeforeEach
    void setUp() {
        generacion = GeneracionVersionArtefactoDomain.crear(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        carga = CargaPendienteDomain.reconstruir(generacion.getCarga(), generacion.getArtefacto(),
                generacion.getSolicitante(), "informe.pdf", TIPO, 2048L, ETAG);
    }

    @Test
    void debePasar_cuandoEtagYTipoCoinciden() {
        // Arrange
        var analisis = AnalisisArchivoDomain.reconstruir(TIPO, ETAG);

        // Act & Assert
        assertThatCode(() -> validator.validar(generacion, new CargaAnalizada(carga, analisis)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarCargaModificada_antesQueElTipo() {
        // Arrange
        var otroEtag = AnalisisArchivoDomain.reconstruir("image/png", "otro");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(generacion, new CargaAnalizada(carga, otroEtag)))
                .isInstanceOf(CargaModificadaException.class);
    }

    @Test
    void debeLanzarTipoNoCoincide_cuandoElEtagCoincide() {
        // Arrange
        var otroTipo = AnalisisArchivoDomain.reconstruir("image/png", ETAG);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(generacion, new CargaAnalizada(carga, otroTipo)))
                .isInstanceOf(TipoArchivoNoCoincideException.class);
    }
}
