package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.readmodel.ItemCuantitativoJuradoReadModel;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// La tabla item_cuantitativo_jurado ya tiene @Entity en el lado de comando
// (ItemCuantitativoJuradoJpaEntity), asi que Hibernate la crea via ddl-auto. La tabla
// categoria_item_cuantitativo_jurado no tiene @Entity en ningun lado (el comando solo la
// consulta con una query nativa), asi que se crea aqui con el mismo DDL de la migracion
// V20260906183010 para poder ejercitar el @Subselect real, tal como exige la skill de testing.
@DataJpaTest
class ItemCuantitativoJuradoQueryOutputAdapterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ItemCuantitativoJuradoQueryRepository repository;

    private ItemCuantitativoJuradoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ItemCuantitativoJuradoQueryOutputAdapter(repository);
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS categoria_item_cuantitativo_jurado (
                    id UUID NOT NULL,
                    nombre VARCHAR(100) NOT NULL,
                    descripcion VARCHAR(300) NOT NULL,
                    PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.flush();
    }

    private UUID sembrarCategoria(String nombre) {
        UUID id = UUID.randomUUID();
        entityManager.createNativeQuery(
                "INSERT INTO categoria_item_cuantitativo_jurado (id, nombre, descripcion) VALUES (?, ?, 'desc')")
                .setParameter(1, id).setParameter(2, nombre).executeUpdate();
        return id;
    }

    private void sembrarItem(UUID id, String nombre, String descripcion, UUID categoriaId, int valor) {
        entityManager.createNativeQuery("""
                INSERT INTO item_cuantitativo_jurado (id, nombre, descripcion, categoria_id, valor)
                VALUES (?, ?, ?, ?, ?)
                """)
                .setParameter(1, id).setParameter(2, nombre).setParameter(3, descripcion)
                .setParameter(4, categoriaId).setParameter(5, valor)
                .executeUpdate();
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayItemsRegistrados() {
        // Act
        List<ItemCuantitativoJuradoReadModel> resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRetornarTodosLosItems_cuandoHayRegistrosEnLaTabla() {
        // Arrange
        UUID categoria = sembrarCategoria("Presentación");
        sembrarItem(UUID.randomUUID(), "Puntualidad", "Evalúa la puntualidad", categoria, 50);
        sembrarItem(UUID.randomUUID(), "Vestimenta", "Evalúa la vestimenta", categoria, 20);
        entityManager.flush();
        entityManager.clear();

        // Act
        List<ItemCuantitativoJuradoReadModel> resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).hasSize(2);
    }

    @Test
    void debeOrdenarPorCategoriaIdYLuegoPorNombre_cuandoHayVariosItems() {
        // Arrange: dos categorías con nombres de item cruzados (Zeta antes que Alfa al insertar)
        // para probar que el orden no es solo por nombre — la BD decide el orden real de
        // categoriaId (no necesariamente el de UUID.compareTo de Java), así que la aserción
        // no asume cuál UUID es "menor": solo que cada categoría queda contigua y ordenada
        // internamente por nombre.
        UUID categoriaUno = sembrarCategoria("Categoria Uno");
        UUID categoriaDos = sembrarCategoria("Categoria Dos");
        sembrarItem(UUID.randomUUID(), "Zeta", "desc", categoriaUno, 10);
        sembrarItem(UUID.randomUUID(), "Alfa", "desc", categoriaUno, 20);
        sembrarItem(UUID.randomUUID(), "Beta", "desc", categoriaDos, 30);
        entityManager.flush();
        entityManager.clear();

        // Act
        List<ItemCuantitativoJuradoReadModel> resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).hasSize(3);
        List<UUID> categoriasEnOrden = resultado.stream()
                .map(ItemCuantitativoJuradoReadModel::categoriaId)
                .distinct()
                .toList();
        assertThat(categoriasEnOrden).hasSize(2);
        List<String> nombresCategoriaUno = resultado.stream()
                .filter(item -> item.categoriaId().equals(categoriaUno))
                .map(ItemCuantitativoJuradoReadModel::nombre)
                .toList();
        assertThat(nombresCategoriaUno).containsExactly("Alfa", "Zeta");
        List<UUID> categoriaIdsAgrupados = resultado.stream()
                .map(ItemCuantitativoJuradoReadModel::categoriaId)
                .toList();
        // Contiguo: no hay un ítem de la otra categoría intercalado entre los de la misma
        int primeraAparicionUno = categoriaIdsAgrupados.indexOf(categoriaUno);
        int ultimaAparicionUno = categoriaIdsAgrupados.lastIndexOf(categoriaUno);
        assertThat(ultimaAparicionUno - primeraAparicionUno).isEqualTo(nombresCategoriaUno.size() - 1);
    }

    @Test
    void debeMapearCategoriaIdYValorCorrectamente_cuandoConsultaTodos() {
        // Arrange
        UUID categoria = sembrarCategoria("Presentación");
        UUID item = UUID.randomUUID();
        sembrarItem(item, "Puntualidad", "Evalúa la puntualidad", categoria, 50);
        entityManager.flush();
        entityManager.clear();

        // Act
        List<ItemCuantitativoJuradoReadModel> resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).hasSize(1);
        ItemCuantitativoJuradoReadModel readModel = resultado.get(0);
        assertThat(readModel.id()).isEqualTo(item);
        assertThat(readModel.nombre()).isEqualTo("Puntualidad");
        assertThat(readModel.descripcion()).isEqualTo("Evalúa la puntualidad");
        assertThat(readModel.categoriaId()).isEqualTo(categoria);
        assertThat(readModel.valor()).isEqualTo(50);
    }
}
