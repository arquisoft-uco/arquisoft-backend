package com.arquisoft.artefactos.application.versionartefacto.command.validator.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoNoEncontradoException;
import com.arquisoft.artefactos.domain.artefacto.exception.EstudianteNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.exception.AsesorNoVigenteException;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.exception.ArtefactoCerradoException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionVersionAnteriorNoEnProgresoException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.VersionAnteriorSinRevisionException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.versionartefacto.CargaPendienteDomain;
import com.arquisoft.artefactos.domain.versionartefacto.GeneracionVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.exception.CargaNoCorrespondeException;
import com.arquisoft.artefactos.domain.versionartefacto.exception.CargaPendienteNoEncontradaException;
import com.arquisoft.artefactos.domain.versionartefacto.exception.TamanoArchivoExcedidoException;
import com.arquisoft.artefactos.domain.versionartefacto.model.PrecondicionesGeneracionVersion;
import com.arquisoft.artefactos.domain.revisionasesor.model.RevisionVersionAnterior;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoNoEncontradaException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;

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
    private VersionRepositorioArtefactoDomain versionRepositorio;
    private CargaPendienteDomain carga;

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
        versionRepositorio = VersionRepositorioArtefactoDomain.reconstruir(versionId, "Formato A", 1,
                UtilUUID.generarNuevoUUID(), "Repo", new DatosArchivo("a.pdf", TIPO, 10L));
        carga = CargaPendienteDomain.reconstruir(generacion.getCarga(), generacion.getArtefacto(),
                generacion.getSolicitante(), "informe.pdf", TIPO, 2048L, ETAG);
    }

    private void validarPrecondiciones(ArtefactoDomain art, boolean vinculado, AsesorDomain ases,
                                       VersionRepositorioArtefactoDomain ver, CargaPendienteDomain car,
                                       long maximo) {
        validarPrecondiciones(art, vinculado, ases, ver, car, maximo,
                new RevisionVersionAnterior(2, revisionEn(EstadoRevisionAsesor.EN_PROGRESO)));
    }

    private void validarPrecondiciones(ArtefactoDomain art, boolean vinculado, AsesorDomain ases,
                                       VersionRepositorioArtefactoDomain ver, CargaPendienteDomain car,
                                       long maximo, RevisionVersionAnterior anterior) {
        validator.validar(generacion, new PrecondicionesGeneracionVersion(
                art, vinculado, ases, ver, car, maximo, anterior));
    }

    private void validarPolitica(int ultimoNumero, RevisionAsesorDomain revision) {
        validarPrecondiciones(artefacto, true, asesor, versionRepositorio, carga, MAXIMO,
                new RevisionVersionAnterior(ultimoNumero, revision));
    }

    @Test
    void debePasar_cuandoTodoValido() {
        // Act & Assert
        assertThatCode(() -> validarPrecondiciones(artefacto, true, asesor, versionRepositorio, carga, MAXIMO))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarArtefactoNoEncontrado_antesQueLasDemas() {
        // Act & Assert
        assertThatThrownBy(() -> validarPrecondiciones(ArtefactoDomain.VACIO, false, AsesorDomain.VACIO,
                VersionRepositorioArtefactoDomain.VACIO, CargaPendienteDomain.VACIO, 1L))
                .isInstanceOf(ArtefactoNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoPropietario_antesQueElAsesor() {
        // Act & Assert
        assertThatThrownBy(() -> validarPrecondiciones(artefacto, false, AsesorDomain.VACIO,
                VersionRepositorioArtefactoDomain.VACIO, CargaPendienteDomain.VACIO, 1L))
                .isInstanceOf(EstudianteNoPropietarioArtefactoException.class);
    }

    @Test
    void debeLanzarAsesorNoVigente_cuandoDadoDeBaja() {
        // Arrange
        var eliminado = AsesorDomain.reconstruir(artefacto.getAsesor(), "20161020123", "Ana Perez",
                "ana@uco.edu.co", Instant.now(), Instant.now());

        // Act & Assert
        assertThatThrownBy(() -> validarPrecondiciones(artefacto, true, eliminado,
                VersionRepositorioArtefactoDomain.VACIO, CargaPendienteDomain.VACIO, 1L))
                .isInstanceOf(AsesorNoVigenteException.class);
    }

    @Test
    void debeLanzarVersionRepositorioNoEncontrada_antesQueLaCarga() {
        // Act & Assert
        assertThatThrownBy(() -> validarPrecondiciones(artefacto, true, asesor,
                VersionRepositorioArtefactoDomain.VACIO, CargaPendienteDomain.VACIO, 1L))
                .isInstanceOf(VersionRepositorioArtefactoNoEncontradaException.class);
    }

    @Test
    void debeLanzarCargaNoEncontrada() {
        // Act & Assert
        assertThatThrownBy(() -> validarPrecondiciones(artefacto, true, asesor, versionRepositorio,
                CargaPendienteDomain.VACIO, 1L))
                .isInstanceOf(CargaPendienteNoEncontradaException.class);
    }

    @Test
    void debeLanzarCargaNoCorresponde_cuandoEsDeOtroSolicitante() {
        // Arrange
        var ajena = CargaPendienteDomain.reconstruir(generacion.getCarga(), generacion.getArtefacto(),
                UtilUUID.generarNuevoUUID(), "informe.pdf", TIPO, 2048L, ETAG);

        // Act & Assert
        assertThatThrownBy(() -> validarPrecondiciones(artefacto, true, asesor, versionRepositorio, ajena, MAXIMO))
                .isInstanceOf(CargaNoCorrespondeException.class);
    }

    @Test
    void debeLanzarTamanoExcedido() {
        // Act & Assert
        assertThatThrownBy(() -> validarPrecondiciones(artefacto, true, asesor, versionRepositorio, carga, 1024L))
                .isInstanceOf(TamanoArchivoExcedidoException.class);
    }

    private RevisionAsesorDomain revisionEn(EstadoRevisionAsesor estado) {
        return RevisionAsesorDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), estado, Instant.now());
    }

    @Test
    void debePermitir_cuandoPrimeraVersion() {
        // Act & Assert
        assertThatCode(() -> validarPolitica(0, RevisionAsesorDomain.VACIO))
                .doesNotThrowAnyException();
    }

    @Test
    void debePermitir_cuandoEnProgreso() {
        // Arrange
        var revision = revisionEn(EstadoRevisionAsesor.EN_PROGRESO);

        // Act & Assert
        assertThatCode(() -> validarPolitica(2, revision)).doesNotThrowAnyException();
    }

    @Test
    void debeRechazar_cuandoSinRevision() {
        // Act & Assert
        assertThatThrownBy(() -> validarPolitica(2, RevisionAsesorDomain.VACIO))
                .isInstanceOf(VersionAnteriorSinRevisionException.class);
    }

    @ParameterizedTest
    @EnumSource(value = EstadoRevisionAsesor.class, names = {"PENDIENTE", "RESUELTA"})
    void debeRechazar_cuandoRevisionNoEstaEnProgreso(EstadoRevisionAsesor estado) {
        // Arrange
        var revision = revisionEn(estado);

        // Act & Assert
        assertThatThrownBy(() -> validarPolitica(2, revision))
                .isInstanceOf(RevisionVersionAnteriorNoEnProgresoException.class);
    }

    @Test
    void debeRechazar_cuandoCerrada() {
        // Arrange
        var revision = revisionEn(EstadoRevisionAsesor.CERRADA);

        // Act & Assert
        assertThatThrownBy(() -> validarPolitica(2, revision))
                .isInstanceOf(ArtefactoCerradoException.class);
    }

    @Test
    void debeLanzarTamanoExcedido_antesQueLaPoliticaDeSubida() {
        // Arrange
        var pendiente = revisionEn(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validarPrecondiciones(
                artefacto, true, asesor, versionRepositorio, carga, 1024L, new RevisionVersionAnterior(2, pendiente)))
                .isInstanceOf(TamanoArchivoExcedidoException.class);
    }
}
