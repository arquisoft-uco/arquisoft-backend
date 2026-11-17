package com.arquisoft.entregables.infrastructure.artefacto.query.secondaryadapter.repository;

import com.arquisoft.entregables.application.artefacto.query.readmodel.ArtefactoVistaPreviaReadModel;
import com.arquisoft.entregables.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.entregables.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ArtefactoQueryOutputAdapter.class)
class ArtefactoQueryOutputAdapterTest {

    private static final Instant T0 = Instant.parse("2026-11-10T10:00:00Z");

    @Autowired
    private ArtefactoQueryOutputAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    private void sembrarProyecto(UUID proyecto, String estado) {
        entityManager.persist(ProyectoGradoJpaEntity.builder()
                .id(proyecto).nombre("Proyecto").estadoProyectoId(estado).build());
    }

    private UUID sembrar(UUID proyecto, String documento, int numeroVersion, Instant omitidoEn) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(ArtefactoJpaEntity.builder()
                .id(id).proyectoGrado(proyecto).versionArtefacto(UtilUUID.generarNuevoUUID())
                .numeroVersion(numeroVersion).fechaVersion(T0).claveObjeto("clave/" + id)
                .versionRepositorioArtefacto(UtilUUID.generarNuevoUUID()).nombreDocumento(documento)
                .repositorio(UtilUUID.generarNuevoUUID()).nombreRepositorio("Repo")
                .omitidoEn(omitidoEn).omisionOcurridaEn(omitidoEn).build());
        return id;
    }

    @Test
    void debeExcluirLosOmitidosYLosDeOtrosProyectos_cuandoSeConsultaLaVistaPrevia() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var otro = UtilUUID.generarNuevoUUID();
        sembrarProyecto(proyecto, "EN_PROCESO");
        sembrarProyecto(otro, "EN_PROCESO");
        var vigente = sembrar(proyecto, "Anteproyecto", 2, null);
        sembrar(proyecto, "Informe", 1, T0);
        sembrar(otro, "Ajeno", 1, null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarVigentesPorProyecto(proyecto);

        // Assert
        assertThat(resultado).containsExactly(new ArtefactoVistaPreviaReadModel(vigente, "Anteproyecto", 2));
    }

    @Test
    void noDebeListarLasLapidas_cuandoElProyectoTieneVersionesYLapidas() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        sembrarProyecto(proyecto, "EN_PROCESO");
        var vigente = sembrar(proyecto, "Anteproyecto", 1, null);
        entityManager.persist(ArtefactoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).proyectoGrado(proyecto).omisionOcurridaEn(T0).build());
        entityManager.persist(ArtefactoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).proyectoGrado(proyecto).omitidoEn(T0).omisionOcurridaEn(T0).build());
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarVigentesPorProyecto(proyecto);

        // Assert
        assertThat(resultado).extracting(ArtefactoVistaPreviaReadModel::artefacto).containsExactly(vigente);
    }

    @Test
    void debeDevolverVacio_cuandoElProyectoEstaFinalizadoOCancelado() {
        // Arrange
        var finalizado = UtilUUID.generarNuevoUUID();
        var cancelado = UtilUUID.generarNuevoUUID();
        sembrarProyecto(finalizado, "FINALIZADO");
        sembrarProyecto(cancelado, "CANCELADO");
        sembrar(finalizado, "Informe", 1, null);
        sembrar(cancelado, "Manual", 1, null);
        entityManager.flush();
        entityManager.clear();

        // Act & Assert
        assertThat(adapter.consultarVigentesPorProyecto(finalizado)).isEmpty();
        assertThat(adapter.consultarVigentesPorProyecto(cancelado)).isEmpty();
    }

    @Test
    void debeOrdenarPorNombreDeDocumento_cuandoHayVariosVigentes() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        sembrarProyecto(proyecto, "ATRASADO");
        sembrar(proyecto, "Manual", 1, null);
        sembrar(proyecto, "Anteproyecto", 3, null);
        sembrar(proyecto, "Informe", 2, null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarVigentesPorProyecto(proyecto);

        // Assert
        assertThat(resultado).extracting(ArtefactoVistaPreviaReadModel::documento)
                .containsExactly("Anteproyecto", "Informe", "Manual");
        assertThat(resultado).extracting(ArtefactoVistaPreviaReadModel::numeroVersion).containsExactly(3, 2, 1);
    }
}
