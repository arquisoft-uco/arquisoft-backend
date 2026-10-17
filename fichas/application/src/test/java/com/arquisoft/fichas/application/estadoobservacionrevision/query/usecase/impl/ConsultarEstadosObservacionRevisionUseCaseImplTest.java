package com.arquisoft.fichas.application.estadoobservacionrevision.query.usecase.impl;

import com.arquisoft.fichas.application.estadoobservacionrevision.query.readmodel.EstadoObservacionRevisionReadModel;
import com.arquisoft.fichas.application.estadoobservacionrevision.query.secondaryport.EstadoObservacionRevisionQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstadoObservacionRevisionKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadosObservacionRevisionUseCaseImplTest {

    @Mock
    private EstadoObservacionRevisionQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEstadosObservacionRevisionUseCaseImpl useCase;

    private static List<EstadoObservacionRevisionReadModel> catalogoCompleto() {
        return List.of(
                new EstadoObservacionRevisionReadModel("PENDIENTE", "Pendiente",
                        "La observacion revisión ha sido registrada, pero aun no se ha iniciado ninguna accion sobre ella."),
                new EstadoObservacionRevisionReadModel("EN_PROGRESO", "En Progreso",
                        "La observacion revisión esta siendo trabajada activamente."),
                new EstadoObservacionRevisionReadModel("CERRADO", "Cerrado",
                        "La observacion revisión ha sido completada y no requiere mas acciones.")
        );
    }

    @Test
    void debeRetornarLosEstadosObservacionRevision_cuandoElPuertoDevuelveResultados() {
        // Arrange
        var esperados = catalogoCompleto();
        when(queryOutputPort.consultarTodos()).thenReturn(esperados);

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).hasSize(3);
        assertThat(resultado).containsExactlyElementsOf(esperados);
    }

    @Test
    void debeRetornarListaVacia_cuandoElPuertoNoDevuelveNada() {
        // Arrange
        when(queryOutputPort.consultarTodos()).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeDelegarEnElPuerto_sinTransformarElResultado() {
        // Arrange
        var esperados = catalogoCompleto();
        when(queryOutputPort.consultarTodos()).thenReturn(esperados);

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isSameAs(esperados);
        verify(queryOutputPort, times(1)).consultarTodos();
    }

    @Test
    void debeRegistrarElTotalEnLog_cuandoConsultaCompleta() {
        // Arrange
        when(queryOutputPort.consultarTodos()).thenReturn(catalogoCompleto());

        // Act
        useCase.ejecutar();

        // Assert
        verify(logger).debug(EstadoObservacionRevisionKey.LOG_CONSULTA_COMPLETADA, 3);
    }
}
