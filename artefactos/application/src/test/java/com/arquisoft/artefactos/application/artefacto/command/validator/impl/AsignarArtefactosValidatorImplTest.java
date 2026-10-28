package com.arquisoft.artefactos.application.artefacto.command.validator.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.AsignacionArtefactosDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.RepositorioArtefactoYaAsignadoException;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.exception.AsesorNoVigenteException;
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

class AsignarArtefactosValidatorImplTest {

    private final AsignarArtefactosValidatorImpl validator = new AsignarArtefactosValidatorImpl();

    private UUID proyecto;
    private UUID asesorId;
    private UUID versionId;
    private UUID repositorio;
    private AsignacionArtefactosDomain asignacion;
    private AsesorDomain asesorVigente;
    private AsesorProyectoGradoDomain vinculoVigente;
    private VersionRepositorioArtefactoDomain version;

    @BeforeEach
    void setUp() {
        proyecto = UtilUUID.generarNuevoUUID();
        asesorId = UtilUUID.generarNuevoUUID();
        versionId = UtilUUID.generarNuevoUUID();
        repositorio = UtilUUID.generarNuevoUUID();
        asignacion = AsignacionArtefactosDomain.crear(ArtefactoDomain.crear(proyecto, asesorId, List.of(versionId)));
        asesorVigente = AsesorDomain.reconstruir(
                asesorId, "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now(), null);
        vinculoVigente = AsesorProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), asesorId, proyecto, Instant.now(), null);
        version = VersionRepositorioArtefactoDomain.reconstruir(
                versionId, "Doc", 1, repositorio, "Repo", new DatosArchivo("a.pdf", "application/pdf", 10L));
    }

    @Test
    void debePasar_cuandoTodasLasReglasSeCumplen() {
        // Act & Assert
        assertThatCode(() -> validator.validar(
                asignacion, true, asesorVigente, List.of(vinculoVigente), List.of(version), List.of()))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarProyectoNoEncontrado_antesQueLasDemasReglas_cuandoTodoFalla() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                asignacion, false, AsesorDomain.VACIO, List.of(), List.of(), List.of(repositorio)))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarAsesorNoVigente_antesQueElVinculo_cuandoElAsesorEstaDadoDeBaja() {
        // Arrange
        var eliminado = AsesorDomain.reconstruir(
                asesorId, "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now(), Instant.now());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                asignacion, true, eliminado, List.of(), List.of(), List.of()))
                .isInstanceOf(AsesorNoVigenteException.class);
    }

    @Test
    void debeLanzarAsesorNoAsignado_antesQueLasVersiones_cuandoNoHayVinculoVigente() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                asignacion, true, asesorVigente, List.of(), List.of(), List.of()))
                .isInstanceOf(AsesorNoAsignadoProyectoGradoException.class);
    }

    @Test
    void debeLanzarAsesorNoAsignado_antesQueLasVersiones_cuandoElVinculoEstaFinalizado() {
        // Arrange
        var finalizado = AsesorProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), asesorId, proyecto, Instant.now(), Instant.now());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                asignacion, true, asesorVigente, List.of(finalizado), List.of(), List.of()))
                .isInstanceOf(AsesorNoAsignadoProyectoGradoException.class);
    }

    @Test
    void debeLanzarAsesorNoAsignado_cuandoOtroAsesorTieneElVinculoMasReciente() {
        // Arrange
        var otroAsesor = AsesorProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), proyecto, Instant.now(), null);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                asignacion, true, asesorVigente, List.of(otroAsesor), List.of(), List.of()))
                .isInstanceOf(AsesorNoAsignadoProyectoGradoException.class);
    }

    @Test
    void debeLanzarVersionNoEncontrada_antesQueElRepositorio_cuandoFaltaUnaVersion() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                asignacion, true, asesorVigente, List.of(vinculoVigente), List.of(), List.of(repositorio)))
                .isInstanceOf(VersionRepositorioArtefactoNoEncontradaException.class);
    }

    @Test
    void debeLanzarRepositorioYaAsignado_cuandoElProyectoYaTieneElRepositorio() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                asignacion, true, asesorVigente, List.of(vinculoVigente), List.of(version), List.of(repositorio)))
                .isInstanceOf(RepositorioArtefactoYaAsignadoException.class);
    }
}
