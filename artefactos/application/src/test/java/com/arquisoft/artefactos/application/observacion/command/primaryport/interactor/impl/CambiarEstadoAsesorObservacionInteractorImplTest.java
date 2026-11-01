package com.arquisoft.artefactos.application.observacion.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.observacion.command.primaryport.model.CambiarEstadoAsesorObservacionCommand;
import com.arquisoft.artefactos.application.observacion.command.usecase.CambiarEstadoAsesorObservacionUseCase;
import com.arquisoft.artefactos.domain.observacion.CambioEstadoAsesorObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CambiarEstadoAsesorObservacionInteractorImplTest {

    @Mock
    private CambiarEstadoAsesorObservacionUseCase cambiarEstadoAsesorObservacionUseCase;

    @InjectMocks
    private CambiarEstadoAsesorObservacionInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoDelCommand() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var command = CambiarEstadoAsesorObservacionCommand.crear(observacion, "REABIERTA", asesor);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(CambioEstadoAsesorObservacionDomain.class);
        verify(cambiarEstadoAsesorObservacionUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getObservacion()).isEqualTo(observacion);
        assertThat(captor.getValue().getEstado()).isEqualTo(EstadoObservacion.REABIERTA);
        assertThat(captor.getValue().getAsesor()).isEqualTo(asesor);
    }
}
