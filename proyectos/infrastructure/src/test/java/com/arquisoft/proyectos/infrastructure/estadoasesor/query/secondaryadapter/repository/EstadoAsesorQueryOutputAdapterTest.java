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

    private void sembrar() {
        entityManager.persist(new EstadoAsesorFixtureJpaEntity("FINALIZADO", "Finalizado", "Asesoria finalizada"));
        entityManager.persist(new EstadoAsesorFixtureJpaEntity("ASIGNADO", "Asignado", "Asesor asignado"));
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void debeRetornarEstadosConSusColumnas_cuandoExistenEnBD() {
        // Arrange
        sembrar();

        // Act
        var resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).hasSize(2);
        var asignado = resultado.stream().filter(e -> e.id().equals("ASIGNADO")).findFirst().orElseThrow();
        assertThat(asignado.nombre()).isEqualTo("Asignado");
        assertThat(asignado.descripcion()).isEqualTo("Asesor asignado");
    }

    @Test
    void debeOrdenarPorIdAscendente_cuandoSeSiembraEnOtroOrden() {
        // Arrange
        sembrar();

        // Act
        var resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).extracting(e -> e.id()).containsExactly("ASIGNADO", "FINALIZADO");
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayEstados() {
        // Act
        var resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).isEmpty();
    }
}
