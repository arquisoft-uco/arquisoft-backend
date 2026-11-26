package com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.impl;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.EstadoFichaDeEvaluacionFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.EvaluacionFichaExisteFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.RepresentantePropietarioEvaluacionFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.UltimoEstadoEvaluacionFichaFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.DescartarEvaluacionFichaValidator;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionNoDescartableException;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DescartarEvaluacionFichaUseCaseImplTest {

    @Mock
    private EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;

    @Mock
    private EvaluacionFichaExisteFinder evaluacionFichaExisteFinder;

    @Mock
    private RepresentantePropietarioEvaluacionFinder representantePropietarioEvaluacionFinder;

    @Mock
    private UltimoEstadoEvaluacionFichaFinder ultimoEstadoEvaluacionFichaFinder;

    @Mock
    private EstadoFichaDeEvaluacionFinder estadoFichaDeEvaluacionFinder;

    @Mock
    private DescartarEvaluacionFichaValidator descartarEvaluacionFichaValidator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private DescartarEvaluacionFichaUseCaseImpl useCase;

    private UUID evaluacion;
    private AgregacionEstadoEvaluacionFichaDomain entrada;
    private EstadoEvaluacionFichaDomain ultimoEstado;
    private EstadoActualFicha estadoFicha;

    @BeforeEach
    void preparar() {
        evaluacion = UtilUUID.generarNuevoUUID();
        var representante = UtilUUID.generarNuevoUUID();
        entrada = AgregacionEstadoEvaluacionFichaDomain.crear(
                EstadoEvaluacionFichaDomain.crearConEstado(evaluacion, EstadoEvaluacion.DESCARTADA.getId()),
                representante);
        ultimoEstado = EstadoEvaluacionFichaDomain.crearConEstado(evaluacion, EstadoEvaluacion.EN_EVALUACION.getId());
        estadoFicha = new EstadoActualFicha(UtilUUID.generarNuevoUUID(), EstadoFicha.DISPONIBLE_PARA_EVALUACION);

        when(evaluacionFichaExisteFinder.obtener(evaluacion)).thenReturn(true);
        when(representantePropietarioEvaluacionFinder.obtener(entrada)).thenReturn(true);
        when(ultimoEstadoEvaluacionFichaFinder.obtener(evaluacion)).thenReturn(ultimoEstado);
        when(estadoFichaDeEvaluacionFinder.obtener(evaluacion)).thenReturn(estadoFicha);
    }

    @Test
    void debePersistirEstado_cuandoValidacionPasa() {
        // Act
        useCase.ejecutar(entrada);

        // Assert
        var orden = inOrder(evaluacionFichaExisteFinder, representantePropietarioEvaluacionFinder,
                ultimoEstadoEvaluacionFichaFinder, estadoFichaDeEvaluacionFinder,
                descartarEvaluacionFichaValidator, estadoEvaluacionFichaOutputPort);
        orden.verify(evaluacionFichaExisteFinder, times(1)).obtener(evaluacion);
        orden.verify(representantePropietarioEvaluacionFinder, times(1)).obtener(entrada);
        orden.verify(ultimoEstadoEvaluacionFichaFinder, times(1)).obtener(evaluacion);
        orden.verify(estadoFichaDeEvaluacionFinder, times(1)).obtener(evaluacion);
        orden.verify(descartarEvaluacionFichaValidator)
                .validar(entrada, true, true, ultimoEstado, estadoFicha);
        orden.verify(estadoEvaluacionFichaOutputPort).agregarEstado(argThat(estado ->
                estado.evaluacionFichaPerfil().equals(evaluacion)
                        && estado.estadoEvaluacion().equals(EstadoEvaluacion.DESCARTADA.getId())));
    }

    @Test
    void debeNoPersistirNiCerrar_cuandoValidatorLanza() {
        // Arrange
        doThrow(new EvaluacionNoDescartableException(evaluacion, EstadoEvaluacion.APROBADA))
                .when(descartarEvaluacionFichaValidator)
                .validar(entrada, true, true, ultimoEstado, estadoFicha);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(EvaluacionNoDescartableException.class);

        verify(estadoEvaluacionFichaOutputPort, never()).agregarEstado(any());
        verify(logger, never()).info(eq(EstadoEvaluacionFichaKey.LOG_DESCARTADA), eq(evaluacion), any());
    }
}
