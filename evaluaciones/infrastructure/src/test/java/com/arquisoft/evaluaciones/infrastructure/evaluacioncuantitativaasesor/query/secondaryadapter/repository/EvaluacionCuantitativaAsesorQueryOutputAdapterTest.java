package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.criteria.EvaluacionCuantitativaAsesorCriteria;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.secondaryadapter.entity.EvaluacionCuantitativaAsesorJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.secondaryadapter.entity.ItemCuantitativoAsesorJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EvaluacionCuantitativaAsesorQueryOutputAdapterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EvaluacionCuantitativaAsesorQueryRepository repository;

    private EvaluacionCuantitativaAsesorQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionCuantitativaAsesorQueryOutputAdapter(repository);
    }

    private UUID sembrarItem(String nombre, String descripcion, UUID categoria, int valor) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(ItemCuantitativoAsesorJpaEntity.builder()
                .id(id).nombre(nombre).descripcion(descripcion).categoria(categoria).valor(valor).build());
        return id;
    }

    private UUID sembrarCuantitativa(UUID evaluacionAsesor, UUID item, int puntaje) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(EvaluacionCuantitativaAsesorJpaEntity.builder()
                .id(id).evaluacionAsesor(evaluacionAsesor).item(item).puntaje(puntaje).build());
        return id;
    }

    private EvaluacionCuantitativaAsesorCriteria criteria(UUID evaluacionAsesor) {
        return new EvaluacionCuantitativaAsesorCriteria(evaluacionAsesor, UtilUUID.generarNuevoUUID());
    }

    @Test
    void debeRetornarLasCuantitativasDeLaEvaluacion_conItemMapeadoYSinLasDeOtras() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var categoria = UtilUUID.generarNuevoUUID();
        var item = sembrarItem("Claridad", "Evalua la claridad", categoria, 10);
        var cuantitativa = sembrarCuantitativa(evaluacionAsesor, item, 4);
        sembrarCuantitativa(UtilUUID.generarNuevoUUID(), item, 2);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultar(criteria(evaluacionAsesor));

        // Assert
        assertThat(resultado).hasSize(1);
        var readModel = resultado.get(0);
        assertThat(readModel.id()).isEqualTo(cuantitativa);
        assertThat(readModel.puntaje()).isEqualTo(4);
        assertThat(readModel.item().id()).isEqualTo(item);
        assertThat(readModel.item().nombre()).isEqualTo("Claridad");
        assertThat(readModel.item().descripcion()).isEqualTo("Evalua la claridad");
        assertThat(readModel.item().categoriaId()).isEqualTo(categoria);
        assertThat(readModel.item().valor()).isEqualTo(10);
    }

    @Test
    void debeOrdenarPorNombreDelItem_cuandoHayVariasCuantitativas() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var categoria = UtilUUID.generarNuevoUUID();
        var itemRigor = sembrarItem("Rigor", "desc", categoria, 5);
        var itemClaridad = sembrarItem("Claridad", "desc", categoria, 5);
        var itemAportes = sembrarItem("Aportes", "desc", categoria, 5);
        sembrarCuantitativa(evaluacionAsesor, itemRigor, 3);
        sembrarCuantitativa(evaluacionAsesor, itemClaridad, 4);
        sembrarCuantitativa(evaluacionAsesor, itemAportes, 5);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultar(criteria(evaluacionAsesor));

        // Assert
        assertThat(resultado).extracting(r -> r.item().nombre()).containsExactly("Aportes", "Claridad", "Rigor");
    }

    @Test
    void debeConsultarPorEvaluacionAsesor_ordenadoPorNombreDeItemEId() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var categoria = UtilUUID.generarNuevoUUID();
        var itemRigor = sembrarItem("Rigor", "desc", categoria, 5);
        var itemAportes = sembrarItem("Aportes", "desc", categoria, 5);
        sembrarCuantitativa(evaluacionAsesor, itemRigor, 3);
        sembrarCuantitativa(evaluacionAsesor, itemAportes, 5);
        sembrarCuantitativa(UtilUUID.generarNuevoUUID(), itemAportes, 1);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorEvaluacionAsesor(evaluacionAsesor);

        // Assert
        assertThat(resultado).extracting(r -> r.item().nombre()).containsExactly("Aportes", "Rigor");
    }

    @Test
    void debeDevolverVacio_cuandoNoHayCuantitativas() {
        // Act
        var resultado = adapter.consultarPorEvaluacionAsesor(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRetornarListaVacia_cuandoLaEvaluacionNoTieneCuantitativas() {
        // Act
        var resultado = adapter.consultar(criteria(UtilUUID.generarNuevoUUID()));

        // Assert
        assertThat(resultado).isEmpty();
    }
}
