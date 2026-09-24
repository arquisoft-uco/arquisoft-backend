package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.EliminarRespuestaNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.EliminarRespuestaNovedadCoordinadorUseCase;
import com.arquisoft.solicitudes.domain.respuesta.EliminacionRespuestaNovedadCoordinadorDomain;
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
class EliminarRespuestaNovedadCoordinadorInteractorImplTest {

    @Mock
    private EliminarRespuestaNovedadCoordinadorUseCase useCase;

    @InjectMocks
    private EliminarRespuestaNovedadCoordinadorInteractorImpl interactor;

    @Test
    void debeMapearYDelegarEnElUseCase_cuandoEjecutaElComando() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var coordinador = UUID.randomUUID();
        var command = EliminarRespuestaNovedadCoordinadorCommand.crear(
                solicitud.toString(), coordinador);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(EliminacionRespuestaNovedadCoordinadorDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getSolicitud()).isEqualTo(solicitud);
        assertThat(captor.getValue().getCoordinadorUsuario()).isEqualTo(coordinador);
    }
}
