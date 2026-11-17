package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.model.OmitirEvaluacionesCuantitativasAsesorCommand;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.usecase.OmitirEvaluacionesCuantitativasAsesorUseCase;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.OmisionEvaluacionesCuantitativasAsesorDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OmitirEvaluacionesCuantitativasAsesorInteractorImplTest {

    @Mock
    private OmitirEvaluacionesCuantitativasAsesorUseCase useCase;

    @InjectMocks
    private OmitirEvaluacionesCuantitativasAsesorInteractorImpl interactor;

    @Test
    void debeDelegarAlUseCase_cuandoEjecutaCommand() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        var command = new OmitirEvaluacionesCuantitativasAsesorCommand(evaluacionAsesor, asesor, List.of(item));
        var captor = ArgumentCaptor.forClass(OmisionEvaluacionesCuantitativasAsesorDomain.class);

        // Act
        interactor.ejecutar(command);

        // Assert
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getEvaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(captor.getValue().getAsesor()).isEqualTo(asesor);
        assertThat(captor.getValue().getItems()).containsExactly(item);
    }
}
