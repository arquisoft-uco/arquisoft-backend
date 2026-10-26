package com.arquisoft.artefactos.application.versionartefacto.command.validator.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoNoEncontradoException;
import com.arquisoft.artefactos.domain.artefacto.exception.EstudianteNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.exception.AsesorNoVigenteException;
import com.arquisoft.artefactos.domain.versionartefacto.AnalisisArchivoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.CargaPendienteDomain;
import com.arquisoft.artefactos.domain.versionartefacto.GeneracionVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.exception.CargaModificadaException;
import com.arquisoft.artefactos.domain.versionartefacto.exception.CargaNoCorrespondeException;
import com.arquisoft.artefactos.domain.versionartefacto.exception.CargaPendienteNoEncontradaException;
import com.arquisoft.artefactos.domain.versionartefacto.exception.TamanoArchivoExcedidoException;
import com.arquisoft.artefactos.domain.versionartefacto.exception.TipoArchivoNoCoincideException;
import com.arquisoft.artefactos.domain.versionartefacto.model.CargaAnalizada;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoNoEncontradaException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GenerarVersionArtefactoValidatorImplTest {

    private static final long MAXIMO = 5_242_880L;
    private static final String TIPO = "application/pdf";
    private static final String ETAG = "etag-1";

    private final GenerarVersionArtefactoValidatorImpl validator = new GenerarVersionArtefactoValidatorImpl();

    private GeneracionVersionArtefactoDomain generacion;
    private ArtefactoDomain artefacto;
    private AsesorDomain asesor;
    private List<VersionRepositorioArtefactoDomain> versiones;
    private CargaPendienteDomain carga;
    private AnalisisArchivoDomain analisis;

    @BeforeEach
    void setUp() {
        generacion = GeneracionVersionArtefactoDomain.crear(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var asesorId = UtilUUID.generarNuevoUUID();
        var versionId = UtilUUID.generarNuevoUUID();
        artefacto = ArtefactoDomain.reconstruir(generacion.getArtefacto(), UtilUUID.generarNuevoUUID(),
                versionId, asesorId);
        asesor = AsesorDomain.reconstruir(asesorId, "20161020123", "Ana Perez", "ana@uco.edu.co",
                Instant.now(), null);
        versiones = List.of(VersionRepositorioArtefactoDomain.reconstruir(versionId, "Formato A", 1,
                UtilUUID.generarNuevoUUID(), "Repo", new DatosArchivo("a.pdf", TIPO, 10L)));
        carga = CargaPendienteDomain.reconstruir(generacion.getCarga(), generacion.getArtefacto(),
                generacion.getSolicitante(), "informe.pdf", TIPO, 2048L, ETAG);
        analisis = AnalisisArchivoDomain.reconstruir(TIPO, ETAG);
    }

    private void validar(ArtefactoDomain art, boolean vinculado, AsesorDomain ases,
                         List<VersionRepositorioArtefactoDomain> vers, CargaPendienteDomain car,
                         AnalisisArchivoDomain ana, long maximo) {
        validator.validar(generacion, art, vinculado, ases, vers, new CargaAnalizada(car, ana), maximo);
    }

    @Test
    void debePasar_cuandoTodoValido() {
        // Act & Assert
        assertThatCode(() -> validar(artefacto, true, asesor, versiones, carga, analisis, MAXIMO))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarArtefactoNoEncontrado_antesQueLasDemas() {
        // Act & Assert
        assertThatThrownBy(() -> validar(ArtefactoDomain.VACIO, false, AsesorDomain.VACIO, List.of(),
                CargaPendienteDomain.VACIO, AnalisisArchivoDomain.VACIO, 1L))
                .isInstanceOf(ArtefactoNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoPropietario_antesQueElAsesor() {
        // Act & Assert
        assertThatThrownBy(() -> validar(artefacto, false, AsesorDomain.VACIO, List.of(),
                CargaPendienteDomain.VACIO, AnalisisArchivoDomain.VACIO, 1L))
                .isInstanceOf(EstudianteNoPropietarioArtefactoException.class);
    }

    @Test
    void debeLanzarAsesorNoVigente_cuandoDadoDeBaja() {
        // Arrange
        var eliminado = AsesorDomain.reconstruir(artefacto.getAsesor(), "20161020123", "Ana Perez",
                "ana@uco.edu.co", Instant.now(), Instant.now());

        // Act & Assert
        assertThatThrownBy(() -> validar(artefacto, true, eliminado, List.of(),
                CargaPendienteDomain.VACIO, AnalisisArchivoDomain.VACIO, 1L))
                .isInstanceOf(AsesorNoVigenteException.class);
    }

    @Test
    void debeLanzarVersionRepositorioNoEncontrada_antesQueLaCarga() {
        // Act & Assert
        assertThatThrownBy(() -> validar(artefacto, true, asesor, List.of(),
                CargaPendienteDomain.VACIO, AnalisisArchivoDomain.VACIO, 1L))
                .isInstanceOf(VersionRepositorioArtefactoNoEncontradaException.class);
    }

    @Test
    void debeLanzarCargaNoEncontrada() {
        // Act & Assert
        assertThatThrownBy(() -> validar(artefacto, true, asesor, versiones,
                CargaPendienteDomain.VACIO, AnalisisArchivoDomain.VACIO, 1L))
                .isInstanceOf(CargaPendienteNoEncontradaException.class);
    }

    @Test
    void debeLanzarCargaNoCorresponde_cuandoEsDeOtroSolicitante() {
        // Arrange
        var ajena = CargaPendienteDomain.reconstruir(generacion.getCarga(), generacion.getArtefacto(),
                UtilUUID.generarNuevoUUID(), "informe.pdf", TIPO, 2048L, ETAG);

        // Act & Assert
        assertThatThrownBy(() -> validar(artefacto, true, asesor, versiones, ajena, analisis, MAXIMO))
                .isInstanceOf(CargaNoCorrespondeException.class);
    }

    @Test
    void debeLanzarCargaModificada_antesQueTamanoYTipo() {
        // Arrange
        var otroEtag = AnalisisArchivoDomain.reconstruir("image/png", "otro");

        // Act & Assert
        assertThatThrownBy(() -> validar(artefacto, true, asesor, versiones, carga, otroEtag, 1L))
                .isInstanceOf(CargaModificadaException.class);
    }

    @Test
    void debeLanzarTamanoExcedido_antesQueElTipo() {
        // Arrange
        var otroTipo = AnalisisArchivoDomain.reconstruir("image/png", ETAG);

        // Act & Assert
        assertThatThrownBy(() -> validar(artefacto, true, asesor, versiones, carga, otroTipo, 1024L))
                .isInstanceOf(TamanoArchivoExcedidoException.class);
    }

    @Test
    void debeLanzarTipoNoCoincide() {
        // Arrange
        var otroTipo = AnalisisArchivoDomain.reconstruir("image/png", ETAG);

        // Act & Assert
        assertThatThrownBy(() -> validar(artefacto, true, asesor, versiones, carga, otroTipo, MAXIMO))
                .isInstanceOf(TipoArchivoNoCoincideException.class);
    }
}
