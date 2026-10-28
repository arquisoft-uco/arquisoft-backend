package com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.VersionRepositorioArtefactoEntity;
import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.entity.VersionRepositorioArtefactoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.AgregarVersionRepositorioArtefactoKey;
import com.arquisoft.shared.message.key.artefactos.CambiarNombreRepositorioVersionRepositorioArtefactoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
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

    @Test
    void debeActualizarNombreDeTodasLasVersionesDelRepositorio() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var primera = UtilUUID.generarNuevoUUID();
        var segunda = UtilUUID.generarNuevoUUID();
        var tercera = UtilUUID.generarNuevoUUID();
        adapter.guardar(entidadDeRepositorio(primera, repositorio, "Doc uno"));
        adapter.guardar(entidadDeRepositorio(segunda, repositorio, "Doc dos"));
        adapter.guardar(entidadDeRepositorio(tercera, repositorio, "Doc tres"));
        entityManager.flush();

        // Act
        adapter.actualizarNombreRepositorio(repositorio, "Repositorio nuevo");
        entityManager.clear();

        // Assert
        for (var id : List.of(primera, segunda, tercera)) {
            var version = entityManager.find(VersionRepositorioArtefactoJpaEntity.class, id);
            assertThat(version.getNombreRepositorio()).isEqualTo("Repositorio nuevo");
        }
        verify(logger).debug(CambiarNombreRepositorioVersionRepositorioArtefactoKey.LOG_NOMBRE_ACTUALIZADO,
                repositorio);
    }

    @Test
    void debeNoTocarOtrosRepositorios() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var otroRepositorio = UtilUUID.generarNuevoUUID();
        var propia = UtilUUID.generarNuevoUUID();
        var ajena = UtilUUID.generarNuevoUUID();
        adapter.guardar(entidadDeRepositorio(propia, repositorio, "Doc uno"));
        adapter.guardar(entidadDeRepositorio(ajena, otroRepositorio, "Doc dos"));
        entityManager.flush();

        // Act
        adapter.actualizarNombreRepositorio(repositorio, "Repositorio nuevo");
        entityManager.clear();

        // Assert
        var intacta = entityManager.find(VersionRepositorioArtefactoJpaEntity.class, ajena);
        assertThat(intacta.getNombreRepositorio()).isEqualTo("Repositorio");
    }

    @Test
    void debeConsultarSoloLasVersionesExistentes_cuandoAlgunaIdNoExiste() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var primera = UtilUUID.generarNuevoUUID();
        var segunda = UtilUUID.generarNuevoUUID();
        var ajena = UtilUUID.generarNuevoUUID();
        adapter.guardar(entidadDeRepositorio(primera, repositorio, "Doc uno"));
        adapter.guardar(entidadDeRepositorio(segunda, repositorio, "Doc dos"));
        adapter.guardar(entidadDeRepositorio(ajena, UtilUUID.generarNuevoUUID(), "Doc tres"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var encontradas = adapter.consultarPorIds(List.of(primera, segunda, UtilUUID.generarNuevoUUID()));

        // Assert
        assertThat(encontradas).extracting(VersionRepositorioArtefactoEntity::id)
                .containsExactlyInAnyOrder(primera, segunda);
        assertThat(encontradas).allSatisfy(version -> {
            assertThat(version.repositorioArtefacto()).isEqualTo(repositorio);
            assertThat(version.nombreArchivo()).isEqualTo("plantilla.pdf");
        });
    }

    @Test
    void debeNoFallar_cuandoNoHayVersiones() {
        // Act & Assert
        assertThatCode(() -> adapter.actualizarNombreRepositorio(UtilUUID.generarNuevoUUID(), "Repositorio nuevo"))
                .doesNotThrowAnyException();
        assertThat(versionRepositorioArtefactoCommandRepository.count()).isZero();
    }

    @Test
    void debeConsultarSoloLosRepositoriosDelProyecto_cuandoHayVersionesDeVariosRepositorios() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var otroProyecto = UtilUUID.generarNuevoUUID();
        var repositorioUno = UtilUUID.generarNuevoUUID();
        var repositorioDos = UtilUUID.generarNuevoUUID();
        var repositorioAjeno = UtilUUID.generarNuevoUUID();
        var versionUno = UtilUUID.generarNuevoUUID();
        var versionDos = UtilUUID.generarNuevoUUID();
        var versionAjena = UtilUUID.generarNuevoUUID();
        adapter.guardar(entidadDeRepositorio(versionUno, repositorioUno, "Uno"));
        adapter.guardar(entidadDeRepositorio(versionDos, repositorioDos, "Dos"));
        adapter.guardar(entidadDeRepositorio(versionAjena, repositorioAjeno, "Ajena"));
        sembrarArtefacto(proyecto, versionUno);
        sembrarArtefacto(proyecto, versionDos);
        sembrarArtefacto(otroProyecto, versionAjena);
        entityManager.flush();
        entityManager.clear();

        // Act
        var repositorios = adapter.consultarRepositoriosAsignados(proyecto);
        var sinArtefactos = adapter.consultarRepositoriosAsignados(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(repositorios).containsExactlyInAnyOrder(repositorioUno, repositorioDos);
        assertThat(sinArtefactos).isEmpty();
    }

    private void sembrarArtefacto(UUID proyecto, UUID version) {
        entityManager.persist(ArtefactoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .proyectoGrado(proyecto)
                .versionRepositorioArtefacto(version)
                .asesor(UtilUUID.generarNuevoUUID())
                .build());
    }

    private VersionRepositorioArtefactoEntity entidadDeRepositorio(UUID id, UUID repositorio, String nombreDocumento) {
        return new VersionRepositorioArtefactoEntity(
                id, nombreDocumento, 1, repositorio, "Repositorio", "plantilla.pdf", "application/pdf", 2048L);
    }
}
