package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.validator.impl;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.AnalisisArchivoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.CargaPendienteDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SubidaVersionRepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.CargaModificadaException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.TipoArchivoNoCoincideException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SubirVersionRepositorioArtefactoContenidoValidatorImplTest {

    private static final String TIPO = "application/pdf";
    private static final String ETAG = "etag-1";
    private static final String HASH = "h".repeat(64);

    private final SubirVersionRepositorioArtefactoContenidoValidatorImpl validator =
            new SubirVersionRepositorioArtefactoContenidoValidatorImpl();

    private SubidaVersionRepositorioArtefactoDomain subida;
    private CargaPendienteDomain carga;

    @BeforeEach
    void setUp() {
        subida = SubidaVersionRepositorioArtefactoDomain.crear(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Formato A", false);
        carga = CargaPendienteDomain.reconstruir(subida.getCarga(), subida.getRepositorio(),
                subida.getSolicitante(), "formato.pdf", TIPO, 2048L, ETAG);
    }

    @Test
    void debePasar_cuandoEtagYTipoCoinciden() {
        // Arrange
        var analisis = AnalisisArchivoDomain.reconstruir(TIPO, HASH, ETAG);

        // Act & Assert
        assertThatCode(() -> validator.validar(subida, carga, analisis)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarCargaModificada_antesQueElTipo() {
        // Arrange
        var analisis = AnalisisArchivoDomain.reconstruir("application/zip", HASH, "otro-etag");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(subida, carga, analisis))
                .isInstanceOf(CargaModificadaException.class);
    }

    @Test
    void debeLanzarTipoNoCoincide_cuandoElEtagCoincide() {
        // Arrange
        var analisis = AnalisisArchivoDomain.reconstruir("application/zip", HASH, ETAG);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(subida, carga, analisis))
                .isInstanceOf(TipoArchivoNoCoincideException.class);
    }

    @Test
    void debeLanzarCargaModificada_cuandoElAnalisisEstaVacio() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(subida, carga, AnalisisArchivoDomain.VACIO))
                .isInstanceOf(CargaModificadaException.class);
    }
}
