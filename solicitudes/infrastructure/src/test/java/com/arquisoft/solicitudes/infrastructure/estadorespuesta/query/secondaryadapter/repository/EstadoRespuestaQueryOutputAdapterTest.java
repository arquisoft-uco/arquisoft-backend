package com.arquisoft.solicitudes.infrastructure.estadorespuesta.query.secondaryadapter.repository;

import com.arquisoft.solicitudes.application.estadorespuesta.query.readmodel.EstadoRespuestaReadModel;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.secondaryadapter.entity.EstadoRespuestaJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstadoRespuestaQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EstadoRespuestaQueryRepository repository;

    private EstadoRespuestaQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstadoRespuestaQueryOutputAdapter(repository);
    }

    @Test
    void debeRetornarLosEstadosDelCatalogo_cuandoExistenEnBD() {
        // Arrange
        persistirEstado("APROBADA", "Aprobada", "Cumple los criterios");
        persistirEstado("NO_APROBADA", "No aprobada", "No cumple los criterios");
        persistirEstado("EN_REVISION", "En revisión", "En proceso de evaluación");
        entityManager.flush();

        // Act
        var resultado = adapter.findAll();

        // Assert
        assertThat(resultado)
                .extracting(EstadoRespuestaReadModel::id)
                .containsExactlyInAnyOrder("APROBADA", "NO_APROBADA", "EN_REVISION");
    }

    @Test
    void debeProyectarTodasLasColumnas_cuandoLeeUnEstado() {
        // Arrange
        persistirEstado("EN_REVISION", "En revisión", "En proceso de evaluación");
        entityManager.flush();

        // Act
        var resultado = adapter.findAll();

        // Assert
        assertThat(resultado).singleElement().satisfies(estado -> {
            assertThat(estado.id()).isEqualTo("EN_REVISION");
            assertThat(estado.nombre()).isEqualTo("En revisión");
            assertThat(estado.descripcion()).isEqualTo("En proceso de evaluación");
        });
    }

    @Test
    void debeRetornarVacio_cuandoNoHayEstadosEnBD() {
        // Act
        var resultado = adapter.findAll();

        // Assert
        assertThat(resultado).isEmpty();
    }

    private void persistirEstado(String id, String nombre, String descripcion) {
        entityManager.persist(EstadoRespuestaJpaEntity.builder()
                .id(id)
                .nombre(nombre)
                .descripcion(descripcion)
                .build());
    }
}
