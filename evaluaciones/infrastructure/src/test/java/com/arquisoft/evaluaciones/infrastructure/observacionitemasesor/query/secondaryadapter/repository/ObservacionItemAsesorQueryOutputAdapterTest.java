package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.observacionitemasesor.query.criteria.ObservacionItemAsesorCriteria;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.readmodel.ObservacionItemAsesorReadModel;
import com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.secondaryadapter.entity.ObservacionItemAsesorJpaEntity;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ObservacionItemAsesorQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ObservacionItemAsesorQueryRepository repository;

    private ObservacionItemAsesorQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ObservacionItemAsesorQueryOutputAdapter(repository, new ObservacionItemAsesorJpaSpecification());
    }

    private UUID sembrar(UUID evaluacionCuantitativaAsesor, String descripcion) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(ObservacionItemAsesorJpaEntity.builder()
                .id(id)
                .evaluacionCuantitativaAsesorId(evaluacionCuantitativaAsesor)
                .descripcion(descripcion)
                .build());
        return id;
    }

    private static ObservacionItemAsesorCriteria.Builder criteria(UUID evaluacionCuantitativaAsesor) {
        return ObservacionItemAsesorCriteria.builder().evaluacionCuantitativaAsesor(evaluacionCuantitativaAsesor);
    }

    @Test
    void debeRetornarSoloLasObservacionesDeLaEvaluacionPedida_conIdYDescripcionMapeados() {
        // Arrange
        var solicitada = UtilUUID.generarNuevoUUID();
        var otra = UtilUUID.generarNuevoUUID();
        var idSolicitada = sembrar(solicitada, "Sustenta el puntaje");
        sembrar(otra, "Observacion de otra evaluacion");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarTodas(criteria(solicitada).build());

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent())
                .containsExactly(new ObservacionItemAsesorReadModel(idSolicitada, "Sustenta el puntaje"));
    }

    @Test
    void debeRespetarElOrdenDescendentePorDescripcion() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
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
        assertThat(resultado.getContent()).extracting(ObservacionItemAsesorReadModel::descripcion)
                .containsExactly("Gamma", "Beta", "Alfa");
    }

    @Test
    void debePaginar_conElTotalDeLaEvaluacionCompleta() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
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
        assertThat(resultado.getContent()).extracting(ObservacionItemAsesorReadModel::descripcion)
                .containsExactly("Gamma");
        assertThat(resultado.getTotalElements()).isEqualTo(3);
        assertThat(resultado.getTotalPages()).isEqualTo(2);
        assertThat(resultado.getPage()).isEqualTo(1);
        assertThat(resultado.isLast()).isTrue();
    }

    @Test
    void debeFiltrarPorDescripcion_sinSalirseDeLaEvaluacionPedida() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        sembrar(evaluacion, "Rigor metodologico insuficiente");
        sembrar(evaluacion, "Redaccion clara");
        sembrar(UtilUUID.generarNuevoUUID(), "Rigor en otra evaluacion");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarTodas(criteria(evaluacion)
                .raiz(NodoFiltro.predicado("descripcion", FiltroOperador.CONTIENE, "Rigor"))
                .build());

        // Assert
        assertThat(resultado.getContent()).extracting(ObservacionItemAsesorReadModel::descripcion)
                .containsExactly("Rigor metodologico insuficiente");
    }

    @Test
    void debeRetornarPaginaVacia_cuandoLaEvaluacionNoTieneObservaciones() {
        // Act
        var resultado = adapter.consultarTodas(criteria(UtilUUID.generarNuevoUUID()).build());

        // Assert
        assertThat(resultado.isEmpty()).isTrue();
        assertThat(resultado.getTotalElements()).isZero();
    }
}
