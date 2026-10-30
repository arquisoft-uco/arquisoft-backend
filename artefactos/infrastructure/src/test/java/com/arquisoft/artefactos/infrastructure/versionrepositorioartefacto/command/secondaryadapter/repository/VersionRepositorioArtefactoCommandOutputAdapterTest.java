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

import java.time.Instant;
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
    void debeObtenerLaVersion_cuandoExiste() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var buscada = UtilUUID.generarNuevoUUID();
        adapter.guardar(entidadDeRepositorio(buscada, repositorio, "Doc uno"));
        adapter.guardar(entidadDeRepositorio(UtilUUID.generarNuevoUUID(), repositorio, "Doc dos"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var encontrada = adapter.obtenerPorId(buscada);

        // Assert
        assertThat(encontrada).hasValueSatisfying(version -> {
            assertThat(version.id()).isEqualTo(buscada);
            assertThat(version.nombreDocumento()).isEqualTo("Doc uno");
            assertThat(version.repositorioArtefacto()).isEqualTo(repositorio);
            assertThat(version.nombreArchivo()).isEqualTo("plantilla.pdf");
        });
    }

    @Test
    void debeDevolverVacio_cuandoLaVersionNoExiste() {
        // Arrange
        adapter.guardar(entidadDeRepositorio(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Doc uno"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var encontrada = adapter.obtenerPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(encontrada).isEmpty();
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

    @Test
    void debeIgnorarLosArtefactosOmitidos_cuandoConsultaLosRepositoriosAsignados() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var repositorioActivo = UtilUUID.generarNuevoUUID();
        var repositorioOmitido = UtilUUID.generarNuevoUUID();
        var versionActiva = UtilUUID.generarNuevoUUID();
        var versionOmitida = UtilUUID.generarNuevoUUID();
        adapter.guardar(entidadDeRepositorio(versionActiva, repositorioActivo, "Activo"));
        adapter.guardar(entidadDeRepositorio(versionOmitida, repositorioOmitido, "Omitido"));
        sembrarArtefacto(proyecto, versionActiva);
        entityManager.persist(ArtefactoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .proyectoGrado(proyecto)
                .versionRepositorioArtefacto(versionOmitida)
                .asesor(UtilUUID.generarNuevoUUID())
                .omitidoEn(Instant.now())
                .build());
        entityManager.flush();
        entityManager.clear();

        // Act
        var repositorios = adapter.consultarRepositoriosAsignados(proyecto);

        // Assert
        assertThat(repositorios).containsExactly(repositorioActivo);
    }

    @Test
    void debeObtenerLaVersionDelArtefacto_cuandoExiste() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID();
        var otraVersion = UtilUUID.generarNuevoUUID();
        adapter.guardar(entidadDeRepositorio(version, UtilUUID.generarNuevoUUID(), "Uno"));
        adapter.guardar(entidadDeRepositorio(otraVersion, UtilUUID.generarNuevoUUID(), "Dos"));
        var artefacto = UtilUUID.generarNuevoUUID();
        entityManager.persist(ArtefactoJpaEntity.builder()
                .id(artefacto)
                .proyectoGrado(proyecto)
                .versionRepositorioArtefacto(version)
                .asesor(UtilUUID.generarNuevoUUID())
                .build());
        entityManager.flush();
        entityManager.clear();

        // Act
        var encontrada = adapter.obtenerPorArtefacto(artefacto);

        // Assert
        assertThat(encontrada).hasValueSatisfying(entity -> assertThat(entity.id()).isEqualTo(version));
        assertThat(adapter.obtenerPorArtefacto(UtilUUID.generarNuevoUUID())).isEmpty();
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
