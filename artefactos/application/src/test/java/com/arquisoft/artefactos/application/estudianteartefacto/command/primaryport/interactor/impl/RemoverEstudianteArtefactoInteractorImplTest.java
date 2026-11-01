package com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.model.RemoverEstudianteArtefactoCommand;
import com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.RemoverEstudianteArtefactoUseCase;
import com.arquisoft.artefactos.domain.estudianteartefacto.RemocionEstudianteArtefactoDomain;
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
class RemoverEstudianteArtefactoInteractorImplTest {

    @Mock
    private RemoverEstudianteArtefactoUseCase removerEstudianteArtefactoUseCase;

    @InjectMocks
    private RemoverEstudianteArtefactoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDominioMapeadoDelCommand() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var command = new RemoverEstudianteArtefactoCommand(artefacto, asesor, estudiante);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(RemocionEstudianteArtefactoDomain.class);
        verify(removerEstudianteArtefactoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getArtefacto()).isEqualTo(artefacto);
        assertThat(captor.getValue().getAsesor()).isEqualTo(asesor);
        assertThat(captor.getValue().getEstudiante()).isEqualTo(estudiante);
    }
}
