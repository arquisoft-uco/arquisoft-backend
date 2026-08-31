package com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudCambioAsesorCommand;
import com.arquisoft.solicitudes.application.solicitud.command.usecase.EnviarSolicitudCambioAsesorUseCase;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudCambioAsesorDomain;
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
class EnviarSolicitudCambioAsesorInteractorImplTest {

    @Mock
    private EnviarSolicitudCambioAsesorUseCase useCase;

    @InjectMocks
    private EnviarSolicitudCambioAsesorInteractorImpl interactor;

    @Test
    void debeMapearElComandoAObjetoDeAccionYDelegarEnElUseCase() {
        // Arrange
        UUID remitente = UUID.randomUUID();
        UUID destinatario = UUID.randomUUID();
        UUID esperado = UUID.randomUUID();
        var command = EnviarSolicitudCambioAsesorCommand.crear(
                remitente.toString(), destinatario.toString(), "cambio de asesor");
        when(useCase.ejecutar(any(EnvioSolicitudCambioAsesorDomain.class))).thenReturn(esperado);

        // Act
        UUID resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isEqualTo(esperado);
        ArgumentCaptor<EnvioSolicitudCambioAsesorDomain> captor =
                ArgumentCaptor.forClass(EnvioSolicitudCambioAsesorDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getRemitenteUsuario()).isEqualTo(remitente);
        assertThat(captor.getValue().getDestinatarioUsuario()).isEqualTo(destinatario);
    }
}
