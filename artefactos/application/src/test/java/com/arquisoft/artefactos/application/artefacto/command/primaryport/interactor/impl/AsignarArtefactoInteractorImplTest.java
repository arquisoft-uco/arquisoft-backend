package com.arquisoft.artefactos.application.artefacto.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.AsignarArtefactoCommand;
import com.arquisoft.artefactos.application.artefacto.command.usecase.AsignarArtefactoUseCase;
import com.arquisoft.artefactos.domain.artefacto.AsignacionArtefactoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignarArtefactoInteractorImplTest {

    @Mock
    private AsignarArtefactoUseCase asignarArtefactoUseCase;

    @InjectMocks
    private AsignarArtefactoInteractorImpl interactor;

    @Test
    void debeDelegarYDevolverElId() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID();
        var command = new AsignarArtefactoCommand(proyecto, asesor, version, List.of(UtilUUID.generarNuevoUUID()));
        var esperado = UtilUUID.generarNuevoUUID();
        when(asignarArtefactoUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isEqualTo(esperado);
        var captor = ArgumentCaptor.forClass(AsignacionArtefactoDomain.class);
        verify(asignarArtefactoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getArtefacto().getProyectoGrado()).isEqualTo(proyecto);
        assertThat(captor.getValue().getArtefacto().getVersionRepositorioArtefacto()).isEqualTo(version);
    }
}
