package com.arquisoft.solicitudes.application.estadorespuesta.query.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.EstadoRespuestaKey;
import com.arquisoft.solicitudes.application.estadorespuesta.query.readmodel.EstadoRespuestaReadModel;
import com.arquisoft.solicitudes.application.estadorespuesta.query.secondaryport.EstadoRespuestaQueryOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadosRespuestaUseCaseImplTest {

    @Mock
    private EstadoRespuestaQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEstadosRespuestaUseCaseImpl useCase;

    @Test
    void debeRetornarListaCompletaEnElOrdenDelPuerto_cuandoExistenEstados() {
        // Arrange
        var estados = List.of(
                new EstadoRespuestaReadModel("EN_REVISION", "En revisión", "En proceso de evaluación"),
                new EstadoRespuestaReadModel("APROBADA", "Aprobada", "Cumple los criterios"),
                new EstadoRespuestaReadModel("NO_APROBADA", "No aprobada", "No cumple los criterios"));
        when(queryOutputPort.findAll()).thenReturn(estados);

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).containsExactlyElementsOf(estados);
        verify(queryOutputPort, times(1)).findAll();
        verify(logger).debug(eq(EstadoRespuestaKey.LOG_CONSULTA_COMPLETADA), eq(3));
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayEstados() {
        // Arrange
        when(queryOutputPort.findAll()).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isEmpty();
        verify(queryOutputPort, times(1)).findAll();
        verify(logger).debug(eq(EstadoRespuestaKey.LOG_CONSULTA_COMPLETADA), eq(0));
    }
}
