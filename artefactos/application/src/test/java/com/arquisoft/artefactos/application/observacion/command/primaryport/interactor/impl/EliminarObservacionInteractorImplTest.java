package com.arquisoft.artefactos.application.observacion.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.observacion.command.primaryport.model.EliminarObservacionCommand;
import com.arquisoft.artefactos.application.observacion.command.usecase.EliminarObservacionUseCase;
import com.arquisoft.artefactos.domain.observacion.EliminacionObservacionDomain;
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
class EliminarObservacionInteractorImplTest {

    @Mock
    private EliminarObservacionUseCase useCase;

    @InjectMocks
    private EliminarObservacionInteractorImpl interactor;

    @Test
    void debeMapearYDelegar_cuandoSeEjecuta() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var command = EliminarObservacionCommand.crear(observacion, asesor);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(EliminacionObservacionDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getObservacion()).isEqualTo(observacion);
        assertThat(captor.getValue().getAsesor()).isEqualTo(asesor);
    }
}
