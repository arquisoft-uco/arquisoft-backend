package com.arquisoft.biblioteca.infrastructure.estadodocumentacion.query.secondaryadapter.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(EstadoDocumentacionQueryOutputAdapter.class)
class EstadoDocumentacionQueryOutputAdapterTest {

    @Autowired
    private EstadoDocumentacionQueryOutputAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void prepararTabla() {
        var em = entityManager.getEntityManager();
        em.createNativeQuery("CREATE TABLE IF NOT EXISTS estado_documentacion (id VARCHAR(60) PRIMARY KEY, "
                + "nombre VARCHAR(60), descripcion VARCHAR(300))").executeUpdate();
        em.createNativeQuery("DELETE FROM estado_documentacion").executeUpdate();
    }

    private void sembrar(String id, String nombre, String descripcion) {
        entityManager.getEntityManager()
                .createNativeQuery("INSERT INTO estado_documentacion (id, nombre, descripcion) VALUES (?1, ?2, ?3)")
                .setParameter(1, id).setParameter(2, nombre).setParameter(3, descripcion)
                .executeUpdate();
    }

    @Test
    void debeRetornarLosEstadosConTodasLasColumnas_cuandoExistenEnBD() {
        // Arrange
        sembrar("PENDIENTE", "Pendiente", "Por publicar");
        sembrar("PUBLICADO", "Publicado", "Publicada");
        sembrar("NO_PUBLICADO", "No Publicado", "No publicada");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.findAll();

        // Assert
        assertThat(resultado).hasSize(3);
        assertThat(resultado).anySatisfy(estado -> {
            assertThat(estado.id()).isEqualTo("NO_PUBLICADO");
            assertThat(estado.nombre()).isEqualTo("No Publicado");
            assertThat(estado.descripcion()).isEqualTo("No publicada");
        });
        assertThat(resultado).extracting("id").containsExactlyInAnyOrder("PENDIENTE", "PUBLICADO", "NO_PUBLICADO");
    }

    @Test
    void debeRetornarListaVacia_cuandoLaTablaEstaVacia() {
        // Arrange
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.findAll();

        // Assert
        assertThat(resultado).isEmpty();
    }
}
