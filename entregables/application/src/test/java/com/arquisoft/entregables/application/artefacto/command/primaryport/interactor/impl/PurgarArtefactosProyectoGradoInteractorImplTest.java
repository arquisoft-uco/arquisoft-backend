package com.arquisoft.entregables.application.artefacto.command.primaryport.interactor.impl;

import com.arquisoft.entregables.application.artefacto.command.primaryport.model.PurgarArtefactosProyectoGradoCommand;
import com.arquisoft.entregables.application.artefacto.command.result.PurgaArtefactosProyectoGradoResult;
import com.arquisoft.entregables.application.artefacto.command.usecase.PurgarArtefactosProyectoGradoUseCase;
import com.arquisoft.entregables.domain.artefacto.PurgaArtefactosProyectoGradoDomain;
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
class PurgarArtefactosProyectoGradoInteractorImplTest {

    @Mock
    private PurgarArtefactosProyectoGradoUseCase purgarArtefactosProyectoGradoUseCase;

    @InjectMocks
    private PurgarArtefactosProyectoGradoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElProyectoDelCommand_cuandoSePurgaUnProyecto() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var esperado = new PurgaArtefactosProyectoGradoResult(proyecto, 2);
        when(purgarArtefactosProyectoGradoUseCase.ejecutar(any(PurgaArtefactosProyectoGradoDomain.class)))
                .thenReturn(esperado);
        var captor = ArgumentCaptor.forClass(PurgaArtefactosProyectoGradoDomain.class);

        // Act
        var resultado = interactor.ejecutar(PurgarArtefactosProyectoGradoCommand.crear(proyecto.toString()));

        // Assert
        verify(purgarArtefactosProyectoGradoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(proyecto);
        assertThat(resultado).isSameAs(esperado);
    }
}
