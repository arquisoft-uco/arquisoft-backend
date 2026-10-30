package com.arquisoft.artefactos.application.artefacto.command.validator.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.AsignacionArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.EstudianteArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.RepositorioArtefactoYaAsignadoException;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.exception.AsesorNoEncontradoException;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.artefactos.domain.asesorproyectogrado.exception.AsesorNoAsignadoProyectoGradoException;
import com.arquisoft.artefactos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
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

class RegistrarArtefactoValidatorImplTest {

    private final RegistrarArtefactoValidatorImpl validator = new RegistrarArtefactoValidatorImpl();

    private UUID proyecto;
    private UUID asesorId;
    private UUID versionId;
    private UUID repositorio;
    private AsignacionArtefactoDomain asignacion;
    private AsesorDomain asesorVigente;
    private AsesorProyectoGradoDomain vinculoVigente;
    private VersionRepositorioArtefactoDomain version;

    private AsignacionArtefactoDomain armar() {
        var artefacto = ArtefactoDomain.crear(proyecto, asesorId, versionId);
        var estudiantes = AgregacionEstudiantesArtefactoDomain.crear(asesorId,
                EstudianteArtefactoDomain.crear(artefacto.getId(), List.of(UtilUUID.generarNuevoUUID())));
        return AsignacionArtefactoDomain.crear(artefacto, estudiantes);
    }

    @BeforeEach
    void setUp() {
        proyecto = UtilUUID.generarNuevoUUID();
        asesorId = UtilUUID.generarNuevoUUID();
        versionId = UtilUUID.generarNuevoUUID();
        repositorio = UtilUUID.generarNuevoUUID();
        asignacion = armar();
        asesorVigente = AsesorDomain.reconstruir(
                asesorId, "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now(), null);
        vinculoVigente = AsesorProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), asesorId, proyecto, Instant.now(), null);
        version = VersionRepositorioArtefactoDomain.reconstruir(
                versionId, "Doc", 1, repositorio, "Repo", new DatosArchivo("a.pdf", "application/pdf", 10L));
    }

    @Test
    void debePasar_cuandoTodoEsValido() {
        // Act & Assert
        assertThatCode(() -> validator.validar(
                asignacion, true, asesorVigente, List.of(vinculoVigente), version, List.of()))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarProyectoNoEncontrado_antesQueElAsesor() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                asignacion, false, AsesorDomain.VACIO, List.of(), VersionRepositorioArtefactoDomain.VACIO, List.of(repositorio)))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarAsesorNoEncontrado_cuandoElProyectoExisteYElAsesorNo() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                asignacion, true, AsesorDomain.VACIO, List.of(), VersionRepositorioArtefactoDomain.VACIO, List.of()))
                .isInstanceOf(AsesorNoEncontradoException.class);
    }

    @Test
    void debeLanzarAsesorNoAsignado_cuandoOtroTieneElVinculoMasReciente() {
        // Arrange
        var otroAsesor = AsesorProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), proyecto, Instant.now(), null);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                asignacion, true, asesorVigente, List.of(otroAsesor), VersionRepositorioArtefactoDomain.VACIO, List.of(repositorio)))
                .isInstanceOf(AsesorNoAsignadoProyectoGradoException.class);
    }

    @Test
    void debeLanzarVersionNoEncontrada_antesQueElRepositorio() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                asignacion, true, asesorVigente, List.of(vinculoVigente), VersionRepositorioArtefactoDomain.VACIO, List.of(repositorio)))
                .isInstanceOf(VersionRepositorioArtefactoNoEncontradaException.class);
    }

    @Test
    void debeLanzarRepositorioYaAsignado_cuandoElProyectoYaTieneElRepositorio() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                asignacion, true, asesorVigente, List.of(vinculoVigente), version, List.of(repositorio)))
                .isInstanceOf(RepositorioArtefactoYaAsignadoException.class);
    }
}
