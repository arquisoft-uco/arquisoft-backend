package com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativoasesor.query.secondaryadapter.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
class CategoriaItemCuantitativoAsesorQueryOutputAdapterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CategoriaItemCuantitativoAsesorQueryRepository repository;

    private CategoriaItemCuantitativoAsesorQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new CategoriaItemCuantitativoAsesorQueryOutputAdapter(repository);
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS categoria_item_cuantitativo_asesor (
                    id UUID NOT NULL,
                    nombre VARCHAR(100) NOT NULL,
                    descripcion VARCHAR(300) NOT NULL,
                    PRIMARY KEY (id),
                    CONSTRAINT uk_cat_cuant_asesor_nombre UNIQUE (nombre))
                """).executeUpdate();
        entityManager.flush();
    }

    private UUID sembrar(String nombre, String descripcion) {
        var id = UUID.randomUUID();
        entityManager.createNativeQuery(
                        "INSERT INTO categoria_item_cuantitativo_asesor (id, nombre, descripcion) VALUES (?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, nombre)
                .setParameter(3, descripcion)
                .executeUpdate();
        return id;
    }

    @Test
    void debeRetornarTodasOrdenadasPorNombreAscendente_cuandoNoHayFiltro() {
        // Arrange
        var idRigor = sembrar("Rigor", "Evalúa el rigor");
        var idAportes = sembrar("Aportes", "Evalúa los aportes");
        var idPuntualidad = sembrar("Puntualidad", "Evalúa la puntualidad");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultar(null);

        // Assert
        assertThat(resultado).extracting("id", "nombre", "descripcion").containsExactly(
                tuple(idAportes, "Aportes", "Evalúa los aportes"),
                tuple(idPuntualidad, "Puntualidad", "Evalúa la puntualidad"),
                tuple(idRigor, "Rigor", "Evalúa el rigor"));
    }

    @Test
    void debeFiltrarPorCoincidenciaParcialSinDistinguirMayusculas_cuandoHayFiltro() {
        // Arrange
        sembrar("Puntualidad", "Evalúa la puntualidad");
        sembrar("Rigor", "Evalúa el rigor");
        sembrar("Impuntual", "Evalúa la impuntualidad");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultar("PUNTUAL");

        // Assert
        assertThat(resultado).extracting("nombre").containsExactly("Impuntual", "Puntualidad");
    }

    @Test
    void debeRetornarListaVacia_cuandoNingunNombreCoincide() {
        // Arrange
        sembrar("Rigor", "Evalúa el rigor");
        entityManager.flush();
        entityManager.clear();

        // Act & Assert
        assertThat(adapter.consultar("inexistente")).isEmpty();
    }

    @Test
    void debeRetornarListaVacia_cuandoLaTablaEstaVacia() {
        // Act & Assert
        assertThat(adapter.consultar(null)).isEmpty();
    }
}
