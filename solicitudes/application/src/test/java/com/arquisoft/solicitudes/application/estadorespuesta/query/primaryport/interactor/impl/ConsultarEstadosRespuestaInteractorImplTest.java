package com.arquisoft.solicitudes.application.estadorespuesta.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.estadorespuesta.query.readmodel.EstadoRespuestaReadModel;
import com.arquisoft.solicitudes.application.estadorespuesta.query.usecase.ConsultarEstadosRespuestaUseCase;
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
class ConsultarEstadosRespuestaInteractorImplTest {

    @Mock
    private ConsultarEstadosRespuestaUseCase consultarEstadosRespuestaUseCase;

    @InjectMocks
    private ConsultarEstadosRespuestaInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCase_yRetornarSuResultado() {
        // Arrange
        var esperado = List.of(new EstadoRespuestaReadModel("APROBADA", "Aprobada", "Cumple"));
        when(consultarEstadosRespuestaUseCase.ejecutar()).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar();

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarEstadosRespuestaUseCase).ejecutar();
    }
}
