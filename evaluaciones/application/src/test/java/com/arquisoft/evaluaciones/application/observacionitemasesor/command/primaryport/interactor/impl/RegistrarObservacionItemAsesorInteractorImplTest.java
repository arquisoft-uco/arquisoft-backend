package com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.model.RegistrarObservacionItemAsesorCommand;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.usecase.RegistrarObservacionItemAsesorUseCase;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.RegistroObservacionItemAsesorDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarObservacionItemAsesorInteractorImplTest {

    @Mock
    private RegistrarObservacionItemAsesorUseCase useCase;

    @InjectMocks
    private RegistrarObservacionItemAsesorInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var command = RegistrarObservacionItemAsesorCommand.crear(evaluacion, asesor, "Sustento");
        var id = UtilUUID.generarNuevoUUID();
        var captor = ArgumentCaptor.forClass(RegistroObservacionItemAsesorDomain.class);
        when(useCase.ejecutar(any(RegistroObservacionItemAsesorDomain.class))).thenReturn(id);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        verify(useCase).ejecutar(captor.capture());
        assertThat(resultado).isEqualTo(id);
        assertThat(captor.getValue().getAsesor()).isEqualTo(asesor);
        assertThat(captor.getValue().getObservacion().getEvaluacionCuantitativaAsesor()).isEqualTo(evaluacion);
    }
}
