package com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.secondaryadapter.entity.ItemCualitativoAsesorJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemCualitativoAsesorQueryOutputAdapterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ItemCualitativoAsesorQueryRepository repository;

    private ItemCualitativoAsesorQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ItemCualitativoAsesorQueryOutputAdapter(repository);
    }

    private UUID sembrarItem(String nombre) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(ItemCualitativoAsesorJpaEntity.builder()
                .id(id)
                .nombre(nombre)
                .descripcion("desc " + nombre)
                .build());
        return id;
    }

    @Test
    void debeRetornarItemsOrdenadosPorNombre_cuandoExisten() {
        // Arrange
        var zeta = sembrarItem("Zeta");
        var beta = sembrarItem("Beta");
        var alfa = sembrarItem("Alfa");
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
    void debeRetornarListaVacia_cuandoNoHayItems() {
        // Act
        var resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).isEmpty();
    }
}
