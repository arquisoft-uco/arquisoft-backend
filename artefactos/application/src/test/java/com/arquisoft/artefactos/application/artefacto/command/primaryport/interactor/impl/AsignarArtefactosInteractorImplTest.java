package com.arquisoft.artefactos.application.artefacto.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.AsignarArtefactosCommand;
import com.arquisoft.artefactos.application.artefacto.command.result.ArtefactosAsignadosResult;
import com.arquisoft.artefactos.application.artefacto.command.usecase.AsignarArtefactosUseCase;
import com.arquisoft.artefactos.domain.artefacto.AsignacionArtefactosDomain;
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
class AsignarArtefactosInteractorImplTest {

    @Mock
    private AsignarArtefactosUseCase asignarArtefactosUseCase;

    @InjectMocks
    private AsignarArtefactosInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoDelCommand() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID();
        var command = new AsignarArtefactosCommand(proyecto, asesor, List.of(version), List.of(UtilUUID.generarNuevoUUID()));
        var esperado = new ArtefactosAsignadosResult(List.of(UtilUUID.generarNuevoUUID()));
        when(asignarArtefactosUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(AsignacionArtefactosDomain.class);
        verify(asignarArtefactosUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(proyecto);
        assertThat(captor.getValue().getAsesor()).isEqualTo(asesor);
        assertThat(captor.getValue().getVersionesRepositorioArtefacto()).containsExactly(version);
    }
}
