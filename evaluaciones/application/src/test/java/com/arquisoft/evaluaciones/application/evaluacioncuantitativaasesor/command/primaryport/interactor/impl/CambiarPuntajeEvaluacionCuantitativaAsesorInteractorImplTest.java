package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.model.CambiarPuntajeEvaluacionCuantitativaAsesorCommand;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.usecase.CambiarPuntajeEvaluacionCuantitativaAsesorUseCase;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.CambioPuntajeEvaluacionCuantitativaAsesorDomain;
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
class CambiarPuntajeEvaluacionCuantitativaAsesorInteractorImplTest {

    @Mock
    private CambiarPuntajeEvaluacionCuantitativaAsesorUseCase useCase;

    @InjectMocks
    private CambiarPuntajeEvaluacionCuantitativaAsesorInteractorImpl interactor;

    @Test
    void debeDelegarAlUseCase_cuandoCommandValido() {
        // Arrange
        var evaluacionCuantitativaAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var command = new CambiarPuntajeEvaluacionCuantitativaAsesorCommand(evaluacionCuantitativaAsesor, 120, asesor);
        var captor = ArgumentCaptor.forClass(CambioPuntajeEvaluacionCuantitativaAsesorDomain.class);

        // Act
        interactor.ejecutar(command);

        // Assert
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getEvaluacionCuantitativaAsesor()).isEqualTo(evaluacionCuantitativaAsesor);
        assertThat(captor.getValue().getAsesor()).isEqualTo(asesor);
        assertThat(captor.getValue().getNuevoPuntaje()).isEqualTo(120);
    }
}
