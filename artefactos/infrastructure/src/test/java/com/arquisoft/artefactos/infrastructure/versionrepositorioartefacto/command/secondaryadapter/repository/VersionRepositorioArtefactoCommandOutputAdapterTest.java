package com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.VersionRepositorioArtefactoEntity;
import com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.entity.VersionRepositorioArtefactoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.AgregarVersionRepositorioArtefactoKey;
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
class VersionRepositorioArtefactoCommandOutputAdapterTest {

    @Autowired
    private VersionRepositorioArtefactoCommandRepository versionRepositorioArtefactoCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private VersionRepositorioArtefactoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new VersionRepositorioArtefactoCommandOutputAdapter(
                versionRepositorioArtefactoCommandRepository, logger);
    }

    private VersionRepositorioArtefactoEntity entity(UUID id, String nombreDocumento) {
        return new VersionRepositorioArtefactoEntity(
                id, nombreDocumento, 2, UtilUUID.generarNuevoUUID(), "Repositorio", "plantilla.pdf",
                "application/pdf", 2048L);
    }

    @Test
    void debeGuardarYLeerDeVuelta_cuandoSePersiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entity = entity(id, "Plantilla");

        // Act
        adapter.guardar(entity);
        entityManager.flush();
        entityManager.clear();

        // Assert
        var guardada = entityManager.find(VersionRepositorioArtefactoJpaEntity.class, id);
        assertThat(guardada.getNombreDocumento()).isEqualTo("Plantilla");
        assertThat(guardada.getVersionDocumento()).isEqualTo(2);
        assertThat(guardada.getRepositorioArtefacto()).isEqualTo(entity.repositorioArtefacto());
        assertThat(guardada.getNombreRepositorio()).isEqualTo("Repositorio");
        assertThat(guardada.getNombreArchivo()).isEqualTo("plantilla.pdf");
        assertThat(guardada.getTipoContenido()).isEqualTo("application/pdf");
        assertThat(guardada.getTamanoBytes()).isEqualTo(2048L);
        verify(logger).debug(AgregarVersionRepositorioArtefactoKey.LOG_GUARDADA, id);
    }

    @Test
    void debeReportarExistencia_cuandoElIdEstaGuardado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        adapter.guardar(entity(id, "Plantilla"));
        entityManager.flush();

        // Act
        var existe = adapter.existePorId(id);

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void debeReportarAusencia_cuandoNoExiste() {
        // Act
        var existe = adapter.existePorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(existe).isFalse();
    }

    @Test
    void debeConservarUnaFila_cuandoSeGuardaDosVecesElMismoId() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        adapter.guardar(entity(id, "Plantilla"));
        entityManager.flush();
        adapter.guardar(entity(id, "Plantilla"));
        entityManager.flush();

        // Assert
        assertThat(versionRepositorioArtefactoCommandRepository.count()).isEqualTo(1L);
    }
}
