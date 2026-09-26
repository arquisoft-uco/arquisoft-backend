package com.arquisoft.fichas.application.estadorevision.query.usecase.impl;

import com.arquisoft.fichas.application.estadorevision.query.readmodel.EstadoRevisionReadModel;
import com.arquisoft.fichas.application.estadorevision.query.secondaryport.EstadoRevisionQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstadoRevisionKey;
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
class ConsultarEstadosRevisionUseCaseImplTest {

    @Mock
    private EstadoRevisionQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEstadosRevisionUseCaseImpl useCase;

    private static List<EstadoRevisionReadModel> catalogoCompleto() {
        return List.of(
                new EstadoRevisionReadModel("NUEVA", "Nueva", "Creada recientemente, aun no revisada."),
                new EstadoRevisionReadModel("VISUALIZADA", "Visualizada", "Vista por el estudiante, sin accion aun."),
                new EstadoRevisionReadModel("EN_PROGRESO", "En Progreso", "En desarrollo, acciones en curso."),
                new EstadoRevisionReadModel("CORRECCION_DISPONIBLE", "Correccion Disponible",
                        "Trabajo completado, pendiente de validacion."),
                new EstadoRevisionReadModel("CERRADA", "Cerrada", "Completada y aprobada; estado terminal.")
        );
    }

    @Test
    void debeRetornarLosEstadosRevision_cuandoElPuertoDevuelveResultados() {
        // Arrange
        var esperados = catalogoCompleto();
        when(queryOutputPort.consultarTodos()).thenReturn(esperados);

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).hasSize(5);
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
        verify(logger).debug(EstadoRevisionKey.LOG_CONSULTA_COMPLETADA, 5);
    }
}
