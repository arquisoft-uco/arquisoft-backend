package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudNovedadAsesorCommand;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.ResponderSolicitudNovedadAsesorUseCase;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaNovedadAsesorDomain;
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
class ResponderSolicitudNovedadAsesorInteractorImplTest {

    @Mock
    private ResponderSolicitudNovedadAsesorUseCase useCase;

    @InjectMocks
    private ResponderSolicitudNovedadAsesorInteractorImpl interactor;

    @Test
    void debeMapearElComandoAObjetoDeAccionYDelegarEnElUseCase() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        UUID asesor = UUID.randomUUID();
        UUID esperado = UUID.randomUUID();
        var command = ResponderSolicitudNovedadAsesorCommand.crear(
                solicitud.toString(), "una respuesta", asesor);
        when(useCase.ejecutar(any(RespuestaNovedadAsesorDomain.class))).thenReturn(esperado);

        // Act
        UUID resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isEqualTo(esperado);
        ArgumentCaptor<RespuestaNovedadAsesorDomain> captor =
                ArgumentCaptor.forClass(RespuestaNovedadAsesorDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getSolicitud()).isEqualTo(solicitud);
        assertThat(captor.getValue().getContenido()).isEqualTo("una respuesta");
        assertThat(captor.getValue().getAsesorUsuario()).isEqualTo(asesor);
    }
}
