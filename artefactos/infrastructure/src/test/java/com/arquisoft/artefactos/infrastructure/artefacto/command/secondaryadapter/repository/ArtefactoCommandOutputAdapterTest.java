package com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.artefacto.command.secondaryport.entity.ArtefactoEntity;
import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

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
    void debeGuardarTodosYLeerDeVuelta_cuandoSePersisteElLote() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var primero = new ArtefactoEntity(
                UtilUUID.generarNuevoUUID(), proyecto, UtilUUID.generarNuevoUUID(), asesor);
        var segundo = new ArtefactoEntity(
                UtilUUID.generarNuevoUUID(), proyecto, UtilUUID.generarNuevoUUID(), asesor);

        // Act
        adapter.registrarTodos(List.of(primero, segundo));
        entityManager.flush();
        entityManager.clear();

        // Assert
        var guardado = entityManager.find(ArtefactoJpaEntity.class, segundo.id());
        assertThat(guardado.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(guardado.getVersionRepositorioArtefacto()).isEqualTo(segundo.versionRepositorioArtefacto());
        assertThat(guardado.getAsesor()).isEqualTo(asesor);
        assertThat(entityManager.find(ArtefactoJpaEntity.class, primero.id())).isNotNull();
        verify(logger).debug(ArtefactoKey.LOG_GUARDADOS, 2);
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
    void debeObtenerPorId_cuandoExiste() {
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
    void debeRetornarVacio_cuandoElArtefactoNoExiste() {
        // Act & Assert
        assertThat(adapter.obtenerPorId(UtilUUID.generarNuevoUUID())).isEmpty();
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

}
