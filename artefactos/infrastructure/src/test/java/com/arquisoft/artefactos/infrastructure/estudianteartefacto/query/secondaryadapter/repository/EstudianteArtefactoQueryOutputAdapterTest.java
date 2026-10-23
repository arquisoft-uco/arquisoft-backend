package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.estudianteartefacto.support.EstudianteTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.ArtefactoTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.EstudianteArtefactoTestEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstudianteArtefactoQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EstudianteArtefactoQueryRepository repository;

    private EstudianteArtefactoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstudianteArtefactoQueryOutputAdapter(repository);
    }

    private UUID sembrarArtefacto() {
        var artefacto = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ArtefactoTestEntity(artefacto));
        return artefacto;
    }

    private UUID vincular(UUID artefacto, String nombre, Instant eliminadoEn) {
        var estudiante = UtilUUID.generarNuevoUUID();
        entityManager.persist(new EstudianteTestEntity(
                estudiante, "ID-" + nombre, nombre, nombre.toLowerCase() + "@uco.edu.co", eliminadoEn));
        entityManager.persist(new EstudianteArtefactoTestEntity(UtilUUID.generarNuevoUUID(), artefacto, estudiante));
        return estudiante;
    }

    @Test
    void debeRetornarOrdenadosPorNombreYId_cuandoHayVarios() {
        // Arrange
        var artefacto = sembrarArtefacto();
        vincular(artefacto, "Zoe", null);
        vincular(artefacto, "Ana", null);
        vincular(artefacto, "Luis", null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorArtefacto(artefacto);

        // Assert
        assertThat(resultado).extracting("nombre").containsExactly("Ana", "Luis", "Zoe");
    }

    @Test
    void debeDesempatarPorId_cuandoDosEstudiantesTienenElMismoNombre() {
        // Arrange
        var artefacto = sembrarArtefacto();
        var primero = vincular(artefacto, "Ana", null);
        var segundo = vincular(artefacto, "Ana", null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorArtefacto(artefacto);

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).id().compareTo(resultado.get(1).id())).isNegative();
        assertThat(resultado).extracting("estudianteId").containsExactlyInAnyOrder(primero, segundo);
    }

    @Test
    void debeIncluirBajaConVigenteFalse_cuandoEstudianteTieneEliminadoEn() {
        // Arrange
        var artefacto = sembrarArtefacto();
        var baja = vincular(artefacto, "Ana", Instant.parse("2026-01-01T00:00:00Z"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorArtefacto(artefacto);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).estudianteId()).isEqualTo(baja);
        assertThat(resultado.get(0).vigente()).isFalse();
    }

    @Test
    void debeMarcarVigenteTrue_cuandoNoTieneEliminadoEn() {
        // Arrange
        var artefacto = sembrarArtefacto();
        var vigente = vincular(artefacto, "Ana", null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorArtefacto(artefacto);

        // Assert
        assertThat(resultado).hasSize(1);
        var readModel = resultado.get(0);
        assertThat(readModel.vigente()).isTrue();
        assertThat(readModel.artefactoId()).isEqualTo(artefacto);
        assertThat(readModel.estudianteId()).isEqualTo(vigente);
        assertThat(readModel.identificador()).isEqualTo("ID-Ana");
        assertThat(readModel.nombre()).isEqualTo("Ana");
        assertThat(readModel.email()).isEqualTo("ana@uco.edu.co");
    }

    @Test
    void debeExcluirVinculosDeOtroArtefacto_cuandoConsulta() {
        // Arrange
        var artefacto = sembrarArtefacto();
        var otro = sembrarArtefacto();
        var propio = vincular(artefacto, "Ana", null);
        vincular(otro, "Luis", null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorArtefacto(artefacto);

        // Assert
        assertThat(resultado).extracting("estudianteId").containsExactly(propio);
    }

    @Test
    void debeRetornarVacio_cuandoArtefactoSinVinculosOInexistente() {
        // Arrange
        var sinVinculos = sembrarArtefacto();
        entityManager.flush();

        // Act
        var deSinVinculos = adapter.consultarPorArtefacto(sinVinculos);
        var deInexistente = adapter.consultarPorArtefacto(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(deSinVinculos).isEmpty();
        assertThat(deInexistente).isEmpty();
    }
}
