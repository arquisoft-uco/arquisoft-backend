package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.entity.ObservacionItemJuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.secondaryadapter.entity.ObservacionItemJuradoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class ObservacionItemJuradoCommandOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ObservacionItemJuradoCommandRepository repository;

    private AppLogger logger;

    private ObservacionItemJuradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        logger = mock(AppLogger.class);
        adapter = new ObservacionItemJuradoCommandOutputAdapter(repository, logger);
    }

    private UUID sembrarObservacion(UUID evaluacionCuantitativaJurado, String descripcion) {
        var id = UUID.randomUUID();
        entityManager.persist(ObservacionItemJuradoJpaEntity.builder()
                .id(id)
                .evaluacionCuantitativaJuradoId(evaluacionCuantitativaJurado)
                .descripcion(descripcion)
                .build());
        entityManager.flush();
        entityManager.clear();
        return id;
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

    @Test
    void debeRetornarEntity_cuandoLaObservacionExiste() {
        // Arrange
        var evaluacionCuantitativaJurado = UUID.randomUUID();
        var id = sembrarObservacion(evaluacionCuantitativaJurado, "Sustenta el puntaje otorgado");

        // Act
        var resultado = adapter.obtenerPorId(id);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().id()).isEqualTo(id);
        assertThat(resultado.get().evaluacionCuantitativaJurado()).isEqualTo(evaluacionCuantitativaJurado);
        assertThat(resultado.get().descripcion()).isEqualTo("Sustenta el puntaje otorgado");
    }

    @Test
    void debeRetornarVacio_cuandoLaObservacionNoExiste() {
        // Act & Assert
        assertThat(adapter.obtenerPorId(UUID.randomUUID())).isEmpty();
    }

    @Test
    void debeRetornarVerdadero_cuandoOtraObservacionDeLaMismaEvaluacionTieneLaDescripcion() {
        // Arrange
        var evaluacionCuantitativaJurado = UUID.randomUUID();
        var observacion = sembrarObservacion(evaluacionCuantitativaJurado, "Descripción original");
        sembrarObservacion(evaluacionCuantitativaJurado, "Descripción repetida");

        // Act
        var existe = adapter.existeOtraConDescripcion(observacion, "Descripción repetida");

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void debeRetornarFalso_cuandoSoloLaPropiaObservacionTieneLaDescripcion() {
        // Arrange
        var evaluacionCuantitativaJurado = UUID.randomUUID();
        var observacion = sembrarObservacion(evaluacionCuantitativaJurado, "Descripción original");
        sembrarObservacion(evaluacionCuantitativaJurado, "Descripción distinta");

        // Act
        var existe = adapter.existeOtraConDescripcion(observacion, "Descripción original");

        // Assert
        assertThat(existe).isFalse();
    }

    @Test
    void debeRetornarFalso_cuandoLaMismaDescripcionEstaEnOtraEvaluacionCuantitativa() {
        // Arrange
        var observacion = sembrarObservacion(UUID.randomUUID(), "Descripción original");
        sembrarObservacion(UUID.randomUUID(), "Descripción repetida");

        // Act
        var existe = adapter.existeOtraConDescripcion(observacion, "Descripción repetida");

        // Assert
        assertThat(existe).isFalse();
    }

    @Test
    void debeRetornarFalso_cuandoLaObservacionConsultadaNoExiste() {
        // Arrange
        sembrarObservacion(UUID.randomUUID(), "Descripción repetida");

        // Act
        var existe = adapter.existeOtraConDescripcion(UUID.randomUUID(), "Descripción repetida");

        // Assert
        assertThat(existe).isFalse();
    }

    @Test
    void debeActualizarSoloLaFilaIndicada_cuandoActualizaLaDescripcion() {
        // Arrange
        var evaluacionCuantitativaJurado = UUID.randomUUID();
        var observacion = sembrarObservacion(evaluacionCuantitativaJurado, "Descripción original");
        var otraObservacion = sembrarObservacion(evaluacionCuantitativaJurado, "Descripción de otra observación");

        // Act
        adapter.actualizarDescripcion(observacion, "Descripción actualizada");
        entityManager.flush();
        entityManager.clear();

        // Assert
        var actualizada = entityManager.find(ObservacionItemJuradoJpaEntity.class, observacion);
        var intacta = entityManager.find(ObservacionItemJuradoJpaEntity.class, otraObservacion);
        assertThat(actualizada.getDescripcion()).isEqualTo("Descripción actualizada");
        assertThat(actualizada.getEvaluacionCuantitativaJuradoId()).isEqualTo(evaluacionCuantitativaJurado);
        assertThat(intacta.getDescripcion()).isEqualTo("Descripción de otra observación");
        verify(logger).debug(any(ClaveMensaje.class), eq(observacion));
    }
}
