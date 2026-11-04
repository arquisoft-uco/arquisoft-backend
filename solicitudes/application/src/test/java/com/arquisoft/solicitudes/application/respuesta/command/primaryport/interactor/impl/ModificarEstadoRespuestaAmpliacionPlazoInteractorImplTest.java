package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaAmpliacionPlazoCommand;
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
class ModificarEstadoRespuestaAmpliacionPlazoInteractorImplTest {

    @Mock
    private ModificarEstadoRespuestaUseCase useCase;

    @InjectMocks
    private ModificarEstadoRespuestaAmpliacionPlazoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElDominio_cuandoEjecuta() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var command = ModificarEstadoRespuestaAmpliacionPlazoCommand.crear(
                solicitud.toString(), "APROBADA", coordinador);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(ModificacionEstadoRespuestaDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getSolicitud()).isEqualTo(solicitud);
        assertThat(captor.getValue().getResponsableUsuario()).isEqualTo(coordinador);
        assertThat(captor.getValue().getTipoEsperado()).isEqualTo(TipoSolicitud.AMPLIACION_DE_PLAZO);
        assertThat(captor.getValue().getNuevoEstado()).isEqualTo(EstadoRespuesta.APROBADA);
    }
}
