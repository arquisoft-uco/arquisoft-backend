package com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.repository;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EvaluacionCommandOutputAdapterTest {

    @Mock
    private EvaluacionCommandRepository repository;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private EvaluacionCommandOutputAdapter adapter;

    @Test
    void debeDelegarActualizacionYLoguear_cuandoSeActualizaElEstado() {
        // Arrange
        var evaluacion = UUID.randomUUID();

        // Act
        adapter.actualizarEstado(evaluacion, "EN_PROGRESO");

        // Assert
        verify(repository).actualizarEstado(evaluacion, "EN_PROGRESO");
        verify(logger).debug(EvaluacionKey.LOG_ESTADO_ACTUALIZADO, evaluacion, "EN_PROGRESO");
    }
}
