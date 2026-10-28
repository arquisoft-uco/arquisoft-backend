package com.arquisoft.artefactos.application.estudianteartefacto.command.validator.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoNoEncontradoException;
import com.arquisoft.artefactos.domain.artefacto.exception.AsesorNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.exception.AsesorNoEncontradoException;
import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.artefactos.domain.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.artefactos.domain.estudiante.exception.EstudianteNoVigenteException;
import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.EstudianteArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.exception.EstudianteNoVinculadoProyectoGradoException;
import com.arquisoft.artefactos.domain.estudianteartefacto.exception.EstudianteYaVinculadoArtefactoException;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoNoEncontradaException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsignarEstudiantesArtefactoValidatorImplTest {

    private final AsignarEstudiantesArtefactoValidatorImpl validator = new AsignarEstudiantesArtefactoValidatorImpl();

    private UUID asesorId;
    private UUID estudianteId;
    private AgregacionEstudiantesArtefactoDomain entrada;
    private ArtefactoDomain artefacto;
    private AsesorDomain asesor;
    private VersionRepositorioArtefactoDomain version;
    private EstudianteDomain estudiante;

    @BeforeEach
    void setUp() {
        asesorId = UtilUUID.generarNuevoUUID();
        estudianteId = UtilUUID.generarNuevoUUID();
        var versionId = UtilUUID.generarNuevoUUID();
        artefacto = ArtefactoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), versionId, asesorId);
        entrada = AgregacionEstudiantesArtefactoDomain.crear(
                asesorId, EstudianteArtefactoDomain.crear(artefacto.getId(), List.of(estudianteId)));
        asesor = AsesorDomain.reconstruir(asesorId, "1", "Ana Perez", "ana@uco.edu.co", Instant.now(), null);
        version = VersionRepositorioArtefactoDomain.reconstruir(
                versionId, "Doc", 1, UtilUUID.generarNuevoUUID(), "Repo",
                new DatosArchivo("a.pdf", "application/pdf", 10L));
        estudiante = EstudianteDomain.reconstruir(estudianteId, "2", "Laura Gomez", "l@uco.edu.co", Instant.EPOCH, null);
    }

    @Test
    void debePasar_cuandoTodasLasReglasSeCumplen() {
        // Act & Assert
        assertThatCode(() -> validator.validar(
                entrada, artefacto, asesor, version, List.of(estudiante), List.of(estudianteId), List.of()))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarArtefactoNoEncontrado_antesQueElAsesorInexistente() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada, ArtefactoDomain.VACIO, AsesorDomain.VACIO, version, List.of(estudiante), List.of(estudianteId), List.of()))
                .isInstanceOf(ArtefactoNoEncontradoException.class);
    }

    @Test
    void debeLanzarAsesorNoEncontrado_antesQueLaVersionInexistente() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada, artefacto, AsesorDomain.VACIO, VersionRepositorioArtefactoDomain.VACIO,
                List.of(estudiante), List.of(estudianteId), List.of()))
                .isInstanceOf(AsesorNoEncontradoException.class);
    }

    @Test
    void debeLanzarVersionNoEncontrada_antesQueLosEstudiantesInexistentes() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada, artefacto, asesor, VersionRepositorioArtefactoDomain.VACIO, List.of(), List.of(estudianteId), List.of()))
                .isInstanceOf(VersionRepositorioArtefactoNoEncontradaException.class);
    }

    @Test
    void debeLanzarEstudianteNoEncontrado_antesQueLaPropiedadDelArtefacto() {
        // Arrange
        var ajeno = ArtefactoDomain.reconstruir(
                artefacto.getId(), artefacto.getProyectoGrado(), artefacto.getVersionRepositorioArtefacto(),
                UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada, ajeno, asesor, version, List.of(), List.of(estudianteId), List.of()))
                .isInstanceOf(EstudianteNoEncontradoException.class);
    }

    @Test
    void debeLanzarAsesorNoPropietario_antesQueLaVigenciaDelEstudiante() {
        // Arrange
        var ajeno = ArtefactoDomain.reconstruir(
                artefacto.getId(), artefacto.getProyectoGrado(), artefacto.getVersionRepositorioArtefacto(),
                UtilUUID.generarNuevoUUID());
        var eliminado = EstudianteDomain.reconstruir(estudianteId, "2", "Laura Gomez", "l@uco.edu.co", Instant.EPOCH, Instant.now());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada, ajeno, asesor, version, List.of(eliminado), List.of(estudianteId), List.of()))
                .isInstanceOf(AsesorNoPropietarioArtefactoException.class);
    }

    @Test
    void debeLanzarEstudianteNoVigente_antesQueLaPertenenciaAlProyecto() {
        // Arrange
        var eliminado = EstudianteDomain.reconstruir(estudianteId, "2", "Laura Gomez", "l@uco.edu.co", Instant.EPOCH, Instant.now());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada, artefacto, asesor, version, List.of(eliminado), List.of(), List.of()))
                .isInstanceOf(EstudianteNoVigenteException.class);
    }

    @Test
    void debeLanzarNoVinculadoAlProyecto_antesQueElVinculoPrevioAlArtefacto() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada, artefacto, asesor, version, List.of(estudiante), List.of(), List.of(estudianteId)))
                .isInstanceOf(EstudianteNoVinculadoProyectoGradoException.class);
    }

    @Test
    void debeLanzarYaVinculado_cuandoElEstudianteYaEstaVinculado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada, artefacto, asesor, version, List.of(estudiante), List.of(estudianteId), List.of(estudianteId)))
                .isInstanceOf(EstudianteYaVinculadoArtefactoException.class);
    }
}
