package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.observacionitemjurado.query.criteria.ObservacionItemJuradoCriteria;
import com.arquisoft.evaluaciones.application.observacionitemjurado.query.readmodel.ObservacionItemJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.secondaryadapter.entity.ObservacionItemJuradoJpaEntity;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ObservacionItemJuradoQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ObservacionItemJuradoQueryRepository repository;

    private ObservacionItemJuradoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ObservacionItemJuradoQueryOutputAdapter(repository, new ObservacionItemJuradoJpaSpecification());
    }

    private UUID sembrar(UUID evaluacionCuantitativaJurado, String descripcion) {
        var id = UUID.randomUUID();
        entityManager.persist(ObservacionItemJuradoJpaEntity.builder()
                .id(id)
                .evaluacionCuantitativaJuradoId(evaluacionCuantitativaJurado)
                .descripcion(descripcion)
                .build());
        return id;
    }

    private static ObservacionItemJuradoCriteria.Builder criteria(UUID evaluacionCuantitativaJurado) {
        return ObservacionItemJuradoCriteria.builder().evaluacionCuantitativaJurado(evaluacionCuantitativaJurado);
    }

    @Test
    void debeRetornarSoloLasObservacionesDeLaEvaluacionPedida_conIdYDescripcionMapeados() {
        // Arrange
        var solicitada = UUID.randomUUID();
        var otra = UUID.randomUUID();
        var idSolicitada = sembrar(solicitada, "Sustenta el puntaje");
        sembrar(otra, "Observación de otra evaluación");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarTodas(criteria(solicitada).build());

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent())
                .containsExactly(new ObservacionItemJuradoReadModel(idSolicitada, "Sustenta el puntaje"));
    }

    @Test
    void debeRespetarElOrdenDescendentePorDescripcion() {
        // Arrange
        var evaluacion = UUID.randomUUID();
        sembrar(evaluacion, "Alfa");
        sembrar(evaluacion, "Gamma");
        sembrar(evaluacion, "Beta");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarTodas(criteria(evaluacion)
                .ordenamiento(List.of(SortOrder.of("descripcion", SortDirection.DESC)))
                .build());

        // Assert
        assertThat(resultado.getContent()).extracting(ObservacionItemJuradoReadModel::descripcion)
                .containsExactly("Gamma", "Beta", "Alfa");
    }

    @Test
    void debePaginar_conElTotalDeLaEvaluacionCompleta() {
        // Arrange
        var evaluacion = UUID.randomUUID();
        sembrar(evaluacion, "Alfa");
        sembrar(evaluacion, "Beta");
        sembrar(evaluacion, "Gamma");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarTodas(criteria(evaluacion)
                .pagina(1)
                .tamanio(2)
                .ordenamiento(List.of(SortOrder.of("descripcion", SortDirection.ASC)))
                .build());

        // Assert
        assertThat(resultado.getContent()).extracting(ObservacionItemJuradoReadModel::descripcion)
                .containsExactly("Gamma");
        assertThat(resultado.getTotalElements()).isEqualTo(3);
        assertThat(resultado.getTotalPages()).isEqualTo(2);
        assertThat(resultado.getPage()).isEqualTo(1);
        assertThat(resultado.isLast()).isTrue();
    }

    @Test
    void debeFiltrarPorDescripcion_sinSalirseDeLaEvaluacionPedida() {
        // Arrange
        var evaluacion = UUID.randomUUID();
        sembrar(evaluacion, "Rigor metodologico insuficiente");
        sembrar(evaluacion, "Redaccion clara");
        sembrar(UUID.randomUUID(), "Rigor en otra evaluacion");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarTodas(criteria(evaluacion)
                .raiz(NodoFiltro.predicado("descripcion", FiltroOperador.CONTIENE, "Rigor"))
                .build());

        // Assert
        assertThat(resultado.getContent()).extracting(ObservacionItemJuradoReadModel::descripcion)
                .containsExactly("Rigor metodologico insuficiente");
    }

    @Test
    void debeRetornarPaginaVacia_cuandoLaEvaluacionNoTieneObservaciones() {
        // Act
        var resultado = adapter.consultarTodas(criteria(UUID.randomUUID()).build());

        // Assert
        assertThat(resultado.isEmpty()).isTrue();
        assertThat(resultado.getTotalElements()).isZero();
    }
}
