package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.model.RegistrarEvaluacionesCuantitativasAsesorCommand;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.model.RegistrarEvaluacionesCuantitativasAsesorCommand.Par;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.usecase.RegistrarEvaluacionesCuantitativasAsesorUseCase;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.RegistroEvaluacionesCuantitativasAsesorDomain;
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
class RegistrarEvaluacionesCuantitativasAsesorInteractorImplTest {

    @Mock
    private RegistrarEvaluacionesCuantitativasAsesorUseCase useCase;

    @InjectMocks
    private RegistrarEvaluacionesCuantitativasAsesorInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElDomainMapeado_cuandoEjecutaCommand() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        var command = new RegistrarEvaluacionesCuantitativasAsesorCommand(
                evaluacionAsesor, asesor, List.of(new Par(item, 30)));
        var captor = ArgumentCaptor.forClass(RegistroEvaluacionesCuantitativasAsesorDomain.class);

        // Act
        interactor.ejecutar(command);

        // Assert
        verify(useCase).ejecutar(captor.capture());
        var registro = captor.getValue();
        assertThat(registro.getEvaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(registro.getAsesor()).isEqualTo(asesor);
        assertThat(registro.getEvaluaciones()).hasSize(1);
        assertThat(registro.getEvaluaciones().get(0).getItem()).isEqualTo(item);
        assertThat(registro.getEvaluaciones().get(0).getPuntaje()).isEqualTo(30);
    }
}
