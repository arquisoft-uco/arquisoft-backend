package com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.query.secondaryadapter.repository;

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
class ItemCuantitativoAsesorQueryOutputAdapterTest {

    private static final UUID CATEGORIA_UNO = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID CATEGORIA_DOS = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ItemCuantitativoAsesorQueryRepository repository;

    private ItemCuantitativoAsesorQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ItemCuantitativoAsesorQueryOutputAdapter(repository);
    }

    private UUID sembrarItem(String nombre, UUID categoria, int valor) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(ItemCuantitativoAsesorJpaEntity.builder()
                .id(id)
                .nombre(nombre)
                .descripcion("desc " + nombre)
                .categoria(categoria)
                .valor(valor)
                .build());
        return id;
    }

    @Test
    void debeRetornarItemsOrdenadosPorCategoriaYNombre_cuandoExisten() {
        // Arrange
        var zetaDos = sembrarItem("Zeta", CATEGORIA_DOS, 30);
        var betaUno = sembrarItem("Beta", CATEGORIA_UNO, 20);
        var alfaUno = sembrarItem("Alfa", CATEGORIA_UNO, 10);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).extracting("id").containsExactly(alfaUno, betaUno, zetaDos);
        var primero = resultado.get(0);
        assertThat(primero.nombre()).isEqualTo("Alfa");
        assertThat(primero.descripcion()).isEqualTo("desc Alfa");
        assertThat(primero.categoriaId()).isEqualTo(CATEGORIA_UNO);
        assertThat(primero.valor()).isEqualTo(10);
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayItems() {
        // Act
        var resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).isEmpty();
    }
}
