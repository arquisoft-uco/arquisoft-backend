package com.arquisoft.artefactos.infrastructure.versionartefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import com.arquisoft.artefactos.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.EstadoRevisionAsesorTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.EstudianteArtefactoTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.RevisionAsesorTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.VersionArtefactoTestEntity;
import com.arquisoft.artefactos.infrastructure.versionartefacto.exception.VersionArtefactoNoDisponibleException;
import com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.entity.VersionRepositorioArtefactoJpaEntity;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class VersionArtefactoDetalleQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VersionArtefactoAsesorQueryRepository asesorRepository;

    @Autowired
    private VersionArtefactoEstudianteQueryRepository estudianteRepository;

    private VersionArtefactoQueryOutputAdapter detalleAdapter;
    private VersionArtefactoAccesoQueryOutputAdapter accesoAdapter;

    private final UUID asesor = UtilUUID.generarNuevoUUID();
    private final UUID estudiante = UtilUUID.generarNuevoUUID();
    private final UUID otroEstudiante = UtilUUID.generarNuevoUUID();

    @BeforeEach
    void setUp() {
        detalleAdapter = new VersionArtefactoQueryOutputAdapter(
                asesorRepository, new VersionArtefactoAsesorJpaSpecification(),
                estudianteRepository, new VersionArtefactoEstudianteJpaSpecification());
        accesoAdapter = new VersionArtefactoAccesoQueryOutputAdapter(asesorRepository, estudianteRepository);
    }

    private UUID sembrarArtefacto(Instant omitidoEn) {
        var proyecto = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ProyectoGradoJpaEntity(proyecto, "Alfa"));
        var documento = UtilUUID.generarNuevoUUID();
        entityManager.persist(new VersionRepositorioArtefactoJpaEntity(
                documento, "Anteproyecto", 1, UtilUUID.generarNuevoUUID(), "Repo Uno",
                "archivo.pdf", "application/pdf", 10L));
        var artefacto = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ArtefactoJpaEntity(artefacto, proyecto, documento, asesor, omitidoEn));
        return artefacto;
    }

    private UUID sembrarVersion(UUID artefacto, int numero) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new VersionArtefactoTestEntity(
                id, artefacto, numero, Instant.parse("2026-10-20T10:00:00Z"), "artefactos/" + id + ".pdf"));
        return id;
    }

    private void sembrarAsesorYEstados() {
        entityManager.persist(new AsesorJpaEntity(asesor, "ID-Ana", "Ana", "ana@uco.edu.co", Instant.now(), null));
        entityManager.persist(new EstadoRevisionAsesorTestEntity("PENDIENTE", "Pendiente", 1));
        entityManager.persist(new EstadoRevisionAsesorTestEntity("RESUELTA", "Resuelta", 3));
    }

    private void confirmarSiembra() {
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void debeConsultarPorId_conRevisionMasReciente() {
        // Arrange
        sembrarAsesorYEstados();
        var version = sembrarVersion(sembrarArtefacto(null), 2);
        var reciente = UtilUUID.generarNuevoUUID();
        entityManager.persist(new RevisionAsesorTestEntity(
                UtilUUID.generarNuevoUUID(), version, "PENDIENTE", Instant.parse("2026-10-21T10:00:00Z")));
        entityManager.persist(new RevisionAsesorTestEntity(
                reciente, version, "RESUELTA", Instant.parse("2026-10-22T10:00:00Z")));
        confirmarSiembra();

        // Act
        var resultado = detalleAdapter.consultarPorId(version);

        // Assert
        assertThat(resultado.id()).isEqualTo(version);
        assertThat(resultado.version()).isEqualTo(2);
        assertThat(resultado.nombreProyecto()).isEqualTo("Alfa");
        assertThat(resultado.nombreDocumento()).isEqualTo("Anteproyecto");
        assertThat(resultado.nombreRepositorio()).isEqualTo("Repo Uno");
        assertThat(resultado.nombreAsesor()).isEqualTo("Ana");
        assertThat(resultado.revisionAsesor()).isEqualTo(reciente);
        assertThat(resultado.estadoRevision()).isEqualTo("RESUELTA");
        assertThat(resultado.nombreEstadoRevision()).isEqualTo("Resuelta");
    }

    @Test
    void debeConsultarPorId_sinRevision_yDeArtefactoOmitido() {
        // Arrange
        sembrarAsesorYEstados();
        var version = sembrarVersion(sembrarArtefacto(Instant.parse("2026-10-25T10:00:00Z")), 1);
        confirmarSiembra();

        // Act
        var resultado = detalleAdapter.consultarPorId(version);

        // Assert
        assertThat(resultado.id()).isEqualTo(version);
        assertThat(resultado.revisionAsesor()).isNull();
        assertThat(resultado.estadoRevision()).isNull();
        assertThat(resultado.nombreEstadoRevision()).isNull();
    }

    @Test
    void debeLanzarNoDisponible_cuandoAusente() {
        // Arrange
        var ausente = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> detalleAdapter.consultarPorId(ausente))
                .isInstanceOf(VersionArtefactoNoDisponibleException.class)
                .extracting("codigoError")
                .isEqualTo(ArtefactosCodes.VersionArtefacto.NO_DISPONIBLE);
    }

    @Test
    void debeDevolverAsesor_cuandoVersionExiste_yVacio_cuandoNoExiste() {
        // Arrange
        sembrarAsesorYEstados();
        var version = sembrarVersion(sembrarArtefacto(null), 1);
        confirmarSiembra();

        // Act
        var existente = accesoAdapter.obtenerAsesorPorId(version);
        var ausente = accesoAdapter.obtenerAsesorPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(existente).contains(asesor);
        assertThat(ausente).isEmpty();
    }

    @Test
    void debeDetectarParticipacion_soloDelEstudianteVinculado() {
        // Arrange
        sembrarAsesorYEstados();
        var artefacto = sembrarArtefacto(null);
        var version = sembrarVersion(artefacto, 1);
        entityManager.persist(new EstudianteArtefactoTestEntity(UtilUUID.generarNuevoUUID(), artefacto, estudiante));
        confirmarSiembra();

        // Act
        var vinculado = accesoAdapter.existeParticipacion(version, estudiante);
        var noVinculado = accesoAdapter.existeParticipacion(version, otroEstudiante);
        var versionAjena = accesoAdapter.existeParticipacion(UtilUUID.generarNuevoUUID(), estudiante);

        // Assert
        assertThat(vinculado).isTrue();
        assertThat(noVinculado).isFalse();
        assertThat(versionAjena).isFalse();
    }
}
