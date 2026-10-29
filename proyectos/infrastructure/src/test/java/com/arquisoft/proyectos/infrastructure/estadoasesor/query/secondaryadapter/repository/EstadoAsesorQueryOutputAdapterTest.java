package com.arquisoft.proyectos.infrastructure.estadoasesor.query.secondaryadapter.repository;

import com.arquisoft.proyectos.application.estadoasesor.query.criteria.EstadoAsesorCriteria;
import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.secondaryadapter.repository.EstadoAsesorFixtureJpaEntity;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;

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
        adapter = new EstadoAsesorQueryOutputAdapter(repository, new EstadoAsesorJpaSpecification());
    }

    private void sembrar() {
        entityManager.persist(new EstadoAsesorFixtureJpaEntity("FINALIZADO", "Finalizado", "Asesoria finalizada"));
        entityManager.persist(new EstadoAsesorFixtureJpaEntity("ASIGNADO", "Asignado", "Asesor asignado"));
        entityManager.flush();
        entityManager.clear();
    }

    private EstadoAsesorCriteria criteria(int pagina, int tamanio, NodoFiltro raiz, List<SortOrder> orden) {
        return EstadoAsesorCriteria.builder().pagina(pagina).tamanio(tamanio).ordenamiento(orden).raiz(raiz).build();
    }

    @Test
    void debeRetornarEstadosConSusColumnas_cuandoExistenEnBD() {
        // Arrange
        sembrar();

        // Act
        var resultado = adapter.consultarTodos(criteria(0, 10, null, List.of()));

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(2);
        var asignado = resultado.getContent().stream()
                .filter(e -> e.id().equals("ASIGNADO")).findFirst().orElseThrow();
        assertThat(asignado.nombre()).isEqualTo("Asignado");
        assertThat(asignado.descripcion()).isEqualTo("Asesor asignado");
    }

    @Test
    void debeFiltrarPorNombre_cuandoHayFiltro() {
        // Arrange
        sembrar();
        var filtro = NodoFiltro.predicado("nombre", FiltroOperador.ES, "Finalizado");

        // Act
        var resultado = adapter.consultarTodos(criteria(0, 10, filtro, List.of()));

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent()).extracting(e -> e.id()).containsExactly("FINALIZADO");
    }

    @Test
    void debeOrdenarPorNombreDescendente_cuandoSePideOrden() {
        // Arrange
        sembrar();
        var orden = List.of(SortOrder.of("nombre", SortDirection.DESC));

        // Act
        var resultado = adapter.consultarTodos(criteria(0, 10, null, orden));

        // Assert
        assertThat(resultado.getContent()).extracting(e -> e.id()).containsExactly("FINALIZADO", "ASIGNADO");
    }

    @Test
    void debeOrdenarPorIdPorDefecto_cuandoNoHayOrden() {
        // Arrange
        sembrar();

        // Act
        var resultado = adapter.consultarTodos(criteria(0, 10, null, List.of()));

        // Assert
        assertThat(resultado.getContent()).extracting(e -> e.id()).containsExactly("ASIGNADO", "FINALIZADO");
    }

    @Test
    void debePaginar_cuandoTamanioMenorAlTotal() {
        // Arrange
        sembrar();

        // Act
        var resultado = adapter.consultarTodos(criteria(1, 1, null, List.of()));

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(2);
        assertThat(resultado.getContent()).extracting(e -> e.id()).containsExactly("FINALIZADO");
    }

    @Test
    void debeRetornarPaginaVacia_cuandoNoHayEstados() {
        // Act
        var resultado = adapter.consultarTodos(criteria(0, 10, null, List.of()));

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }
}
