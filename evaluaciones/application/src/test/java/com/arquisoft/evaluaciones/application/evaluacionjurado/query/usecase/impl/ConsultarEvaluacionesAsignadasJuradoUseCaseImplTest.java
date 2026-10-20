package com.arquisoft.evaluaciones.application.evaluacionjurado.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionesAsignadasJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionAsignadaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.secondaryport.EvaluacionAsignadaJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.validator.ConsultarEvaluacionesAsignadasJuradoValidator;
import com.arquisoft.evaluaciones.application.jurado.query.finder.JuradoExisteQueryFinder;
import com.arquisoft.evaluaciones.domain.jurado.exception.JuradoNoEncontradoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEvaluacionesAsignadasJuradoUseCaseImplTest {

    @Mock
    private JuradoExisteQueryFinder juradoExisteQueryFinder;

    @Mock
    private ConsultarEvaluacionesAsignadasJuradoValidator validator;

    @Mock
    private EvaluacionAsignadaJuradoQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEvaluacionesAsignadasJuradoUseCaseImpl useCase;

    private static EvaluacionAsignadaJuradoReadModel asignacion() {
        return new EvaluacionAsignadaJuradoReadModel(
                UtilUUID.generarNuevoUUID(),
                new EvaluacionAsignadaJuradoReadModel.Evaluacion(UtilUUID.generarNuevoUUID(),
                        new EvaluacionAsignadaJuradoReadModel.Estado("PENDIENTE", "Pendiente")),
                new EvaluacionAsignadaJuradoReadModel.Entregable(UtilUUID.generarNuevoUUID(), "Proyecto", 1));
    }

    @Test
    void debeRetornarAsignaciones_cuandoElJuradoExiste() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var criteria = new EvaluacionesAsignadasJuradoCriteria(jurado);
        var esperado = List.of(asignacion(), asignacion(), asignacion());
        when(juradoExisteQueryFinder.obtener(jurado)).thenReturn(true);
        when(queryOutputPort.consultar(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var orden = inOrder(juradoExisteQueryFinder, validator, queryOutputPort);
        orden.verify(juradoExisteQueryFinder).obtener(jurado);
        orden.verify(validator).validar(jurado, true);
        orden.verify(queryOutputPort).consultar(criteria);
        verify(juradoExisteQueryFinder, times(1)).obtener(jurado);
        verify(queryOutputPort, times(1)).consultar(criteria);
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayAsignaciones() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var criteria = new EvaluacionesAsignadasJuradoCriteria(jurado);
        when(juradoExisteQueryFinder.obtener(jurado)).thenReturn(true);
        when(queryOutputPort.consultar(criteria)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debePropagarRechazoYNoLeer_cuandoElValidatorLanza() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var criteria = new EvaluacionesAsignadasJuradoCriteria(jurado);
        when(juradoExisteQueryFinder.obtener(jurado)).thenReturn(false);
        doThrow(new JuradoNoEncontradoException(jurado)).when(validator).validar(jurado, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(criteria))
                .isInstanceOf(JuradoNoEncontradoException.class);
        verify(queryOutputPort, never()).consultar(any());
    }
}
