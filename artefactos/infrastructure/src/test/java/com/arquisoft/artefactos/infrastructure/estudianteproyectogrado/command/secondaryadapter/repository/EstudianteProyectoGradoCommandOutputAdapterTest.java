package com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.shared.message.key.artefactos.EstudianteProyectoGradoKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
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
import static org.mockito.Mockito.verify;

@DataJpaTest
class EstudianteProyectoGradoCommandOutputAdapterTest {

    @Autowired
    private EstudianteProyectoGradoCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private EstudianteProyectoGradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstudianteProyectoGradoCommandOutputAdapter(repository, mock(AppLogger.class));
    }

    @Test
    void debeDevolverElEstudiante_cuandoTieneVinculoVigenteConElProyectoDelArtefacto() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var artefacto = sembrarArtefacto(proyecto);
        sembrarVinculo(estudiante, proyecto, null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerEstudiantesVigentesDelArtefacto(artefacto, List.of(estudiante));

        // Assert
        assertThat(resultado).containsExactly(estudiante);
    }

    @Test
    void debeExcluirDestituidosYVinculosDeOtroProyecto_cuandoHayOtrasFilas() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var otroProyecto = UtilUUID.generarNuevoUUID();
        var artefacto = sembrarArtefacto(proyecto);
        var vigente = UtilUUID.generarNuevoUUID();
        var destituido = UtilUUID.generarNuevoUUID();
        var deOtroProyecto = UtilUUID.generarNuevoUUID();
        var sinVinculo = UtilUUID.generarNuevoUUID();
        var vigenteNoPedido = UtilUUID.generarNuevoUUID();
        sembrarVinculo(vigente, proyecto, null);
        sembrarVinculo(destituido, proyecto, Instant.now());
        sembrarVinculo(vigenteNoPedido, proyecto, null);
        sembrarVinculo(deOtroProyecto, otroProyecto, null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerEstudiantesVigentesDelArtefacto(
                artefacto, List.of(vigente, destituido, deOtroProyecto, sinVinculo));

        // Assert
        assertThat(resultado).containsExactly(vigente);
    }

    @Test
    void debeFiltrarPorProyectoYEstudiantesPedidos_cuandoHayOtrosVinculos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var pedido = UtilUUID.generarNuevoUUID();
        var destituido = UtilUUID.generarNuevoUUID();
        var noPedido = UtilUUID.generarNuevoUUID();
        var deOtroProyecto = UtilUUID.generarNuevoUUID();
        sembrarVinculo(pedido, proyecto, null);
        sembrarVinculo(destituido, proyecto, Instant.parse("2026-10-02T10:00:00Z"));
        sembrarVinculo(noPedido, proyecto, null);
        sembrarVinculo(deOtroProyecto, UtilUUID.generarNuevoUUID(), null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerPorProyectoGradoYEstudiantes(
                proyecto, List.of(pedido, destituido, deOtroProyecto));

        // Assert
        assertThat(resultado).extracting(EstudianteProyectoGradoEntity::estudiante)
                .containsExactlyInAnyOrder(pedido, destituido);
        assertThat(resultado).filteredOn(e -> e.estudiante().equals(destituido)).singleElement()
                .satisfies(e -> assertThat(e.destituidoEn()).isEqualTo(Instant.parse("2026-10-02T10:00:00Z")));
        assertThat(resultado).filteredOn(e -> e.estudiante().equals(pedido)).singleElement()
                .satisfies(e -> assertThat(e.destituidoEn()).isEqualTo(UtilFecha.VACIO));
    }

    @Test
    void debeGuardarYRecuperarElVinculo_cuandoSeGuardaUnoYVarios() {
        // Arrange
        var logger = mock(AppLogger.class);
        var conLogger = new EstudianteProyectoGradoCommandOutputAdapter(repository, logger);
        var proyecto = UtilUUID.generarNuevoUUID();
        var primero = new EstudianteProyectoGradoEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                proyecto, Instant.parse("2026-10-01T10:00:00Z"), UtilFecha.VACIO);
        var segundo = new EstudianteProyectoGradoEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                proyecto, Instant.parse("2026-10-02T10:00:00Z"), Instant.parse("2026-10-02T10:00:00Z"));
        var tercero = new EstudianteProyectoGradoEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                proyecto, Instant.parse("2026-10-03T10:00:00Z"), UtilFecha.VACIO);

        // Act
        conLogger.guardar(primero);
        conLogger.guardarTodos(List.of(segundo, tercero));
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(conLogger.obtenerPorEstudianteYProyectoGrado(primero.estudiante(), proyecto)).contains(primero);
        assertThat(conLogger.obtenerPorEstudianteYProyectoGrado(segundo.estudiante(), proyecto)).contains(segundo);
        assertThat(conLogger.obtenerPorEstudianteYProyectoGrado(
                UtilUUID.generarNuevoUUID(), proyecto)).isEmpty();
        verify(logger).debug(EstudianteProyectoGradoKey.LOG_GUARDADO, primero.id());
        verify(logger).debug(EstudianteProyectoGradoKey.LOG_GUARDADOS, 2);
    }

    private UUID sembrarArtefacto(UUID proyecto) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(ArtefactoJpaEntity.builder()
                .id(id)
                .proyectoGrado(proyecto)
                .versionRepositorioArtefacto(UtilUUID.generarNuevoUUID())
                .asesor(UtilUUID.generarNuevoUUID())
                .build());
        return id;
    }

    private void sembrarVinculo(UUID estudiante, UUID proyecto, Instant destituidoEn) {
        entityManager.persist(EstudianteProyectoGradoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .estudianteId(estudiante)
                .proyectoGradoId(proyecto)
                .ocurridoEn(Instant.now())
                .destituidoEn(destituidoEn)
                .build());
    }
}
