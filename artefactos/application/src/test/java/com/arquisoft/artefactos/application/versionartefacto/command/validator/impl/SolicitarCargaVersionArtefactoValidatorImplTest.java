package com.arquisoft.artefactos.application.versionartefacto.command.validator.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoNoEncontradoException;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoOmitidoException;
import com.arquisoft.artefactos.domain.artefacto.exception.EstudianteNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.exception.ArtefactoCerradoException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionVersionAnteriorNoEnProgresoException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.VersionAnteriorSinRevisionException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.versionartefacto.SolicitudCargaVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.exception.TamanoArchivoExcedidoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitarCargaVersionArtefactoValidatorImplTest {

    private static final long MAXIMO = 5_242_880L;

    private final SolicitarCargaVersionArtefactoValidatorImpl validator =
            new SolicitarCargaVersionArtefactoValidatorImpl();

    private SolicitudCargaVersionArtefactoDomain solicitud;
    private ArtefactoDomain artefacto;

    @BeforeEach
    void setUp() {
        solicitud = SolicitudCargaVersionArtefactoDomain.crear(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "informe.pdf", "application/pdf", 2048L);
        artefacto = ArtefactoDomain.reconstruir(solicitud.getArtefacto(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), null);
    }

    private RevisionAsesorDomain revisionEn(EstadoRevisionAsesor estado) {
        return RevisionAsesorDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), estado, Instant.now());
    }

    @Test
    void debePermitir_cuandoPrimeraVersion() {
        // Act & Assert
        assertThatCode(() -> validator.validar(
                solicitud, artefacto, true, MAXIMO, 0, RevisionAsesorDomain.VACIO)).doesNotThrowAnyException();
    }

    @Test
    void debePermitir_cuandoEnProgreso() {
        // Arrange
        var revision = revisionEn(EstadoRevisionAsesor.EN_PROGRESO);

        // Act & Assert
        assertThatCode(() -> validator.validar(solicitud, artefacto, true, MAXIMO, 2, revision))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarArtefactoNoEncontrado_primero() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                solicitud, ArtefactoDomain.VACIO, false, 1L, 2, RevisionAsesorDomain.VACIO))
                .isInstanceOf(ArtefactoNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoPropietario_antesQueLaPoliticaDeSubida() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                solicitud, artefacto, false, 1L, 2, RevisionAsesorDomain.VACIO))
                .isInstanceOf(EstudianteNoPropietarioArtefactoException.class);
    }

    @Test
    void debeLanzarArtefactoOmitido_antesQueLaPoliticaDeSubida_cuandoElArtefactoEstaOmitido() {
        // Arrange
        var omitido = ArtefactoDomain.reconstruir(artefacto.getId(), artefacto.getProyectoGrado(),
                artefacto.getVersionRepositorioArtefacto(), artefacto.getAsesor(), Instant.now());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                solicitud, omitido, true, MAXIMO, 2, RevisionAsesorDomain.VACIO))
                .isInstanceOf(ArtefactoOmitidoException.class);
    }

    @Test
    void debeRechazar_cuandoSinRevision() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                solicitud, artefacto, true, MAXIMO, 2, RevisionAsesorDomain.VACIO))
                .isInstanceOf(VersionAnteriorSinRevisionException.class);
    }

    @ParameterizedTest
    @EnumSource(value = EstadoRevisionAsesor.class, names = {"PENDIENTE", "RESUELTA"})
    void debeRechazar_cuandoRevisionNoEstaEnProgreso(EstadoRevisionAsesor estado) {
        // Arrange
        var revision = revisionEn(estado);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(solicitud, artefacto, true, MAXIMO, 2, revision))
                .isInstanceOf(RevisionVersionAnteriorNoEnProgresoException.class);
    }

    @Test
    void debeRechazar_cuandoCerrada() {
        // Arrange
        var revision = revisionEn(EstadoRevisionAsesor.CERRADA);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(solicitud, artefacto, true, MAXIMO, 2, revision))
                .isInstanceOf(ArtefactoCerradoException.class);
    }

    @Test
    void debeLanzarPoliticaDeSubida_antesQueElTamano() {
        // Arrange
        var revision = revisionEn(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(solicitud, artefacto, true, 1024L, 2, revision))
                .isInstanceOf(RevisionVersionAnteriorNoEnProgresoException.class);
    }

    @Test
    void debeLanzarTamanoExcedido_cuandoLaPoliticaPermiteSubir() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                solicitud, artefacto, true, 1024L, 0, RevisionAsesorDomain.VACIO))
                .isInstanceOf(TamanoArchivoExcedidoException.class);
    }
}
