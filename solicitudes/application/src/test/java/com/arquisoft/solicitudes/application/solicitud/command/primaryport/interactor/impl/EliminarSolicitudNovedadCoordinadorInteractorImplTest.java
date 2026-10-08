package com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EliminarSolicitudNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.application.solicitud.command.usecase.EliminarSolicitudUseCase;
import com.arquisoft.solicitudes.domain.solicitud.EliminacionSolicitudDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EliminarSolicitudNovedadCoordinadorInteractorImplTest {

    @Mock
    private EliminarSolicitudUseCase useCase;

    @InjectMocks
    private EliminarSolicitudNovedadCoordinadorInteractorImpl interactor;

    @Test
    void debeMapearElComandoAObjetoDeAccionYDelegarEnElUseCase() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var remitente = UUID.randomUUID();
        var command = EliminarSolicitudNovedadCoordinadorCommand.crear(
                solicitud.toString(), remitente);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor =
                ArgumentCaptor.forClass(EliminacionSolicitudDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getSolicitud()).isEqualTo(solicitud);
        assertThat(captor.getValue().getRemitenteUsuario()).isEqualTo(remitente);
    }
}
