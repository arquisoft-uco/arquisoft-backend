package com.arquisoft.proyectos.infrastructure.estadoasesor.query.secondaryadapter.repository;

import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.secondaryadapter.repository.EstadoAsesorFixtureJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstadoAsesorQueryOutputAdapterTest {

    @Autowired
    private EstadoAsesorQueryRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private EstadoAsesorQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstadoAsesorQueryOutputAdapter(repository);
    }

    @Test
    void debeRetornarTodosLosEstadosConSusColumnas_cuandoExistenEnBD() {
        // Arrange
        entityManager.persist(new EstadoAsesorFixtureJpaEntity("ASIGNADO", "Asignado", "Asesor asignado"));
        entityManager.persist(new EstadoAsesorFixtureJpaEntity("FINALIZADO", "Finalizado", "Asesoria finalizada"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).extracting(e -> e.id()).containsExactlyInAnyOrder("ASIGNADO", "FINALIZADO");
        var asignado = resultado.stream().filter(e -> e.id().equals("ASIGNADO")).findFirst().orElseThrow();
        assertThat(asignado.nombre()).isEqualTo("Asignado");
        assertThat(asignado.descripcion()).isEqualTo("Asesor asignado");
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayEstados() {
        // Act
        var resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).isEmpty();
    }
}
