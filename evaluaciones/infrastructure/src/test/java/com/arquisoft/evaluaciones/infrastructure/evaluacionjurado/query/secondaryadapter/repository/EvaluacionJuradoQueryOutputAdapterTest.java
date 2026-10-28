package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.entity.EvaluacionJuradoJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.jurado.command.secondaryadapter.entity.JuradoJpaEntity;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroInvalidoException;
import com.arquisoft.shared.query.pagination.SortDirection;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class EvaluacionJuradoQueryOutputAdapterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private EvaluacionJuradoQueryRepository repository;

    private EvaluacionJuradoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionJuradoQueryOutputAdapter(repository, new EvaluacionJuradoJpaSpecification());
    }

    private UUID sembrarJurado(String nombre, String email) {
        var jurado = UUID.randomUUID();
        testEntityManager.persist(JuradoJpaEntity.builder()
                .id(jurado)
                .identificador(jurado.toString().substring(0, 30))
                .nombre(nombre)
                .email(email)
                .ocurridoEn(Instant.now())
                .build());
        return jurado;
    }

    private UUID sembrarEvaluacionJurado(UUID evaluacion, UUID jurado) {
        var id = UUID.randomUUID();
        testEntityManager.persist(EvaluacionJuradoJpaEntity.builder()
                .id(id)
                .evaluacion(evaluacion)
                .jurado(jurado)
                .estado("PENDIENTE_POR_EVALUAR")
                .build());
        return id;
    }

    private static EvaluacionJuradoCriteria.Builder criteria(UUID evaluacion) {
        return EvaluacionJuradoCriteria.builder().evaluacion(evaluacion);
    }

    private void sincronizar() {
        entityManager.flush();
        testEntityManager.flush();
        testEntityManager.clear();
    }

    @Test
    void debeRetornarTodasLasFilasDeLaEvaluacion_sinFiltro() {
        // Arrange
        var evaluacion = UUID.randomUUID();
        var juradoUno = sembrarJurado("Ana Ruiz", "ana@uco.edu.co");
        var juradoDos = sembrarJurado("Beto Diaz", "beto@uco.edu.co");
        sembrarEvaluacionJurado(evaluacion, juradoUno);
        sembrarEvaluacionJurado(evaluacion, juradoDos);
        sembrarEvaluacionJurado(UUID.randomUUID(), sembrarJurado("Carla Otra", "carla@uco.edu.co"));
        sincronizar();

        // Act
        var resultado = adapter.consultarTodas(criteria(evaluacion).build());

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(2);
        assertThat(resultado.getContent())
                .extracting(EvaluacionJuradoReadModel::jurado)
                .extracting(EvaluacionJuradoReadModel.Jurado::nombre)
                .containsExactlyInAnyOrder("Ana Ruiz", "Beto Diaz");
    }

    @Test
    void debeFiltrarPorNombreDeJurado_sinSalirseDeLaEvaluacionPedida() {
        // Arrange
        var evaluacion = UUID.randomUUID();
        var juradoAna = sembrarJurado("Ana Ruiz", "ana@uco.edu.co");
        var juradoBeto = sembrarJurado("Beto Diaz", "beto@uco.edu.co");
        var juradoOtraAna = sembrarJurado("Ana Perez", "anap@uco.edu.co");
        sembrarEvaluacionJurado(evaluacion, juradoAna);
        sembrarEvaluacionJurado(evaluacion, juradoBeto);
        sembrarEvaluacionJurado(UUID.randomUUID(), juradoOtraAna);
        sincronizar();

        // Act
        var resultado = adapter.consultarTodas(criteria(evaluacion)
                .raiz(NodoFiltro.predicado("jurado", FiltroOperador.CONTIENE, "Ana"))
                .build());

        // Assert
        assertThat(resultado.getContent())
                .extracting(EvaluacionJuradoReadModel::jurado)
                .extracting(EvaluacionJuradoReadModel.Jurado::nombre)
                .containsExactly("Ana Ruiz");
    }

    @Test
    void debeFiltrarPorJuradoIdConEs() {
        // Arrange
        var evaluacion = UUID.randomUUID();
        var juradoUno = sembrarJurado("Ana Ruiz", "ana@uco.edu.co");
        var juradoDos = sembrarJurado("Beto Diaz", "beto@uco.edu.co");
        sembrarEvaluacionJurado(evaluacion, juradoUno);
        sembrarEvaluacionJurado(evaluacion, juradoDos);
        sincronizar();

        // Act
        var resultado = adapter.consultarTodas(criteria(evaluacion)
                .raiz(NodoFiltro.predicado("juradoId", FiltroOperador.ES, juradoUno.toString()))
                .build());

        // Assert
        assertThat(resultado.getContent())
                .extracting(EvaluacionJuradoReadModel::jurado)
                .extracting(EvaluacionJuradoReadModel.Jurado::id)
                .containsExactly(juradoUno);
    }

    @Test
    void debeFiltrarPorJuradoIdConInMultivalor() {
        // Arrange
        var evaluacion = UUID.randomUUID();
        var juradoUno = sembrarJurado("Ana Ruiz", "ana@uco.edu.co");
        var juradoDos = sembrarJurado("Beto Diaz", "beto@uco.edu.co");
        var juradoTres = sembrarJurado("Carla Gomez", "carla@uco.edu.co");
        sembrarEvaluacionJurado(evaluacion, juradoUno);
        sembrarEvaluacionJurado(evaluacion, juradoDos);
        sembrarEvaluacionJurado(evaluacion, juradoTres);
        sincronizar();

        // Act
        var resultado = adapter.consultarTodas(criteria(evaluacion)
                .raiz(NodoFiltro.predicadoMultivalor("juradoId", FiltroOperador.IN,
                        List.of(juradoUno.toString(), juradoTres.toString())))
                .build());

        // Assert
        assertThat(resultado.getContent())
                .extracting(EvaluacionJuradoReadModel::jurado)
                .extracting(EvaluacionJuradoReadModel.Jurado::id)
                .containsExactlyInAnyOrder(juradoUno, juradoTres);
    }

    @Test
    void debeLanzarFiltroInvalidoException_cuandoJuradoIdNoEsUnUuid() {
        // Arrange
        var evaluacion = UUID.randomUUID();
        var criteriaConFiltroInvalido = criteria(evaluacion)
                .raiz(NodoFiltro.predicado("juradoId", FiltroOperador.ES, "no-es-un-uuid"))
                .build();

        // Act & Assert
        assertThatThrownBy(() -> adapter.consultarTodas(criteriaConFiltroInvalido))
                .isInstanceOf(FiltroInvalidoException.class);
    }

    @Test
    void noDebeIncluirUnJuradoDeOtraEvaluacion_aunqueElFiltroLoAcepte() {
        // Arrange
        var evaluacionPedida = UUID.randomUUID();
        var otraEvaluacion = UUID.randomUUID();
        var juradoDeOtraEvaluacion = sembrarJurado("Dana Leon", "dana@uco.edu.co");
        sembrarEvaluacionJurado(otraEvaluacion, juradoDeOtraEvaluacion);
        sincronizar();

        // Act
        var resultado = adapter.consultarTodas(criteria(evaluacionPedida)
                .raiz(NodoFiltro.predicado("juradoId", FiltroOperador.ES, juradoDeOtraEvaluacion.toString()))
                .build());

        // Assert
        assertThat(resultado.isEmpty()).isTrue();
    }

    @Test
    void debeOrdenarDescendentementePorNombreDeJurado() {
        // Arrange
        var evaluacion = UUID.randomUUID();
        sembrarEvaluacionJurado(evaluacion, sembrarJurado("Alfa", "alfa@uco.edu.co"));
        sembrarEvaluacionJurado(evaluacion, sembrarJurado("Gamma", "gamma@uco.edu.co"));
        sembrarEvaluacionJurado(evaluacion, sembrarJurado("Beta", "beta@uco.edu.co"));
        sincronizar();

        // Act
        var resultado = adapter.consultarTodas(criteria(evaluacion)
                .ordenamiento(List.of(SortOrder.of("jurado", SortDirection.DESC)))
                .build());

        // Assert
        assertThat(resultado.getContent())
                .extracting(EvaluacionJuradoReadModel::jurado)
                .extracting(EvaluacionJuradoReadModel.Jurado::nombre)
                .containsExactly("Gamma", "Beta", "Alfa");
    }

    @Test
    void debePaginar_conElTotalDeLaEvaluacionCompleta() {
        // Arrange
        var evaluacion = UUID.randomUUID();
        sembrarEvaluacionJurado(evaluacion, sembrarJurado("Alfa", "alfa@uco.edu.co"));
        sembrarEvaluacionJurado(evaluacion, sembrarJurado("Beta", "beta@uco.edu.co"));
        sembrarEvaluacionJurado(evaluacion, sembrarJurado("Gamma", "gamma@uco.edu.co"));
        sincronizar();

        // Act
        var resultado = adapter.consultarTodas(criteria(evaluacion)
                .pagina(1)
                .tamanio(2)
                .ordenamiento(List.of(SortOrder.of("jurado", SortDirection.ASC)))
                .build());

        // Assert
        assertThat(resultado.getContent())
                .extracting(EvaluacionJuradoReadModel::jurado)
                .extracting(EvaluacionJuradoReadModel.Jurado::nombre)
                .containsExactly("Gamma");
        assertThat(resultado.getTotalElements()).isEqualTo(3);
        assertThat(resultado.getTotalPages()).isEqualTo(2);
        assertThat(resultado.isLast()).isTrue();
    }

    @Test
    void debeRetornarPaginaVacia_cuandoLaEvaluacionNoTieneJuradosAsignados() {
        // Act
        var resultado = adapter.consultarTodas(criteria(UUID.randomUUID()).build());

        // Assert
        assertThat(resultado.isEmpty()).isTrue();
        assertThat(resultado.getTotalElements()).isZero();
    }
}
