package com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.application.solicitud.command.usecase.EnviarSolicitudNovedadCoordinadorUseCase;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudNovedadCoordinadorDomain;
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
class EnviarSolicitudNovedadCoordinadorInteractorImplTest {

    @Mock
    private EnviarSolicitudNovedadCoordinadorUseCase useCase;

    @InjectMocks
    private EnviarSolicitudNovedadCoordinadorInteractorImpl interactor;

    @Test
    void debeMapearElComandoAObjetoDeAccionYDelegarEnElUseCase() {
        // Arrange
        UUID remitente = UUID.randomUUID();
        UUID destinatario = UUID.randomUUID();
        UUID esperado = UUID.randomUUID();
        var command = EnviarSolicitudNovedadCoordinadorCommand.crear(
                remitente.toString(), destinatario.toString(), "novedad");
        when(useCase.ejecutar(any(EnvioSolicitudNovedadCoordinadorDomain.class))).thenReturn(esperado);

        // Act
        UUID resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isEqualTo(esperado);
        ArgumentCaptor<EnvioSolicitudNovedadCoordinadorDomain> captor =
                ArgumentCaptor.forClass(EnvioSolicitudNovedadCoordinadorDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getRemitenteUsuario()).isEqualTo(remitente);
        assertThat(captor.getValue().getDestinatarioUsuario()).isEqualTo(destinatario);
    }
}
