package com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudAmpliacionPlazoCommand;
import com.arquisoft.solicitudes.application.solicitud.command.usecase.EnviarSolicitudUseCase;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudDomain;
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
class EnviarSolicitudAmpliacionPlazoInteractorImplTest {

    @Mock
    private EnviarSolicitudUseCase useCase;

    @InjectMocks
    private EnviarSolicitudAmpliacionPlazoInteractorImpl interactor;

    @Test
    void debeMapearElComandoAObjetoDeAccionYDelegarEnElUseCase() {
        // Arrange
        UUID remitente = UUID.randomUUID();
        UUID destinatario = UUID.randomUUID();
        UUID esperado = UUID.randomUUID();
        var command = EnviarSolicitudAmpliacionPlazoCommand.crear(
                remitente, destinatario.toString(), "ampliacion de plazo");
        when(useCase.ejecutar(any(EnvioSolicitudDomain.class))).thenReturn(esperado);

        // Act
        UUID resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isEqualTo(esperado);
        ArgumentCaptor<EnvioSolicitudDomain> captor =
                ArgumentCaptor.forClass(EnvioSolicitudDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getRemitenteUsuario()).isEqualTo(remitente);
        assertThat(captor.getValue().getDestinatarioUsuario()).isEqualTo(destinatario);
    }
}
