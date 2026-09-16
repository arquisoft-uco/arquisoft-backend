package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.ModificarEstadoRespuestaNovedadCoordinadorUseCase;
import com.arquisoft.solicitudes.domain.respuesta.ModificacionEstadoRespuestaNovedadCoordinadorDomain;
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
class ModificarEstadoRespuestaNovedadCoordinadorInteractorImplTest {

    @Mock
    private ModificarEstadoRespuestaNovedadCoordinadorUseCase useCase;

    @InjectMocks
    private ModificarEstadoRespuestaNovedadCoordinadorInteractorImpl interactor;

    @Test
    void debeMapearElComandoAObjetoDeAccionYDelegarEnElUseCase() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var coordinador = UUID.randomUUID();
        var command = ModificarEstadoRespuestaNovedadCoordinadorCommand.crear(
                solicitud.toString(), "APROBADA", coordinador);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(ModificacionEstadoRespuestaNovedadCoordinadorDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getSolicitud()).isEqualTo(solicitud);
        assertThat(captor.getValue().getCoordinadorUsuario()).isEqualTo(coordinador);
        assertThat(captor.getValue().getNuevoEstado()).isEqualTo("APROBADA");
    }
}
