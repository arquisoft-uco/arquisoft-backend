package com.arquisoft.fichas.infrastructure.estadoevaluacion.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadoevaluacion.query.readmodel.EstadoEvaluacionReadModel;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.command.secondaryadapter.entity.EstadoEvaluacionJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstadoEvaluacionQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EstadoEvaluacionQueryRepository repository;

    private EstadoEvaluacionQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstadoEvaluacionQueryOutputAdapter(repository);
    }

    @Test
    void debeRetornarLosEstadosDelCatalogo_cuandoExistenEnBD() {
        // Arrange
        persistirEstado("EN_EVALUACION", "En Evaluacion", "En evaluacion por un representante del comite.");
        persistirEstado("APROBADA", "Aprobada", "Paso por revision y fue aprobada.");
        persistirEstado("DESCARTADA", "Descartada", "Fue descartada.");
        entityManager.flush();

        // Act
        List<EstadoEvaluacionReadModel> resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado)
                .extracting(EstadoEvaluacionReadModel::id)
                .containsExactlyInAnyOrder("EN_EVALUACION", "APROBADA", "DESCARTADA");
    }

    @Test
    void debeProyectarTodasLasColumnas_cuandoLeeUnEstado() {
        // Arrange
        persistirEstado("APROBADA_CON_OBSERVACIONES", "Aprobada Con Observaciones",
                "Fue aprobada con observaciones.");
        entityManager.flush();

        // Act
        List<EstadoEvaluacionReadModel> resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).singleElement().satisfies(estado -> {
            assertThat(estado.id()).isEqualTo("APROBADA_CON_OBSERVACIONES");
            assertThat(estado.nombre()).isEqualTo("Aprobada Con Observaciones");
            assertThat(estado.descripcion()).isEqualTo("Fue aprobada con observaciones.");
        });
    }

    @Test
    void debeRetornarVacio_cuandoNoHayFilasEnBD() {
        // Act & Assert
        assertThat(adapter.consultarTodos()).isEmpty();
    }

    @Test
    void debeTolerarDescripcionLarga_cuandoOcupaCasiElLimite() {
        // Arrange
        String descripcionLarga = "d".repeat(255);
        persistirEstado("NO_APROBADA", "No Aprobada", descripcionLarga);
        entityManager.flush();

        // Act
        List<EstadoEvaluacionReadModel> resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).singleElement()
                .extracting(EstadoEvaluacionReadModel::descripcion)
                .isEqualTo(descripcionLarga);
    }

    private void persistirEstado(String id, String nombre, String descripcion) {
        entityManager.persist(EstadoEvaluacionJpaEntity.builder()
                .id(id)
                .nombre(nombre)
                .descripcion(descripcion)
                .build());
    }
}
