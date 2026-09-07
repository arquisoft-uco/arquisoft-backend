package com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EliminarSolicitudNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.application.solicitud.command.usecase.EliminarSolicitudNovedadCoordinadorUseCase;
import com.arquisoft.solicitudes.domain.solicitud.EliminacionSolicitudNovedadCoordinadorDomain;
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
    private EliminarSolicitudNovedadCoordinadorUseCase useCase;

    @InjectMocks
    private EliminarSolicitudNovedadCoordinadorInteractorImpl interactor;

    @Test
    void debeMapearElComandoAObjetoDeAccionYDelegarEnElUseCase() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        UUID remitente = UUID.randomUUID();
        var command = EliminarSolicitudNovedadCoordinadorCommand.crear(
                solicitud.toString(), remitente.toString());

        // Act
        interactor.ejecutar(command);

        // Assert
        ArgumentCaptor<EliminacionSolicitudNovedadCoordinadorDomain> captor =
                ArgumentCaptor.forClass(EliminacionSolicitudNovedadCoordinadorDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getSolicitud()).isEqualTo(solicitud);
        assertThat(captor.getValue().getRemitenteUsuario()).isEqualTo(remitente);
    }
}
