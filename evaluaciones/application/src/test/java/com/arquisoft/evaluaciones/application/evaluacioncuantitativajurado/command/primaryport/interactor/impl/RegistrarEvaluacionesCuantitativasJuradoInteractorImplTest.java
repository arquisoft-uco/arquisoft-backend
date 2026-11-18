package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.model.RegistrarEvaluacionesCuantitativasJuradoCommand;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.model.RegistrarEvaluacionesCuantitativasJuradoCommand.Par;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.usecase.RegistrarEvaluacionesCuantitativasJuradoUseCase;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.RegistroEvaluacionesCuantitativasJuradoDomain;
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
class RegistrarEvaluacionesCuantitativasJuradoInteractorImplTest {

    @Mock
    private RegistrarEvaluacionesCuantitativasJuradoUseCase useCase;

    @InjectMocks
    private RegistrarEvaluacionesCuantitativasJuradoInteractorImpl interactor;

    @Test
    void debeMapearYDelegarAlUseCase_cuandoEjecutaCommand() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        var command = new RegistrarEvaluacionesCuantitativasJuradoCommand(
                evaluacionJurado, jurado, List.of(new Par(item, 300)));

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(RegistroEvaluacionesCuantitativasJuradoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        var registro = captor.getValue();
        assertThat(registro.getEvaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(registro.getJurado()).isEqualTo(jurado);
        assertThat(registro.getEvaluaciones()).hasSize(1);
        assertThat(registro.getEvaluaciones().get(0).getItem()).isEqualTo(item);
        assertThat(registro.getEvaluaciones().get(0).getPuntaje()).isEqualTo(300);
    }
}
