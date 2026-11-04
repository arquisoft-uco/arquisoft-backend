package com.arquisoft.fichas.application.estadoobservacionrevision.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estadoobservacionrevision.query.readmodel.EstadoObservacionRevisionReadModel;
import com.arquisoft.fichas.application.estadoobservacionrevision.query.usecase.ConsultarEstadosObservacionRevisionUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadosObservacionRevisionInteractorImplTest {

    @Mock
    private ConsultarEstadosObservacionRevisionUseCase consultarEstadosObservacionRevisionUseCase;

    @InjectMocks
    private ConsultarEstadosObservacionRevisionInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_cuandoSeEjecuta() {
        // Arrange
        var esperados = List.of(
                new EstadoObservacionRevisionReadModel("PENDIENTE", "Pendiente",
                        "La observacion revisión ha sido registrada, pero aun no se ha iniciado ninguna accion sobre ella."));
        when(consultarEstadosObservacionRevisionUseCase.ejecutar()).thenReturn(esperados);

        // Act
        var resultado = interactor.ejecutar();

        // Assert
        assertThat(resultado).isSameAs(esperados);
        verify(consultarEstadosObservacionRevisionUseCase).ejecutar();
    }
}
