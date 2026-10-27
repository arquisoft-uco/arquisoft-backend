package com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.model.AsignarEstudiantesArtefactoCommand;
import com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.AsignarEstudiantesArtefactoUseCase;
import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
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
class AsignarEstudiantesArtefactoInteractorImplTest {

    @Mock
    private AsignarEstudiantesArtefactoUseCase asignarEstudiantesArtefactoUseCase;

    @InjectMocks
    private AsignarEstudiantesArtefactoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoDelCommand() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var command = new AsignarEstudiantesArtefactoCommand(artefacto, asesor, List.of(estudiante));

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(AgregacionEstudiantesArtefactoDomain.class);
        verify(asignarEstudiantesArtefactoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getAsesor()).isEqualTo(asesor);
        assertThat(captor.getValue().getArtefactos()).containsExactly(artefacto);
        assertThat(captor.getValue().getEstudiantes()).containsExactly(estudiante);
    }
}
