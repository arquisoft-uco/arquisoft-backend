package com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudRegistroModificacionUsuariosCommand;
import com.arquisoft.solicitudes.application.solicitud.command.usecase.EnviarSolicitudUseCase;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnviarSolicitudRegistroModificacionUsuariosInteractorImplTest {

    @Mock
    private EnviarSolicitudUseCase useCase;

    @InjectMocks
    private EnviarSolicitudRegistroModificacionUsuariosInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElDominioMapeado_cuandoEjecuta() {
        // Arrange
        var remitente = UtilUUID.generarNuevoUUID();
        var destinatario = UtilUUID.generarNuevoUUID();
        var esperado = UtilUUID.generarNuevoUUID();
        var command = EnviarSolicitudRegistroModificacionUsuariosCommand.crear(
                remitente, destinatario.toString(), "registro y modificacion de usuarios");
        when(useCase.ejecutar(any(EnvioSolicitudDomain.class))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isEqualTo(esperado);
        var captor =
                ArgumentCaptor.forClass(EnvioSolicitudDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getRemitenteUsuario()).isEqualTo(remitente);
        assertThat(captor.getValue().getDestinatarioUsuario()).isEqualTo(destinatario);
    }
}
