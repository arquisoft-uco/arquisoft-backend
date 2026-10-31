package com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.artefacto.command.secondaryport.entity.ArtefactoEntity;
import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.RevisionAsesorTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.VersionArtefactoTestEntity;
import com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.entity.VersionRepositorioArtefactoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DataJpaTest
class ArtefactoCommandOutputAdapterTest {

    @Autowired
    private ArtefactoCommandRepository artefactoCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private ArtefactoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ArtefactoCommandOutputAdapter(artefactoCommandRepository, logger);
    }

    @Test
    void debePersistirElArtefacto_yLoguearGuardado() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var segundo = new ArtefactoEntity(
                UtilUUID.generarNuevoUUID(), proyecto, UtilUUID.generarNuevoUUID(), asesor, null);

        // Act
        adapter.registrar(segundo);
        entityManager.flush();
        entityManager.clear();

        // Assert
        var guardado = entityManager.find(ArtefactoJpaEntity.class, segundo.id());
        assertThat(guardado.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(guardado.getVersionRepositorioArtefacto()).isEqualTo(segundo.versionRepositorioArtefacto());
        assertThat(guardado.getAsesor()).isEqualTo(asesor);
        verify(logger).debug(ArtefactoKey.LOG_GUARDADO, segundo.id());
    }

    @Test
    void debeTraspasarSoloLosArtefactosDelProyecto_cuandoHayDeOtrosProyectos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var otroProyecto = UtilUUID.generarNuevoUUID();
        var anterior = UtilUUID.generarNuevoUUID();
        var nuevo = UtilUUID.generarNuevoUUID();
        var propios = List.of(
                sembrar(proyecto, anterior), sembrar(proyecto, anterior), sembrar(proyecto, anterior));
        var ajeno = sembrar(otroProyecto, anterior);
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.traspasarAsesorDeProyecto(proyecto, nuevo);
        entityManager.clear();

        // Assert
        propios.forEach(id -> assertThat(entityManager.find(ArtefactoJpaEntity.class, id).getAsesor())
                .isEqualTo(nuevo));
        assertThat(entityManager.find(ArtefactoJpaEntity.class, ajeno).getAsesor()).isEqualTo(anterior);
        verify(logger).debug(ArtefactoKey.LOG_TRASPASADOS, 3);
    }

    @Test
    void debeNoCambiarNada_cuandoYaSonDelAsesorOElProyectoNoTieneArtefactos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var id = sembrar(proyecto, asesor);
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.traspasarAsesorDeProyecto(proyecto, asesor);
        adapter.traspasarAsesorDeProyecto(UtilUUID.generarNuevoUUID(), asesor);
        entityManager.clear();

        // Assert
        assertThat(entityManager.find(ArtefactoJpaEntity.class, id).getAsesor()).isEqualTo(asesor);
        verify(logger, times(2)).debug(ArtefactoKey.LOG_TRASPASADOS, 0);
    }

    @Test
    void debeObtenerBloqueadoPorId_cuandoExiste() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var id = sembrar(proyecto, asesor);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerBloqueadoPorId(id);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().id()).isEqualTo(id);
        assertThat(resultado.get().asesor()).isEqualTo(asesor);
    }

    @Test
    void debeRetornarVacio_cuandoElArtefactoBloqueadoNoExiste() {
        // Act & Assert
        assertThat(adapter.obtenerBloqueadoPorId(UtilUUID.generarNuevoUUID())).isEmpty();
    }

    private UUID sembrar(UUID proyecto, UUID asesor) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(ArtefactoJpaEntity.builder()
                .id(id)
                .proyectoGrado(proyecto)
                .versionRepositorioArtefacto(UtilUUID.generarNuevoUUID())
                .asesor(asesor)
                .build());
        return id;
    }

    @Test
    void debeObtenerPorId_cuandoElArtefactoExiste() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var id = sembrar(proyecto, asesor);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerPorId(id);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().id()).isEqualTo(id);
        assertThat(resultado.get().proyectoGrado()).isEqualTo(proyecto);
        assertThat(resultado.get().asesor()).isEqualTo(asesor);
    }

    @Test
    void debeDevolverVacio_cuandoElArtefactoNoExiste() {
        // Act
        var resultado = adapter.obtenerPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeCambiarElAsesorSoloDelArtefactoIndicado_cuandoHayOtrosDelMismoProyecto() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var anterior = UtilUUID.generarNuevoUUID();
        var nuevo = UtilUUID.generarNuevoUUID();
        var objetivo = sembrar(proyecto, anterior);
        var hermano = sembrar(proyecto, anterior);
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.cambiarAsesor(objetivo, nuevo);
        entityManager.clear();

        // Assert
        assertThat(entityManager.find(ArtefactoJpaEntity.class, objetivo).getAsesor()).isEqualTo(nuevo);
        assertThat(entityManager.find(ArtefactoJpaEntity.class, hermano).getAsesor()).isEqualTo(anterior);
        verify(logger).debug(ArtefactoKey.LOG_ASESOR_ACTUALIZADO, objetivo);
    }

    @Test
    void debeObtenerPorVersionArtefacto_cuandoLaVersionPerteneceAlArtefacto() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var objetivo = sembrar(UtilUUID.generarNuevoUUID(), asesor);
        var otro = sembrar(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var version = UtilUUID.generarNuevoUUID();
        entityManager.persist(new VersionArtefactoTestEntity(version, objetivo, 2));
        entityManager.persist(new VersionArtefactoTestEntity(UtilUUID.generarNuevoUUID(), otro, 1));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerPorVersionArtefacto(version);

        // Assert
        assertThat(resultado).hasValueSatisfying(entity -> {
            assertThat(entity.id()).isEqualTo(objetivo);
            assertThat(entity.asesor()).isEqualTo(asesor);
        });
    }

    @Test
    void debeDevolverVacio_cuandoLaVersionNoExiste() {
        // Act & Assert
        assertThat(adapter.obtenerPorVersionArtefacto(UtilUUID.generarNuevoUUID())).isEmpty();
    }

    @Test
    void debeOmitirSoloElArtefactoIndicado_yLoguear() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var objetivo = sembrar(proyecto, asesor);
        var hermano = sembrar(proyecto, asesor);
        entityManager.flush();
        entityManager.clear();
        var omitidoEn = Instant.parse("2026-10-29T10:00:00Z");

        // Act
        adapter.omitir(objetivo, omitidoEn);
        entityManager.clear();

        // Assert
        assertThat(entityManager.find(ArtefactoJpaEntity.class, objetivo).getOmitidoEn()).isEqualTo(omitidoEn);
        assertThat(entityManager.find(ArtefactoJpaEntity.class, hermano).getOmitidoEn()).isNull();
        verify(logger).debug(ArtefactoKey.LOG_OMITIDO_ACTUALIZADO, objetivo);
    }

    @Test
    void debeLimpiarLaOmisionYCambiarElAsesor_cuandoSeReactiva() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var anterior = UtilUUID.generarNuevoUUID();
        var nuevo = UtilUUID.generarNuevoUUID();
        var objetivo = sembrarOmitido(proyecto, UtilUUID.generarNuevoUUID(), anterior);
        var hermano = sembrarOmitido(proyecto, UtilUUID.generarNuevoUUID(), anterior);
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.reactivar(objetivo, nuevo);
        entityManager.clear();

        // Assert
        var reactivado = entityManager.find(ArtefactoJpaEntity.class, objetivo);
        assertThat(reactivado.getOmitidoEn()).isNull();
        assertThat(reactivado.getAsesor()).isEqualTo(nuevo);
        var sinTocar = entityManager.find(ArtefactoJpaEntity.class, hermano);
        assertThat(sinTocar.getOmitidoEn()).isNotNull();
        assertThat(sinTocar.getAsesor()).isEqualTo(anterior);
        verify(logger).debug(ArtefactoKey.LOG_REACTIVADO_ACTUALIZADO, objetivo);
    }

    @Test
    void debeNoTocarLosArtefactosOmitidos_cuandoSeTraspasaElAsesorDelProyecto() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var anterior = UtilUUID.generarNuevoUUID();
        var nuevo = UtilUUID.generarNuevoUUID();
        var activo = sembrar(proyecto, anterior);
        var omitido = sembrarOmitido(proyecto, UtilUUID.generarNuevoUUID(), anterior);
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.traspasarAsesorDeProyecto(proyecto, nuevo);
        entityManager.clear();

        // Assert
        assertThat(entityManager.find(ArtefactoJpaEntity.class, activo).getAsesor()).isEqualTo(nuevo);
        assertThat(entityManager.find(ArtefactoJpaEntity.class, omitido).getAsesor()).isEqualTo(anterior);
        verify(logger).debug(ArtefactoKey.LOG_TRASPASADOS, 1);
    }

    @Test
    void debeObtenerElOmitidoDelMismoRepositorio_cuandoLaVersionPedidaEsOtraDelMismoRepositorio() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var repositorio = UtilUUID.generarNuevoUUID();
        var versionOmitida = sembrarVersionRepositorio(repositorio);
        var versionNueva = sembrarVersionRepositorio(repositorio);
        var versionOtroRepositorio = sembrarVersionRepositorio(UtilUUID.generarNuevoUUID());
        var omitido = sembrarOmitido(proyecto, versionOmitida, UtilUUID.generarNuevoUUID());
        sembrarOmitido(UtilUUID.generarNuevoUUID(), versionOmitida, UtilUUID.generarNuevoUUID());
        sembrarOmitido(proyecto, versionOtroRepositorio, UtilUUID.generarNuevoUUID());
        entityManager.flush();
        entityManager.clear();

        // Act
        var encontrado = adapter.obtenerOmitidoPorProyectoYRepositorio(proyecto, versionNueva);

        // Assert
        assertThat(encontrado).hasValueSatisfying(entity -> {
            assertThat(entity.id()).isEqualTo(omitido);
            assertThat(entity.omitidoEn()).isNotNull();
        });
    }

    @Test
    void debeDevolverVacio_cuandoElRepositorioSoloTieneArtefactosActivosOEnOtroProyecto() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var repositorio = UtilUUID.generarNuevoUUID();
        var versionActiva = sembrarVersionRepositorio(repositorio);
        var versionNueva = sembrarVersionRepositorio(repositorio);
        sembrarArtefactoDeVersion(proyecto, versionActiva);
        sembrarOmitido(UtilUUID.generarNuevoUUID(), versionActiva, UtilUUID.generarNuevoUUID());
        entityManager.flush();
        entityManager.clear();

        // Act & Assert
        assertThat(adapter.obtenerOmitidoPorProyectoYRepositorio(proyecto, versionNueva)).isEmpty();
    }

    @Test
    void debeObtenerPorRevisionAsesor_cuandoLaRevisionPerteneceAlArtefacto() {
        // Arrange
        var objetivo = sembrar(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var otro = sembrar(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var version = UtilUUID.generarNuevoUUID();
        var revision = UtilUUID.generarNuevoUUID();
        entityManager.persist(new VersionArtefactoTestEntity(version, objetivo, 1));
        entityManager.persist(new VersionArtefactoTestEntity(UtilUUID.generarNuevoUUID(), otro, 1));
        entityManager.persist(new RevisionAsesorTestEntity(revision, version, "PENDIENTE"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var encontrado = adapter.obtenerPorRevisionAsesor(revision);

        // Assert
        assertThat(encontrado).hasValueSatisfying(entity -> assertThat(entity.id()).isEqualTo(objetivo));
        assertThat(adapter.obtenerPorRevisionAsesor(UtilUUID.generarNuevoUUID())).isEmpty();
    }

    private UUID sembrarOmitido(UUID proyecto, UUID version, UUID asesor) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ArtefactoJpaEntity(id, proyecto, version, asesor, Instant.now()));
        return id;
    }

    private UUID sembrarArtefactoDeVersion(UUID proyecto, UUID version) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ArtefactoJpaEntity(id, proyecto, version, UtilUUID.generarNuevoUUID(), null));
        return id;
    }

    private UUID sembrarVersionRepositorio(UUID repositorio) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new VersionRepositorioArtefactoJpaEntity(
                id, "Documento", 1, repositorio, "Repositorio", "archivo.pdf", "application/pdf", 10L));
        return id;
    }
}
