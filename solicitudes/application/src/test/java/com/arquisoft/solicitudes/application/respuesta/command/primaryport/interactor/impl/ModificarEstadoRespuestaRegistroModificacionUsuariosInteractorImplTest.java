package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaRegistroModificacionUsuariosCommand;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.ModificarEstadoRespuestaUseCase;
import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import com.arquisoft.solicitudes.domain.respuesta.ModificacionEstadoRespuestaDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ModificarEstadoRespuestaRegistroModificacionUsuariosInteractorImplTest {

    @Mock
    private ModificarEstadoRespuestaUseCase useCase;

    @InjectMocks
    private ModificarEstadoRespuestaRegistroModificacionUsuariosInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElDominio_cuandoEjecuta() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var administrador = UtilUUID.generarNuevoUUID();
        var command = ModificarEstadoRespuestaRegistroModificacionUsuariosCommand.crear(
                solicitud, "APROBADA", administrador);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(ModificacionEstadoRespuestaDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getSolicitud()).isEqualTo(solicitud);
        assertThat(captor.getValue().getResponsableUsuario()).isEqualTo(administrador);
        assertThat(captor.getValue().getTipoEsperado()).isEqualTo(TipoSolicitud.REGISTRO_Y_MODIFICACION_DE_USUARIOS);
        assertThat(captor.getValue().getNuevoEstado()).isEqualTo(EstadoRespuesta.APROBADA);
    }
}
