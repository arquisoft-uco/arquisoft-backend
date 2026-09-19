package com.arquisoft.evaluaciones.application.observacionitemjurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.EvaluacionCuantitativaJuradoPorIdFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.EvaluacionJuradoFinalizadaFinder;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.DescripcionObservacionItemJuradoExisteFinder;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.model.CriterioDescripcionObservacionItemJurado;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.ObservacionItemJuradoOutputPort;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.validator.RegistrarObservacionItemJuradoValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.EvaluacionCuantitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ObservacionItemJuradoDomain;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.DescripcionObservacionItemJuradoDuplicadaException;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.EvaluacionCuantitativaJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.EvaluacionJuradoFinalizadaException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ObservacionItemJuradoKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarObservacionItemJuradoUseCaseImplTest {

    @Mock
    private ObservacionItemJuradoOutputPort outputPort;

    @Mock
    private EvaluacionCuantitativaJuradoPorIdFinder evaluacionCuantitativaJuradoPorIdFinder;

    @Mock
    private EvaluacionJuradoFinalizadaFinder evaluacionJuradoFinalizadaFinder;

    @Mock
    private DescripcionObservacionItemJuradoExisteFinder descripcionObservacionItemJuradoExisteFinder;

    @Mock
    private RegistrarObservacionItemJuradoValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RegistrarObservacionItemJuradoUseCaseImpl useCase;

    @Test
    void debeRegistrarEnOrden_cuandoTodoEsValido() {
        // Arrange
        var evaluacionCuantitativaJurado = UUID.randomUUID();
        var evaluacionJurado = UUID.randomUUID();
        var observacion = ObservacionItemJuradoDomain.crear(
                evaluacionCuantitativaJurado, "Sustenta el puntaje otorgado");
        var evaluacion = EvaluacionCuantitativaJuradoDomain.reconstruir(
                evaluacionCuantitativaJurado, evaluacionJurado, UUID.randomUUID(), 300);

        when(evaluacionCuantitativaJuradoPorIdFinder.obtener(evaluacionCuantitativaJurado))
                .thenReturn(Optional.of(evaluacion));
        when(evaluacionJuradoFinalizadaFinder.obtener(evaluacionJurado)).thenReturn(false);
        when(descripcionObservacionItemJuradoExisteFinder.obtener(new CriterioDescripcionObservacionItemJurado(
                evaluacionCuantitativaJurado, observacion.getDescripcion())))
                .thenReturn(false);

        // Act
        var resultado = useCase.ejecutar(observacion);

        // Assert
        assertThat(resultado).isEqualTo(observacion.getId());
        InOrder orden = inOrder(logger, evaluacionCuantitativaJuradoPorIdFinder, evaluacionJuradoFinalizadaFinder,
                descripcionObservacionItemJuradoExisteFinder, validator, outputPort);
        orden.verify(logger).info(ObservacionItemJuradoKey.LOG_REGISTRANDO, evaluacionCuantitativaJurado);
        orden.verify(evaluacionCuantitativaJuradoPorIdFinder).obtener(evaluacionCuantitativaJurado);
        orden.verify(evaluacionJuradoFinalizadaFinder).obtener(evaluacionJurado);
        orden.verify(descripcionObservacionItemJuradoExisteFinder).obtener(new CriterioDescripcionObservacionItemJurado(
                evaluacionCuantitativaJurado, observacion.getDescripcion()));
        orden.verify(logger).debug(ObservacionItemJuradoKey.LOG_VERIFICACION_REGISTRAR, true, false, false);
        orden.verify(validator).validar(observacion, evaluacion, false, false);
        orden.verify(outputPort).registrar(any());
        orden.verify(logger).info(ObservacionItemJuradoKey.LOG_REGISTRADO, observacion.getId());
    }

    @Test
    void debeDetenerFlujo_cuandoEvaluacionNoExiste() {
        // Arrange
        var evaluacionCuantitativaJurado = UUID.randomUUID();
        var observacion = ObservacionItemJuradoDomain.crear(
                evaluacionCuantitativaJurado, "Sustenta el puntaje otorgado");

        when(evaluacionCuantitativaJuradoPorIdFinder.obtener(evaluacionCuantitativaJurado))
                .thenReturn(Optional.empty());
        when(evaluacionJuradoFinalizadaFinder.obtener(any())).thenReturn(false);
        when(descripcionObservacionItemJuradoExisteFinder.obtener(any())).thenReturn(false);
        doThrow(new EvaluacionCuantitativaJuradoNoEncontradaException(evaluacionCuantitativaJurado))
                .when(validator).validar(eq(observacion), any(), eq(false), eq(false));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(observacion))
                .isInstanceOf(EvaluacionCuantitativaJuradoNoEncontradaException.class);
        verify(outputPort, never()).registrar(any());
        verify(logger, never()).info(ObservacionItemJuradoKey.LOG_REGISTRADO, observacion.getId());
    }

    @Test
    void debeDetenerFlujo_cuandoEvaluacionJuradoFinalizada() {
        // Arrange
        var evaluacionCuantitativaJurado = UUID.randomUUID();
        var evaluacionJurado = UUID.randomUUID();
        var observacion = ObservacionItemJuradoDomain.crear(
                evaluacionCuantitativaJurado, "Sustenta el puntaje otorgado");
        var evaluacion = EvaluacionCuantitativaJuradoDomain.reconstruir(
                evaluacionCuantitativaJurado, evaluacionJurado, UUID.randomUUID(), 300);

        when(evaluacionCuantitativaJuradoPorIdFinder.obtener(evaluacionCuantitativaJurado))
                .thenReturn(Optional.of(evaluacion));
        when(evaluacionJuradoFinalizadaFinder.obtener(evaluacionJurado)).thenReturn(true);
        when(descripcionObservacionItemJuradoExisteFinder.obtener(any())).thenReturn(false);
        doThrow(new EvaluacionJuradoFinalizadaException(evaluacionCuantitativaJurado))
                .when(validator).validar(eq(observacion), eq(evaluacion), eq(true), eq(false));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(observacion))
                .isInstanceOf(EvaluacionJuradoFinalizadaException.class);
        verify(outputPort, never()).registrar(any());
    }

    @Test
    void debeDetenerFlujo_cuandoDescripcionYaExiste() {
        // Arrange
        var evaluacionCuantitativaJurado = UUID.randomUUID();
        var evaluacionJurado = UUID.randomUUID();
        var observacion = ObservacionItemJuradoDomain.crear(
                evaluacionCuantitativaJurado, "Sustenta el puntaje otorgado");
        var evaluacion = EvaluacionCuantitativaJuradoDomain.reconstruir(
                evaluacionCuantitativaJurado, evaluacionJurado, UUID.randomUUID(), 300);

        when(evaluacionCuantitativaJuradoPorIdFinder.obtener(evaluacionCuantitativaJurado))
                .thenReturn(Optional.of(evaluacion));
        when(evaluacionJuradoFinalizadaFinder.obtener(evaluacionJurado)).thenReturn(false);
        when(descripcionObservacionItemJuradoExisteFinder.obtener(any())).thenReturn(true);
        doThrow(new DescripcionObservacionItemJuradoDuplicadaException(evaluacionCuantitativaJurado))
                .when(validator).validar(eq(observacion), eq(evaluacion), eq(false), eq(true));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(observacion))
                .isInstanceOf(DescripcionObservacionItemJuradoDuplicadaException.class);
        verify(outputPort, never()).registrar(any());
    }
}
