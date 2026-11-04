package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model.RegistrarEvaluacionesCualitativasAsesorCommand;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model.RegistrarEvaluacionesCualitativasAsesorCommand.Par;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.usecase.RegistrarEvaluacionesCualitativasAsesorUseCase;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.RegistroEvaluacionesCualitativasAsesorDomain;
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
class RegistrarEvaluacionesCualitativasAsesorInteractorImplTest {

    @Mock
    private RegistrarEvaluacionesCualitativasAsesorUseCase useCase;

    @InjectMocks
    private RegistrarEvaluacionesCualitativasAsesorInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElDomainMapeado_cuandoEjecutaCommand() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        var criterio = UtilUUID.generarNuevoUUID();
        var command = new RegistrarEvaluacionesCualitativasAsesorCommand(
                evaluacionAsesor, asesor, List.of(new Par<>(item, criterio)));
        var captor = ArgumentCaptor.forClass(RegistroEvaluacionesCualitativasAsesorDomain.class);

        // Act
        interactor.ejecutar(command);

        // Assert
        verify(useCase).ejecutar(captor.capture());
        var registro = captor.getValue();
        assertThat(registro.getEvaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(registro.getAsesor()).isEqualTo(asesor);
        assertThat(registro.getEvaluaciones()).hasSize(1);
        assertThat(registro.getEvaluaciones().get(0).getItem()).isEqualTo(item);
        assertThat(registro.getEvaluaciones().get(0).getCriterio()).isEqualTo(criterio);
    }
}
