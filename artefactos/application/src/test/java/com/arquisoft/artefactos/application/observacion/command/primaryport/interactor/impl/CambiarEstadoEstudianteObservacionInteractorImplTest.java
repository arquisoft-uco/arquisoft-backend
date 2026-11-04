package com.arquisoft.artefactos.application.observacion.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.observacion.command.primaryport.model.CambiarEstadoEstudianteObservacionCommand;
import com.arquisoft.artefactos.application.observacion.command.usecase.CambiarEstadoEstudianteObservacionUseCase;
import com.arquisoft.artefactos.domain.observacion.CambioEstadoEstudianteObservacionDomain;
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
class CambiarEstadoEstudianteObservacionInteractorImplTest {

    @Mock
    private CambiarEstadoEstudianteObservacionUseCase cambiarEstadoEstudianteObservacionUseCase;

    @InjectMocks
    private CambiarEstadoEstudianteObservacionInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoDelCommand() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var command = CambiarEstadoEstudianteObservacionCommand.crear(observacion, "SOLUCIONADA", estudiante);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(CambioEstadoEstudianteObservacionDomain.class);
        verify(cambiarEstadoEstudianteObservacionUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getObservacion()).isEqualTo(observacion);
        assertThat(captor.getValue().getEstado()).isEqualTo(EstadoObservacion.SOLUCIONADA);
        assertThat(captor.getValue().getEstudiante()).isEqualTo(estudiante);
    }
}
