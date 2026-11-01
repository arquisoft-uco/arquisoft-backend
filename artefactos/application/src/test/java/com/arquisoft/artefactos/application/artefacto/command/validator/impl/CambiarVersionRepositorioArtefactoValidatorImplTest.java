package com.arquisoft.artefactos.application.artefacto.command.validator.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.CambioVersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoNoEncontradoException;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoOmitidoException;
import com.arquisoft.artefactos.domain.artefacto.exception.AsesorNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.artefacto.exception.CambioVersionRepositorioNoPermitidoException;
import com.arquisoft.artefactos.domain.artefacto.exception.VersionRepositorioNoPosteriorException;
import com.arquisoft.artefactos.domain.artefacto.exception.VersionRepositorioOtroRepositorioException;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.exception.AsesorNoEncontradoException;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.RevisionVersionAnterior;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoNoEncontradaException;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarVersionRepositorioArtefactoValidatorImplTest {

    private final CambiarVersionRepositorioArtefactoValidatorImpl validator =
            new CambiarVersionRepositorioArtefactoValidatorImpl();

    private UUID artefactoId;
    private UUID asesorId;
    private UUID repositorioId;
    private CambioVersionRepositorioArtefactoDomain cambio;
    private ArtefactoDomain artefacto;
    private AsesorDomain asesor;
    private VersionRepositorioArtefactoDomain actual;
    private VersionRepositorioArtefactoDomain elegida;
    private RevisionVersionAnterior pendiente;

    @BeforeEach
    void setUp() {
        artefactoId = UtilUUID.generarNuevoUUID();
        asesorId = UtilUUID.generarNuevoUUID();
        repositorioId = UtilUUID.generarNuevoUUID();
        var actualId = UtilUUID.generarNuevoUUID();
        var elegidaId = UtilUUID.generarNuevoUUID();
        cambio = CambioVersionRepositorioArtefactoDomain.crear(artefactoId, asesorId, elegidaId);
        artefacto = artefactoConAsesor(asesorId, null);
        asesor = AsesorDomain.reconstruir(
                asesorId, "20161020123", "Ana Perez", "ana@uco.edu.co", UtilFecha.generarInstanteActual(), null);
        actual = version(artefacto.getVersionRepositorioArtefacto(), 1, repositorioId);
        elegida = version(elegidaId, 2, repositorioId);
        pendiente = new RevisionVersionAnterior(2, revisionEn(EstadoRevisionAsesor.PENDIENTE));
    }

    private ArtefactoDomain artefactoConAsesor(UUID asesorDelArtefacto, Instant omitidoEn) {
        var versionId = artefacto == null ? UtilUUID.generarNuevoUUID() : artefacto.getVersionRepositorioArtefacto();
        return ArtefactoDomain.reconstruir(
                artefactoId, UtilUUID.generarNuevoUUID(), versionId, asesorDelArtefacto, omitidoEn);
    }

    private static VersionRepositorioArtefactoDomain version(UUID id, int numero, UUID repositorio) {
        return VersionRepositorioArtefactoDomain.reconstruir(id, "Formato A", numero, repositorio, "Repo",
                new DatosArchivo("a.pdf", "application/pdf", 10L));
    }

    private static RevisionAsesorDomain revisionEn(EstadoRevisionAsesor estado) {
        return RevisionAsesorDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), estado, UtilFecha.generarInstanteActual());
    }

    @Test
    void debePasar_cuandoRevisionPendienteYTodoValido() {
        // Act & Assert
        assertThatCode(() -> validator.validar(cambio, artefacto, asesor, actual, elegida, pendiente))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarArtefactoNoEncontrado_antesQueLasDemasReglas_cuandoTodoFalla() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, ArtefactoDomain.VACIO, AsesorDomain.VACIO,
                VersionRepositorioArtefactoDomain.VACIO, VersionRepositorioArtefactoDomain.VACIO, pendiente))
                .isInstanceOf(ArtefactoNoEncontradoException.class);
    }

    @Test
    void debeLanzarAsesorNoEncontrado_antesQueLasVersiones_cuandoElAsesorNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, artefacto, AsesorDomain.VACIO,
                VersionRepositorioArtefactoDomain.VACIO, VersionRepositorioArtefactoDomain.VACIO, pendiente))
                .isInstanceOf(AsesorNoEncontradoException.class);
    }

    @Test
    void debeLanzarVersionNoEncontrada_cuandoLaVersionActualNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, artefacto, asesor,
                VersionRepositorioArtefactoDomain.VACIO, elegida, pendiente))
                .isInstanceOf(VersionRepositorioArtefactoNoEncontradaException.class);
    }

    @Test
    void debeLanzarVersionNoEncontrada_cuandoLaVersionElegidaNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, artefacto, asesor,
                actual, VersionRepositorioArtefactoDomain.VACIO, pendiente))
                .isInstanceOf(VersionRepositorioArtefactoNoEncontradaException.class);
    }

    @Test
    void debeLanzarAsesorNoPropietario_antesQueElArtefactoOmitido_cuandoOtroAsesorEsElResponsable() {
        // Arrange
        var ajeno = artefactoConAsesor(UtilUUID.generarNuevoUUID(), UtilFecha.generarInstanteActual());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, ajeno, asesor, actual, elegida, pendiente))
                .isInstanceOf(AsesorNoPropietarioArtefactoException.class);
    }

    @Test
    void debeLanzarArtefactoOmitido_antesQueLasReglasDeVersion_cuandoElArtefactoEstaOmitido() {
        // Arrange
        var omitido = artefactoConAsesor(asesorId, UtilFecha.generarInstanteActual());
        var otroRepositorio = version(elegida.getId(), 2, UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, omitido, asesor, actual, otroRepositorio, pendiente))
                .isInstanceOf(ArtefactoOmitidoException.class);
    }

    @Test
    void debeLanzarOtroRepositorio_antesQueNoPosterior_cuandoLaVersionEsDeOtroRepositorio() {
        // Arrange
        var otroRepositorio = version(elegida.getId(), 1, UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, artefacto, asesor, actual, otroRepositorio, pendiente))
                .isInstanceOf(VersionRepositorioOtroRepositorioException.class);
    }

    @Test
    void debeLanzarNoPosterior_antesQueElMomentoDelCiclo_cuandoLaVersionNoEsMayor() {
        // Arrange
        var igual = version(elegida.getId(), 1, repositorioId);
        var resuelta = new RevisionVersionAnterior(2, revisionEn(EstadoRevisionAsesor.RESUELTA));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, artefacto, asesor, actual, igual, resuelta))
                .isInstanceOf(VersionRepositorioNoPosteriorException.class);
    }

    @Test
    void debeLanzarCambioNoPermitido_cuandoLaRevisionEstaResuelta() {
        // Arrange
        var resuelta = new RevisionVersionAnterior(2, revisionEn(EstadoRevisionAsesor.RESUELTA));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, artefacto, asesor, actual, elegida, resuelta))
                .isInstanceOf(CambioVersionRepositorioNoPermitidoException.class);
    }
}
