package com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.command.secondaryadapter.entity.CriterioItemCualitativoAsesorJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CriterioItemCualitativoAsesorQueryOutputAdapterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CriterioItemCualitativoAsesorQueryRepository repository;

    private CriterioItemCualitativoAsesorQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new CriterioItemCualitativoAsesorQueryOutputAdapter(repository);
    }

    private UUID sembrarCriterio(String nombre) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(CriterioItemCualitativoAsesorJpaEntity.builder()
                .id(id)
                .nombre(nombre)
                .descripcion("desc " + nombre)
                .build());
        return id;
    }

    @Test
    void debeRetornarCriteriosOrdenadosPorNombre_cuandoExisten() {
        // Arrange
        var zeta = sembrarCriterio("Zeta");
        var beta = sembrarCriterio("Beta");
        var alfa = sembrarCriterio("Alfa");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).extracting("id").containsExactly(alfa, beta, zeta);
        var primero = resultado.get(0);
        assertThat(primero.nombre()).isEqualTo("Alfa");
        assertThat(primero.descripcion()).isEqualTo("desc Alfa");
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayCriterios() {
        // Act
        var resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).isEmpty();
    }
}
