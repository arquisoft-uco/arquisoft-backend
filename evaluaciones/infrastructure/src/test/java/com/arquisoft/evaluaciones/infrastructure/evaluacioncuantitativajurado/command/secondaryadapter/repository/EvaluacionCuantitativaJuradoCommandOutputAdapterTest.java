package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.secondaryadapter.entity.EvaluacionCuantitativaJuradoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class EvaluacionCuantitativaJuradoCommandOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EvaluacionCuantitativaJuradoCommandRepository repository;

    private AppLogger logger;

    private EvaluacionCuantitativaJuradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        logger = mock(AppLogger.class);
        adapter = new EvaluacionCuantitativaJuradoCommandOutputAdapter(repository, logger);
    }

    private void sembrarEvaluacion(UUID id, UUID evaluacionJurado) {
        entityManager.persist(EvaluacionCuantitativaJuradoJpaEntity.builder()
                .id(id)
                .evaluacionJuradoId(evaluacionJurado)
                .itemId(UUID.randomUUID())
                .puntaje(300)
                .build());
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void debeRetornarVacio_cuandoLaEvaluacionNoExiste() {
        // Act & Assert
        assertThat(adapter.obtenerPorId(UUID.randomUUID())).isEmpty();
    }

    @Test
    void debeRetornarEntity_cuandoLaEvaluacionExiste() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID evaluacionJurado = UUID.randomUUID();
        UUID item = UUID.randomUUID();
        entityManager.persist(EvaluacionCuantitativaJuradoJpaEntity.builder()
                .id(id)
                .evaluacionJuradoId(evaluacionJurado)
                .itemId(item)
                .puntaje(250)
                .build());
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerPorId(id);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().id()).isEqualTo(id);
        assertThat(resultado.get().evaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(resultado.get().item()).isEqualTo(item);
        assertThat(resultado.get().puntaje()).isEqualTo(250);
    }

    @Test
    void debeActualizarYPersistirElPuntaje_cuandoCambiarPuntaje() {
        // Arrange
        UUID id = UUID.randomUUID();
        entityManager.persist(EvaluacionCuantitativaJuradoJpaEntity.builder()
                .id(id)
                .evaluacionJuradoId(UUID.randomUUID())
                .itemId(UUID.randomUUID())
                .puntaje(200)
                .build());
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.cambiarPuntaje(id, 450);
        entityManager.clear();

        // Assert
        var actualizada = entityManager.find(EvaluacionCuantitativaJuradoJpaEntity.class, id);
        assertThat(actualizada.getPuntaje()).isEqualTo(450);
    }

    @Test
    void debeDelegarExistePorItemEnRepositorio_cuandoConsultaExistencia() {
        // Arrange
        var item = UUID.randomUUID();
        entityManager.persist(EvaluacionCuantitativaJuradoJpaEntity.builder()
                .id(UUID.randomUUID())
                .evaluacionJuradoId(UUID.randomUUID())
                .itemId(item)
                .puntaje(300)
                .build());
        entityManager.flush();
        entityManager.clear();

        // Act
        var existe = adapter.existePorItem(item);
        var noExiste = adapter.existePorItem(UUID.randomUUID());

        // Assert
        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }

    @Test
    void debeDelegarConsultaDeIdsEnRepositorio_cuandoConsultaIdsPorEvaluacionJurado() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var perteneciente = UUID.randomUUID();
        var ajena = UUID.randomUUID();
        sembrarEvaluacion(perteneciente, evaluacionJurado);
        sembrarEvaluacion(ajena, UUID.randomUUID());

        // Act
        var resultado = adapter.consultarIdsPorEvaluacionJurado(evaluacionJurado, Set.of(perteneciente, ajena));

        // Assert
        assertThat(resultado).containsExactly(perteneciente);
    }

    @Test
    void debeEliminarYLogearElLote_cuandoEliminaPorIds() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var primera = UUID.randomUUID();
        var segunda = UUID.randomUUID();
        var intacta = UUID.randomUUID();
        sembrarEvaluacion(primera, evaluacionJurado);
        sembrarEvaluacion(segunda, evaluacionJurado);
        sembrarEvaluacion(intacta, evaluacionJurado);

        // Act
        adapter.eliminarPorIds(evaluacionJurado, Set.of(primera, segunda));
        entityManager.clear();

        // Assert
        assertThat(entityManager.find(EvaluacionCuantitativaJuradoJpaEntity.class, primera)).isNull();
        assertThat(entityManager.find(EvaluacionCuantitativaJuradoJpaEntity.class, segunda)).isNull();
        assertThat(entityManager.find(EvaluacionCuantitativaJuradoJpaEntity.class, intacta)).isNotNull();
        verify(logger).debug(any(ClaveMensaje.class), eq(2));
    }
}
