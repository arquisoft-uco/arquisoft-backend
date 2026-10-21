package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.criteria.EvaluacionCualitativaAsesorCriteria;
import com.arquisoft.shared.util.UtilUUID;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// Ninguna de las tres tablas tiene @Entity en el lado de comando, asi que Hibernate no las genera
// con ddl-auto. Se crean aqui con el DDL de V20261020233051 para ejercitar el @Subselect real.
@DataJpaTest
class EvaluacionCualitativaAsesorQueryOutputAdapterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EvaluacionCualitativaAsesorQueryRepository repository;

    private EvaluacionCualitativaAsesorQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionCualitativaAsesorQueryOutputAdapter(repository);
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS item_cualitativo_asesor (
                    id UUID NOT NULL, nombre VARCHAR(100) NOT NULL,
                    descripcion VARCHAR(300) NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS criterio_item_cualitativo_asesor (
                    id UUID NOT NULL, nombre VARCHAR(100) NOT NULL,
                    descripcion VARCHAR(300) NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS evaluacion_cualitativa_asesor (
                    id UUID NOT NULL, evaluacion_asesor_id UUID NOT NULL,
                    item_id UUID NOT NULL, criterio_id UUID NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.flush();
    }

    private void sembrarItem(UUID id, String nombre, String descripcion) {
        entityManager.createNativeQuery(
                        "INSERT INTO item_cualitativo_asesor (id, nombre, descripcion) VALUES (?, ?, ?)")
                .setParameter(1, id).setParameter(2, nombre).setParameter(3, descripcion)
                .executeUpdate();
    }

    private void sembrarCriterio(UUID id, String nombre, String descripcion) {
        entityManager.createNativeQuery(
                        "INSERT INTO criterio_item_cualitativo_asesor (id, nombre, descripcion) VALUES (?, ?, ?)")
                .setParameter(1, id).setParameter(2, nombre).setParameter(3, descripcion)
                .executeUpdate();
    }

    private void sembrarEvaluacionCualitativa(UUID id, UUID evaluacionAsesor, UUID item, UUID criterio) {
        entityManager.createNativeQuery("""
                        INSERT INTO evaluacion_cualitativa_asesor (id, evaluacion_asesor_id, item_id, criterio_id)
                        VALUES (?, ?, ?, ?)
                        """)
                .setParameter(1, id).setParameter(2, evaluacionAsesor)
                .setParameter(3, item).setParameter(4, criterio)
                .executeUpdate();
    }

    @Test
    void debeRetornarLasCualitativasDeLaEvaluacion_conItemYCriterioMapeadosYSinLasDeOtras() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var otraEvaluacion = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        var criterio = UtilUUID.generarNuevoUUID();
        var cualitativa = UtilUUID.generarNuevoUUID();
        sembrarItem(item, "Claridad", "Evalua la claridad");
        sembrarCriterio(criterio, "Excelente", "Cumple todo");
        sembrarEvaluacionCualitativa(cualitativa, evaluacionAsesor, item, criterio);
        sembrarEvaluacionCualitativa(UtilUUID.generarNuevoUUID(), otraEvaluacion, item, criterio);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultar(new EvaluacionCualitativaAsesorCriteria(
                evaluacionAsesor, UtilUUID.generarNuevoUUID()));

        // Assert
        assertThat(resultado).hasSize(1);
        var readModel = resultado.get(0);
        assertThat(readModel.id()).isEqualTo(cualitativa);
        assertThat(readModel.item().id()).isEqualTo(item);
        assertThat(readModel.item().nombre()).isEqualTo("Claridad");
        assertThat(readModel.item().descripcion()).isEqualTo("Evalua la claridad");
        assertThat(readModel.criterio().id()).isEqualTo(criterio);
        assertThat(readModel.criterio().nombre()).isEqualTo("Excelente");
        assertThat(readModel.criterio().descripcion()).isEqualTo("Cumple todo");
    }

    @Test
    void debeOrdenarPorNombreDelItem_cuandoHayVariasCualitativas() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var itemRigor = UtilUUID.generarNuevoUUID();
        var itemClaridad = UtilUUID.generarNuevoUUID();
        var criterio = UtilUUID.generarNuevoUUID();
        sembrarItem(itemRigor, "Rigor", "desc");
        sembrarItem(itemClaridad, "Claridad", "desc");
        sembrarCriterio(criterio, "Excelente", "desc");
        sembrarEvaluacionCualitativa(UtilUUID.generarNuevoUUID(), evaluacionAsesor, itemRigor, criterio);
        sembrarEvaluacionCualitativa(UtilUUID.generarNuevoUUID(), evaluacionAsesor, itemClaridad, criterio);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultar(new EvaluacionCualitativaAsesorCriteria(
                evaluacionAsesor, UtilUUID.generarNuevoUUID()));

        // Assert
        assertThat(resultado).extracting(r -> r.item().nombre()).containsExactly("Claridad", "Rigor");
    }

    @Test
    void debeRetornarListaVacia_cuandoLaEvaluacionNoTieneCualitativas() {
        // Act
        var resultado = adapter.consultar(new EvaluacionCualitativaAsesorCriteria(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID()));

        // Assert
        assertThat(resultado).isEmpty();
    }
}
