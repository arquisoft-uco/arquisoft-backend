package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.entity.EvaluacionCuantitativaJuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.secondaryadapter.entity.EvaluacionCuantitativaJuradoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DataJpaTest
class EvaluacionCuantitativaJuradoCommandOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EvaluacionCuantitativaJuradoCommandRepository repository;

    private EvaluacionCuantitativaJuradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionCuantitativaJuradoCommandOutputAdapter(repository, mock(AppLogger.class));
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
    void debeActualizarYPersistirElPuntaje_cuandoActualizar() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID evaluacionJurado = UUID.randomUUID();
        UUID item = UUID.randomUUID();
        entityManager.persist(EvaluacionCuantitativaJuradoJpaEntity.builder()
                .id(id)
                .evaluacionJuradoId(evaluacionJurado)
                .itemId(item)
                .puntaje(200)
                .build());
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.actualizar(new EvaluacionCuantitativaJuradoEntity(id, evaluacionJurado, item, 450));
        entityManager.flush();
        entityManager.clear();

        // Assert
        var actualizada = entityManager.find(EvaluacionCuantitativaJuradoJpaEntity.class, id);
        assertThat(actualizada.getPuntaje()).isEqualTo(450);
        assertThat(actualizada.getEvaluacionJuradoId()).isEqualTo(evaluacionJurado);
        assertThat(actualizada.getItemId()).isEqualTo(item);
    }
}
