package com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.model.ModificarObservacionItemAsesorCommand;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.usecase.ModificarObservacionItemAsesorUseCase;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.ModificacionObservacionItemAsesorDomain;
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
class ModificarObservacionItemAsesorInteractorImplTest {

    @Mock
    private ModificarObservacionItemAsesorUseCase useCase;

    @InjectMocks
    private ModificarObservacionItemAsesorInteractorImpl interactor;

    @Test
    void debeDelegarAlUseCase_conElDominioMapeado() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var command = new ModificarObservacionItemAsesorCommand(observacion, asesor, "Sustento");
        var captor = ArgumentCaptor.forClass(ModificacionObservacionItemAsesorDomain.class);

        // Act
        interactor.ejecutar(command);

        // Assert
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getObservacion()).isEqualTo(observacion);
        assertThat(captor.getValue().getAsesor()).isEqualTo(asesor);
        assertThat(captor.getValue().getDescripcion()).isEqualTo("Sustento");
    }
}
