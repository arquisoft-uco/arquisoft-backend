package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.ResponderSolicitudNovedadCoordinadorUseCase;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaNovedadCoordinadorDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResponderSolicitudNovedadCoordinadorInteractorImplTest {

    @Mock
    private ResponderSolicitudNovedadCoordinadorUseCase useCase;

    @InjectMocks
    private ResponderSolicitudNovedadCoordinadorInteractorImpl interactor;

    @Test
    void debeMapearElComandoAObjetoDeAccionYDelegarEnElUseCase() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        UUID coordinador = UUID.randomUUID();
        UUID esperado = UUID.randomUUID();
        var command = ResponderSolicitudNovedadCoordinadorCommand.crear(
                solicitud.toString(), "una respuesta", coordinador);
        when(useCase.ejecutar(any(RespuestaNovedadCoordinadorDomain.class))).thenReturn(esperado);

        // Act
        UUID resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isEqualTo(esperado);
        ArgumentCaptor<RespuestaNovedadCoordinadorDomain> captor =
                ArgumentCaptor.forClass(RespuestaNovedadCoordinadorDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getSolicitud()).isEqualTo(solicitud);
        assertThat(captor.getValue().getContenido()).isEqualTo("una respuesta");
        assertThat(captor.getValue().getCoordinadorUsuario()).isEqualTo(coordinador);
    }
}
