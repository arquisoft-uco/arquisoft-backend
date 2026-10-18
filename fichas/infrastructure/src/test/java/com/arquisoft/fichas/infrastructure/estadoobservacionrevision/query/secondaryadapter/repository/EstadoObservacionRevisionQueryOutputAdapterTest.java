package com.arquisoft.fichas.infrastructure.estadoobservacionrevision.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadoobservacionrevision.query.readmodel.EstadoObservacionRevisionReadModel;
import com.arquisoft.fichas.infrastructure.estadoobservacionrevision.command.secondaryadapter.entity.EstadoObservacionRevisionJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstadoObservacionRevisionQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EstadoObservacionRevisionQueryRepository repository;

    private EstadoObservacionRevisionQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstadoObservacionRevisionQueryOutputAdapter(repository);
    }

    @Test
    void debeRetornarLosEstadosDelCatalogo_cuandoExistenEnBD() {
        // Arrange
        persistirEstado("PENDIENTE", "Pendiente",
                "La observacion revisión ha sido registrada, pero aun no se ha iniciado ninguna accion sobre ella.");
        persistirEstado("EN_PROGRESO", "En Progreso",
                "La observacion revisión esta siendo trabajada activamente.");
        persistirEstado("CERRADO", "Cerrado",
                "La observacion revisión ha sido completada y no requiere mas acciones.");
        entityManager.flush();

        // Act
        var resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado)
                .extracting(EstadoObservacionRevisionReadModel::id)
                .containsExactlyInAnyOrder("PENDIENTE", "EN_PROGRESO", "CERRADO");
    }

    @Test
    void debeProyectarTodasLasColumnas_cuandoLeeUnEstado() {
        // Arrange
        persistirEstado("EN_PROGRESO", "En Progreso",
                "La observacion revisión esta siendo trabajada activamente.");
        entityManager.flush();

        // Act
        var resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).singleElement().satisfies(estado -> {
            assertThat(estado.id()).isEqualTo("EN_PROGRESO");
            assertThat(estado.nombre()).isEqualTo("En Progreso");
            assertThat(estado.descripcion()).isEqualTo("La observacion revisión esta siendo trabajada activamente.");
        });
    }

    @Test
    void debeRetornarVacio_cuandoNoHayFilasEnBD() {
        // Act & Assert
        assertThat(adapter.consultarTodos()).isEmpty();
    }

    @Test
    void debeTolerarDescripcionLarga_cuandoOcupaElLimite() {
        // Arrange
        var descripcionLarga = "d".repeat(300);
        persistirEstado("PENDIENTE", "Pendiente", descripcionLarga);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).singleElement()
                .extracting(EstadoObservacionRevisionReadModel::descripcion)
                .isEqualTo(descripcionLarga);
    }

    private void persistirEstado(String id, String nombre, String descripcion) {
        entityManager.persist(EstadoObservacionRevisionJpaEntity.builder()
                .id(id)
                .nombre(nombre)
                .descripcion(descripcion)
                .build());
    }
}
