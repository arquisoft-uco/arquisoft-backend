package com.arquisoft.fichas.application.estadoevaluacion.query.usecase.impl;

import com.arquisoft.fichas.application.estadoevaluacion.query.readmodel.EstadoEvaluacionReadModel;
import com.arquisoft.fichas.application.estadoevaluacion.query.secondaryport.EstadoEvaluacionQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionKey;
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
class ConsultarEstadosEvaluacionUseCaseImplTest {

    @Mock
    private EstadoEvaluacionQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEstadosEvaluacionUseCaseImpl useCase;

    private static List<EstadoEvaluacionReadModel> catalogoCompleto() {
        return List.of(
                new EstadoEvaluacionReadModel("EN_EVALUACION", "En Evaluacion",
                        "Se refiere a que la ficha de perfil se encuentra en evaluacion por un representante del comite."),
                new EstadoEvaluacionReadModel("APROBADA", "Aprobada",
                        "Se refiere a que la ficha de perfil paso por revision y fue aprobada."),
                new EstadoEvaluacionReadModel("APROBADA_CON_OBSERVACIONES", "Aprobada Con Observaciones",
                        "Se refiere a que la ficha de perfil fue aprobada con observaciones."),
                new EstadoEvaluacionReadModel("NO_APROBADA", "No Aprobada",
                        "Se refiere a que la ficha de perfil no fue aprobada."),
                new EstadoEvaluacionReadModel("DESCARTADA", "Descartada",
                        "Se refiere a que la ficha de perfil fue descartada.")
        );
    }

    @Test
    void debeRetornarTodosLosEstados_cuandoElPuertoDevuelveResultados() {
        // Arrange
        List<EstadoEvaluacionReadModel> esperados = catalogoCompleto();
        when(queryOutputPort.consultarTodos()).thenReturn(esperados);

        // Act
        List<EstadoEvaluacionReadModel> resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).hasSize(5);
        assertThat(resultado).containsExactlyElementsOf(esperados);
    }

    @Test
    void debeRetornarListaVacia_cuandoElPuertoNoDevuelveNada() {
        // Arrange
        when(queryOutputPort.consultarTodos()).thenReturn(List.of());

        // Act
        List<EstadoEvaluacionReadModel> resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeDelegarEnElPuerto_sinTransformarElResultado() {
        // Arrange
        List<EstadoEvaluacionReadModel> esperados = catalogoCompleto();
        when(queryOutputPort.consultarTodos()).thenReturn(esperados);

        // Act
        List<EstadoEvaluacionReadModel> resultado = useCase.ejecutar();

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
        verify(logger).debug(EstadoEvaluacionKey.LOG_CONSULTA_COMPLETADA, 5);
    }
}
