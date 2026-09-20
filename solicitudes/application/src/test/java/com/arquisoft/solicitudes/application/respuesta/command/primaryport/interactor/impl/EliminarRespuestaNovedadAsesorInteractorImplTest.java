package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.EliminarRespuestaNovedadAsesorCommand;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.EliminarRespuestaNovedadAsesorUseCase;
import com.arquisoft.solicitudes.domain.respuesta.EliminacionRespuestaNovedadAsesorDomain;
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
class EliminarRespuestaNovedadAsesorInteractorImplTest {

    @Mock
    private EliminarRespuestaNovedadAsesorUseCase useCase;

    @InjectMocks
    private EliminarRespuestaNovedadAsesorInteractorImpl interactor;

    @Test
    void debeMapearElComandoAObjetoDeAccionYDelegarEnElUseCase() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var asesor = UUID.randomUUID();
        var command = EliminarRespuestaNovedadAsesorCommand.crear(solicitud.toString(), asesor);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(EliminacionRespuestaNovedadAsesorDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getSolicitud()).isEqualTo(solicitud);
        assertThat(captor.getValue().getAsesorUsuario()).isEqualTo(asesor);
    }
}
