package com.arquisoft.artefactos.infrastructure.revisionasesor.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.revisionasesor.command.secondaryadapter.entity.RevisionAsesorJpaEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.ArtefactoTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.EstudianteArtefactoTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.VersionArtefactoTestEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.CambiarEstadoRevisionAsesorKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class RevisionAsesorCommandOutputAdapterTest {

    @Autowired
    private RevisionAsesorCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private RevisionAsesorCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new RevisionAsesorCommandOutputAdapter(repository, logger);
    }

    private UUID sembrarRevision(UUID artefacto, String estado) {
        var version = UtilUUID.generarNuevoUUID();
        var revision = UtilUUID.generarNuevoUUID();
        entityManager.persist(new VersionArtefactoTestEntity(version, artefacto, 1));
        entityManager.persist(RevisionAsesorJpaEntity.builder()
                .id(revision).versionArtefacto(version).estado(estado).build());
        return revision;
    }

    @Test
    void debeObtenerPorId_cuandoExiste() {
        // Arrange
        var revision = sembrarRevision(UtilUUID.generarNuevoUUID(), "PENDIENTE");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerPorId(revision);

        // Assert
        assertThat(resultado).hasValueSatisfying(entity -> {
            assertThat(entity.id()).isEqualTo(revision);
            assertThat(entity.estado()).isEqualTo("PENDIENTE");
            assertThat(entity.versionArtefacto()).isNotNull();
        });
    }

    @Test
    void debeRetornarVacio_cuandoNoExiste() {
        // Act
        var resultado = adapter.obtenerPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeActualizarSoloElEstadoDeLaRevision_cuandoSeActualiza() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var objetivo = sembrarRevision(artefacto, "PENDIENTE");
        var otra = sembrarRevision(artefacto, "PENDIENTE");
        entityManager.flush();

        // Act
        adapter.actualizarEstado(objetivo, "RESUELTA");
        entityManager.clear();

        // Assert
        assertThat(entityManager.find(RevisionAsesorJpaEntity.class, objetivo).getEstado()).isEqualTo("RESUELTA");
        assertThat(entityManager.find(RevisionAsesorJpaEntity.class, otra).getEstado()).isEqualTo("PENDIENTE");
        verify(logger).debug(CambiarEstadoRevisionAsesorKey.LOG_ESTADO_ACTUALIZADO, objetivo);
    }

    @Test
    void existeParticipacion_debeSerVerdaderoCuandoElEstudianteEstaEnElArtefacto() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ArtefactoTestEntity(artefacto));
        entityManager.persist(new EstudianteArtefactoTestEntity(UtilUUID.generarNuevoUUID(), artefacto, estudiante));
        var revision = sembrarRevision(artefacto, "PENDIENTE");
        entityManager.flush();

        // Act
        var participa = adapter.existeParticipacion(revision, estudiante);

        // Assert
        assertThat(participa).isTrue();
    }

    @Test
    void existeParticipacion_debeSerFalsoCuandoElEstudianteEsDeOtroArtefactoOInexistente() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var otroArtefacto = UtilUUID.generarNuevoUUID();
        var estudianteAjeno = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ArtefactoTestEntity(artefacto));
        entityManager.persist(new ArtefactoTestEntity(otroArtefacto));
        entityManager.persist(
                new EstudianteArtefactoTestEntity(UtilUUID.generarNuevoUUID(), otroArtefacto, estudianteAjeno));
        var revision = sembrarRevision(artefacto, "PENDIENTE");
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.existeParticipacion(revision, estudianteAjeno)).isFalse();
        assertThat(adapter.existeParticipacion(UtilUUID.generarNuevoUUID(), estudianteAjeno)).isFalse();
    }
}
