package com.arquisoft.mapas_ruta.application.estadoitem.query.usecase.impl;

import com.arquisoft.mapas_ruta.application.estadoitem.query.readmodel.EstadoItemReadModel;
import com.arquisoft.mapas_ruta.application.estadoitem.query.secondaryport.EstadoItemQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.EstadoItemKey;
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
class ConsultarEstadosItemUseCaseImplTest {

    @Mock
    private EstadoItemQueryOutputPort outputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEstadosItemUseCaseImpl useCase;

    @Test
    void debeRetornarLosEstadosYRegistrarElTotal_cuandoElPuertoLosDevuelve() {
        // Arrange
        var estados = List.of(
                new EstadoItemReadModel("NUEVA", "Nueva", "Sin iniciar"),
                new EstadoItemReadModel("EN_PROGRESO", "En progreso", "En curso"),
                new EstadoItemReadModel("FINALIZADA", "Finalizada", "Terminada"));
        when(outputPort.findAll()).thenReturn(estados);

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isSameAs(estados);
        verify(outputPort, times(1)).findAll();
        verify(logger).debug(eq(EstadoItemKey.LOG_CONSULTA_COMPLETADA), eq(3));
    }

    @Test
    void debeRetornarListaVacia_cuandoElCatalogoEstaVacio() {
        // Arrange
        when(outputPort.findAll()).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(eq(EstadoItemKey.LOG_CONSULTA_COMPLETADA), eq(0));
    }
}
