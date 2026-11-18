package com.arquisoft.evaluaciones.application.observacionitemjurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.EvaluacionJuradoFinalizadaPorObservacionFinder;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.ObservacionItemJuradoPorIdFinder;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.ObservacionItemJuradoOutputPort;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.validator.RemoverObservacionItemJuradoValidator;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ObservacionItemJuradoDomain;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.RemocionObservacionItemJuradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ObservacionItemJuradoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverObservacionItemJuradoUseCaseImplTest {

    @Mock
    private ObservacionItemJuradoOutputPort outputPort;

    @Mock
    private ObservacionItemJuradoPorIdFinder observacionItemJuradoPorIdFinder;

    @Mock
    private EvaluacionJuradoFinalizadaPorObservacionFinder evaluacionJuradoFinalizadaPorObservacionFinder;

    @Mock
    private RemoverObservacionItemJuradoValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RemoverObservacionItemJuradoUseCaseImpl useCase;

    @Test
    void debeEliminarEnOrden_cuandoTodoEsValido() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var remocion = RemocionObservacionItemJuradoDomain.crear(id);
        var observacion = ObservacionItemJuradoDomain.reconstruir(
                id, UtilUUID.generarNuevoUUID(), "Sustenta el puntaje otorgado");
        when(observacionItemJuradoPorIdFinder.obtener(id)).thenReturn(observacion);
        when(evaluacionJuradoFinalizadaPorObservacionFinder.obtener(id)).thenReturn(false);

        // Act
        useCase.ejecutar(remocion);

        // Assert
        var orden = inOrder(logger, observacionItemJuradoPorIdFinder, evaluacionJuradoFinalizadaPorObservacionFinder,
                validator, outputPort);
        orden.verify(logger).info(ObservacionItemJuradoKey.LOG_REMOVIENDO, id);
        orden.verify(observacionItemJuradoPorIdFinder).obtener(id);
        orden.verify(evaluacionJuradoFinalizadaPorObservacionFinder).obtener(id);
        orden.verify(logger).debug(ObservacionItemJuradoKey.LOG_VERIFICACION_REMOVER, true, false);
        orden.verify(validator).validar(remocion, observacion, false);
        orden.verify(outputPort).eliminar(id);
        orden.verify(logger).info(ObservacionItemJuradoKey.LOG_REMOVIDA, id);
        verify(observacionItemJuradoPorIdFinder, times(1)).obtener(any());
        verify(evaluacionJuradoFinalizadaPorObservacionFinder, times(1)).obtener(any());
    }
}
