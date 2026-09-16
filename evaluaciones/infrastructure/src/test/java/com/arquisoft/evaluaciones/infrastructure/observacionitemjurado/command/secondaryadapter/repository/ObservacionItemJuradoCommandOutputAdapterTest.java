package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.entity.ObservacionItemJuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.secondaryadapter.entity.ObservacionItemJuradoJpaEntity;
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
class ObservacionItemJuradoCommandOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ObservacionItemJuradoCommandRepository repository;

    private ObservacionItemJuradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ObservacionItemJuradoCommandOutputAdapter(repository, mock(AppLogger.class));
    }

    @Test
    void debePersistirLaObservacion_cuandoRegistrar() {
        // Arrange
        var id = UUID.randomUUID();
        var evaluacionCuantitativaJurado = UUID.randomUUID();
        var entity = new ObservacionItemJuradoEntity(id, evaluacionCuantitativaJurado, "Sustenta el puntaje otorgado");

        // Act
        adapter.registrar(entity);
        entityManager.flush();
        entityManager.clear();

        // Assert
        var persistida = entityManager.find(ObservacionItemJuradoJpaEntity.class, id);
        assertThat(persistida).isNotNull();
        assertThat(persistida.getEvaluacionCuantitativaJuradoId()).isEqualTo(evaluacionCuantitativaJurado);
        assertThat(persistida.getDescripcion()).isEqualTo("Sustenta el puntaje otorgado");
    }

    @Test
    void debeRetornarVerdadero_cuandoExisteObservacionConMismaEvaluacionYDescripcion() {
        // Arrange
        var evaluacionCuantitativaJurado = UUID.randomUUID();
        entityManager.persist(ObservacionItemJuradoJpaEntity.builder()
                .id(UUID.randomUUID())
                .evaluacionCuantitativaJuradoId(evaluacionCuantitativaJurado)
                .descripcion("Descripción existente")
                .build());
        entityManager.flush();
        entityManager.clear();

        // Act
        var existe = adapter.existePorEvaluacionYDescripcion(evaluacionCuantitativaJurado, "Descripción existente");

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void debeRetornarFalso_cuandoNoExisteObservacionConEsaDescripcion() {
        // Act & Assert
        assertThat(adapter.existePorEvaluacionYDescripcion(UUID.randomUUID(), "Descripción inexistente")).isFalse();
    }
}
