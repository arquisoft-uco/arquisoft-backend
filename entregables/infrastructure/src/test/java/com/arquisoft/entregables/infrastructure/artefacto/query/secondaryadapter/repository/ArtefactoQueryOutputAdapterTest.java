package com.arquisoft.entregables.infrastructure.artefacto.query.secondaryadapter.repository;

import com.arquisoft.entregables.application.artefacto.query.readmodel.ArtefactoVistaPreviaReadModel;
import com.arquisoft.entregables.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
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
        var vigente = sembrar(proyecto, "Anteproyecto", 2, null);
        sembrar(proyecto, "Informe", 1, T0);
        sembrar(UtilUUID.generarNuevoUUID(), "Ajeno", 1, null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarVigentesPorProyecto(proyecto);

        // Assert
        assertThat(resultado).containsExactly(new ArtefactoVistaPreviaReadModel(vigente, "Anteproyecto", 2));
    }

    @Test
    void debeOrdenarPorNombreDeDocumento_cuandoHayVariosVigentes() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
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
        assertThat(resultado).extracting(ArtefactoVistaPreviaReadModel::version).containsExactly(3, 2, 1);
    }
}
