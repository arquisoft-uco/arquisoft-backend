package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaNovedadAsesorCommand;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ModificarEstadoRespuestaNovedadAsesorInteractorImplTest {

    @Mock
    private ModificarEstadoRespuestaUseCase useCase;

    @InjectMocks
    private ModificarEstadoRespuestaNovedadAsesorInteractorImpl interactor;

    @Test
    void debeMapearElComandoAObjetoDeAccionYDelegarEnElUseCase() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var asesor = UUID.randomUUID();
        var command = ModificarEstadoRespuestaNovedadAsesorCommand.crear(
                solicitud.toString(), "APROBADA", asesor);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(ModificacionEstadoRespuestaDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getSolicitud()).isEqualTo(solicitud);
        assertThat(captor.getValue().getResponsableUsuario()).isEqualTo(asesor);
        assertThat(captor.getValue().getTipoEsperado()).isEqualTo(TipoSolicitud.NOVEDAD_PARA_EL_ASESOR);
        assertThat(captor.getValue().getNuevoEstado()).isEqualTo(EstadoRespuesta.APROBADA);
    }
}
