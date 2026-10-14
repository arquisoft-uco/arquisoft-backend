package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.EliminarRespuestaCambioAsesorCommand;
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
class EliminarRespuestaCambioAsesorInteractorImplTest {

    @Mock
    private EliminarRespuestaUseCase useCase;

    @InjectMocks
    private EliminarRespuestaCambioAsesorInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElDominioMapeado_cuandoEjecutaElComando() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var command = EliminarRespuestaCambioAsesorCommand.crear(solicitud.toString(), coordinador);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(EliminacionRespuestaDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getSolicitud()).isEqualTo(solicitud);
        assertThat(captor.getValue().getResponsableUsuario()).isEqualTo(coordinador);
        assertThat(captor.getValue().getTipoEsperado()).isEqualTo(TipoSolicitud.CAMBIO_DE_ASESOR);
    }
}
