package com.arquisoft.fichas.infrastructure.estadoficha.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
import com.arquisoft.fichas.infrastructure.estadoficha.command.secondaryadapter.entity.EstadoFichaJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstadoFichaQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EstadoFichaQueryRepository repository;

    private EstadoFichaQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstadoFichaQueryOutputAdapter(repository);
    }

    @Test
    void debeRetornarLosEstadosDelCatalogo_cuandoExistenEnBD() {
        // Arrange
        persistirEstado("EN_CONSTRUCCION", "En Construccion", "Ficha en desarrollo");
        persistirEstado("APROBADA", "Aprobada", "Ficha aprobada por el comite");
        entityManager.flush();

        // Act
        List<EstadoFichaReadModel> resultado = adapter.findAll();

        // Assert
        assertThat(resultado)
                .extracting(EstadoFichaReadModel::id)
                .containsExactlyInAnyOrder("EN_CONSTRUCCION", "APROBADA");
    }

    @Test
    void debeProyectarTodasLasColumnas_cuandoLeeUnEstado() {
        // Arrange
        persistirEstado("NO_APROBADA", "No Aprobada", "Ficha rechazada");
        entityManager.flush();

        // Act
        List<EstadoFichaReadModel> resultado = adapter.findAll();

        // Assert
        assertThat(resultado).singleElement().satisfies(estado -> {
            assertThat(estado.id()).isEqualTo("NO_APROBADA");
            assertThat(estado.nombre()).isEqualTo("No Aprobada");
            assertThat(estado.descripcion()).isEqualTo("Ficha rechazada");
        });
    }

    @Test
    void debeRetornarVacio_cuandoNoHayEstadosEnBD() {
        // Act & Assert
        assertThat(adapter.findAll()).isEmpty();
    }

    private void persistirEstado(String id, String nombre, String descripcion) {
        entityManager.persist(EstadoFichaJpaEntity.builder()
                .id(id)
                .nombre(nombre)
                .descripcion(descripcion)
                .build());
    }
}
