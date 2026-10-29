package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.artefactos.domain.asesor.exception.AsesorDeRevisionNoEncontradoException;
import com.arquisoft.artefactos.domain.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.versionartefacto.exception.VersionArtefactoNoEncontradaException;
import com.arquisoft.artefactos.domain.versionartefacto.exception.VersionArtefactoNoEsUltimaException;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MensajeSinIdentificadoresTest {

    static Stream<BiFunction<UUID, UUID, DomainException>> excepciones() {
        return Stream.of(
                (estudiante, revision) -> new AsesorDeRevisionNoEncontradoException(revision),
                (estudiante, revision) -> new EstudianteNoEncontradoException(estudiante),
                (estudiante, revision) -> new RevisionAsesorNoEncontradaException(revision),
                (estudiante, revision) -> new RevisionAsesorCerradaException(revision),
                (estudiante, revision) -> new EstadoDestinoRevisionAsesorNoPermitidoException(
                        revision, EstadoRevisionAsesor.CERRADA),
                (estudiante, revision) -> new EstudianteNoParticipaArtefactoException(estudiante, revision),
                (estudiante, revision) -> new ArtefactoCerradoException(revision),
                (estudiante, revision) -> new RevisionAsesorPendienteException(revision),
                (estudiante, revision) -> new VersionPosteriorARevisionNoExisteException(revision),
                (estudiante, revision) -> new RevisionAsesorAbiertaExistenteException(revision, 3),
                (estudiante, revision) -> new VersionArtefactoNoEncontradaException(revision),
                (estudiante, revision) -> new VersionArtefactoNoEsUltimaException(revision, 3),
                (estudiante, revision) -> new RevisionVersionAnteriorNoEnProgresoException(3,
                        RevisionAsesorDomain.reconstruir(revision, estudiante, EstadoRevisionAsesor.PENDIENTE,
                                UtilFecha.generarInstanteActual())));
    }

    @ParameterizedTest
    @MethodSource("excepciones")
    void noDebeExponerIdentificadoresAlCliente_cuandoSeConstruyeLaExcepcion(
            BiFunction<UUID, UUID, DomainException> fabrica) {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var revision = UtilUUID.generarNuevoUUID();

        // Act
        var excepcion = fabrica.apply(estudiante, revision);
        var mensaje = excepcion.getError().getMessage();

        // Assert
        assertThat(mensaje).isNotBlank().doesNotContain("%s", "??");
        assertThat(mensaje).doesNotContain(estudiante.toString(), revision.toString());
    }

    @ParameterizedTest
    @MethodSource("excepciones")
    void debeConservarLosIdentificadoresEnElDetalle_cuandoSeConstruyeLaExcepcion(
            BiFunction<UUID, UUID, DomainException> fabrica) {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var revision = UtilUUID.generarNuevoUUID();

        // Act
        var excepcion = fabrica.apply(estudiante, revision);

        // Assert
        assertThat(excepcion.getMessage()).isEqualTo(excepcion.getError().getDetalle())
                .doesNotContain("%s", "??")
                .containsAnyOf(estudiante.toString(), revision.toString());
    }
}
