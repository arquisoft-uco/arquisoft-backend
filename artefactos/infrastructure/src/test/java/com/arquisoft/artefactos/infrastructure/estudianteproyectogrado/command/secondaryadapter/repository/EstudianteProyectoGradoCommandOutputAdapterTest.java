package com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.entity.PertenenciaProyectoGradoEntity;
import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
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

@DataJpaTest
class EstudianteProyectoGradoCommandOutputAdapterTest {

    @Autowired
    private EstudianteProyectoGradoCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private EstudianteProyectoGradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstudianteProyectoGradoCommandOutputAdapter(repository);
    }

    @Test
    void debeDevolverUnaPertenenciaPorArtefactoDelProyecto_cuandoElVinculoEstaVigente() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var primero = sembrarArtefacto(proyecto);
        var segundo = sembrarArtefacto(proyecto);
        var tercero = sembrarArtefacto(proyecto);
        sembrarVinculo(estudiante, proyecto, null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerPertenenciasVigentes(List.of(primero, segundo, tercero), List.of(estudiante));

        // Assert
        assertThat(resultado).containsExactlyInAnyOrder(
                new PertenenciaProyectoGradoEntity(primero, estudiante),
                new PertenenciaProyectoGradoEntity(segundo, estudiante),
                new PertenenciaProyectoGradoEntity(tercero, estudiante));
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
        sembrarVinculo(vigente, proyecto, null);
        sembrarVinculo(destituido, proyecto, Instant.now());
        sembrarVinculo(deOtroProyecto, otroProyecto, null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerPertenenciasVigentes(
                List.of(artefacto), List.of(vigente, destituido, deOtroProyecto, sinVinculo));

        // Assert
        assertThat(resultado).containsExactly(new PertenenciaProyectoGradoEntity(artefacto, vigente));
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
