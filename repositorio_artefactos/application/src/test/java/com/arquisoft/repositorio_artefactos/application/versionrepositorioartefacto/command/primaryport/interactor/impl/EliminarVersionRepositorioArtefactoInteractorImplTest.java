package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.model.EliminarVersionRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.EliminacionVersionRepositorioArtefactoResult;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.usecase.EliminarVersionRepositorioArtefactoUseCase;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.EliminacionVersionRepositorioArtefactoDomain;
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
class EliminarVersionRepositorioArtefactoInteractorImplTest {

    @Mock
    private EliminarVersionRepositorioArtefactoUseCase eliminarVersionRepositorioArtefactoUseCase;

    @InjectMocks
    private EliminarVersionRepositorioArtefactoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_yDevolverSuResultado() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var esperado = new EliminacionVersionRepositorioArtefactoResult.Eliminada(version);
        when(eliminarVersionRepositorioArtefactoUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(new EliminarVersionRepositorioArtefactoCommand(version));

        // Assert
        var captor = ArgumentCaptor.forClass(EliminacionVersionRepositorioArtefactoDomain.class);
        verify(eliminarVersionRepositorioArtefactoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getVersion()).isEqualTo(version);
        assertThat(resultado).isSameAs(esperado);
    }
}
