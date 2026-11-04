package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.EliminarRespuestaRegistroModificacionUsuariosCommand;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.EliminarRespuestaUseCase;
import com.arquisoft.solicitudes.domain.respuesta.EliminacionRespuestaDomain;
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
class EliminarRespuestaRegistroModificacionUsuariosInteractorImplTest {

    @Mock
    private EliminarRespuestaUseCase useCase;

    @InjectMocks
    private EliminarRespuestaRegistroModificacionUsuariosInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElDominioMapeado() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var administrador = UtilUUID.generarNuevoUUID();
        var command = EliminarRespuestaRegistroModificacionUsuariosCommand.crear(solicitud.toString(), administrador);
        var captor = ArgumentCaptor.forClass(EliminacionRespuestaDomain.class);

        // Act
        interactor.ejecutar(command);

        // Assert
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getSolicitud()).isEqualTo(solicitud);
        assertThat(captor.getValue().getResponsableUsuario()).isEqualTo(administrador);
        assertThat(captor.getValue().getTipoEsperado()).isEqualTo(TipoSolicitud.REGISTRO_Y_MODIFICACION_DE_USUARIOS);
    }
}
