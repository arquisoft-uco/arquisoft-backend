package com.arquisoft.evaluaciones.application.evaluacionjurado.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionesAsignadasJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionAsignadaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.secondaryport.EvaluacionAsignadaJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.validator.ConsultarEvaluacionesAsignadasJuradoValidator;
import com.arquisoft.evaluaciones.application.jurado.query.finder.JuradoExisteQueryFinder;
import com.arquisoft.evaluaciones.domain.jurado.exception.JuradoNoEncontradoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionJuradoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
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
                new EvaluacionAsignadaJuradoReadModel.Estado("PENDIENTE_POR_EVALUAR", "Pendiente por evaluar"),
                new EvaluacionAsignadaJuradoReadModel.Evaluacion(UtilUUID.generarNuevoUUID(),
                        new EvaluacionAsignadaJuradoReadModel.Estado("PENDIENTE", "Pendiente")),
                new EvaluacionAsignadaJuradoReadModel.Entregable(UtilUUID.generarNuevoUUID(), "Proyecto", 1));
    }

    private static EvaluacionesAsignadasJuradoCriteria criteria(UUID jurado) {
        return EvaluacionesAsignadasJuradoCriteria.builder().jurado(jurado).pagina(0).tamanio(10).build();
    }

    @Test
    void debeRetornarPaginaDeAsignaciones_cuandoElJuradoExiste() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var criteria = criteria(jurado);
        var esperado = PaginatedResult.of(List.of(asignacion(), asignacion(), asignacion()), 0, 10, 3);
        when(juradoExisteQueryFinder.obtener(jurado)).thenReturn(true);
        when(queryOutputPort.consultarTodas(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var orden = inOrder(juradoExisteQueryFinder, validator, queryOutputPort);
        orden.verify(juradoExisteQueryFinder).obtener(jurado);
        orden.verify(validator).validar(jurado, true);
        orden.verify(queryOutputPort).consultarTodas(criteria);
        verify(logger).debug(EvaluacionJuradoKey.LOG_CONSULTA_ASIGNADAS_COMPLETADA, 3L, 0, 10);
    }

    @Test
    void debeRetornarPaginaVacia_cuandoNoHayAsignaciones() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var criteria = criteria(jurado);
        when(juradoExisteQueryFinder.obtener(jurado)).thenReturn(true);
        when(queryOutputPort.consultarTodas(criteria))
                .thenReturn(PaginatedResult.<EvaluacionAsignadaJuradoReadModel>of(List.of(), 0, 10, 0));

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
    }

    @Test
    void debePropagarRechazoYNoLeer_cuandoElValidatorLanza() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var criteria = criteria(jurado);
        when(juradoExisteQueryFinder.obtener(jurado)).thenReturn(false);
        doThrow(new JuradoNoEncontradoException(jurado)).when(validator).validar(jurado, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(criteria))
                .isInstanceOf(JuradoNoEncontradoException.class);
        verify(queryOutputPort, never()).consultarTodas(any());
    }
}
