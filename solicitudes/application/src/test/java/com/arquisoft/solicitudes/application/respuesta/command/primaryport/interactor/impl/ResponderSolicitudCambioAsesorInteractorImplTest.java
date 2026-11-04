package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudCambioAsesorCommand;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.ResponderSolicitudUseCase;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaSolicitudDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
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
class ResponderSolicitudCambioAsesorInteractorImplTest {

    @Mock
    private ResponderSolicitudUseCase useCase;

    @InjectMocks
    private ResponderSolicitudCambioAsesorInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElDominioMapeado_cuandoSeEjecuta() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var esperado = UtilUUID.generarNuevoUUID();
        var command = ResponderSolicitudCambioAsesorCommand.crear(
                solicitud.toString(), "una respuesta", coordinador);
        when(useCase.ejecutar(any(RespuestaSolicitudDomain.class))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isEqualTo(esperado);
        var captor = ArgumentCaptor.forClass(RespuestaSolicitudDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getSolicitud()).isEqualTo(solicitud);
        assertThat(captor.getValue().getRespuesta().getContenido()).isEqualTo("una respuesta");
        assertThat(captor.getValue().getResponsableUsuario()).isEqualTo(coordinador);
        assertThat(captor.getValue().getTipoEsperado()).isEqualTo(TipoSolicitud.CAMBIO_DE_ASESOR);
    }
}
