package com.arquisoft.fichas.infrastructure.estadorevision.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadorevision.query.readmodel.EstadoRevisionReadModel;
import com.arquisoft.fichas.infrastructure.estadorevision.command.secondaryadapter.entity.EstadoRevisionJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstadoRevisionQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EstadoRevisionQueryRepository repository;

    private EstadoRevisionQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstadoRevisionQueryOutputAdapter(repository);
    }

    @Test
    void debeRetornarLosEstadosDelCatalogo_cuandoExistenEnBD() {
        // Arrange
        persistirEstado("NUEVA", "Nueva", "Creada recientemente, aun no revisada.");
        persistirEstado("VISUALIZADA", "Visualizada", "Vista por el estudiante, sin accion aun.");
        persistirEstado("CERRADA", "Cerrada", "Completada y aprobada; estado terminal.");
        entityManager.flush();

        // Act
        var resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado)
                .extracting(EstadoRevisionReadModel::id)
                .containsExactlyInAnyOrder("NUEVA", "VISUALIZADA", "CERRADA");
    }

    @Test
    void debeProyectarTodasLasColumnas_cuandoLeeUnEstado() {
        // Arrange
        persistirEstado("EN_PROGRESO", "En Progreso", "En desarrollo, acciones en curso.");
        entityManager.flush();

        // Act
        var resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).singleElement().satisfies(estado -> {
            assertThat(estado.id()).isEqualTo("EN_PROGRESO");
            assertThat(estado.nombre()).isEqualTo("En Progreso");
            assertThat(estado.descripcion()).isEqualTo("En desarrollo, acciones en curso.");
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
        persistirEstado("CORRECCION_DISPONIBLE", "Correccion Disponible", descripcionLarga);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).singleElement()
                .extracting(EstadoRevisionReadModel::descripcion)
                .isEqualTo(descripcionLarga);
    }

    private void persistirEstado(String id, String nombre, String descripcion) {
        entityManager.persist(EstadoRevisionJpaEntity.builder()
                .id(id)
                .nombre(nombre)
                .descripcion(descripcion)
                .build());
    }
}
