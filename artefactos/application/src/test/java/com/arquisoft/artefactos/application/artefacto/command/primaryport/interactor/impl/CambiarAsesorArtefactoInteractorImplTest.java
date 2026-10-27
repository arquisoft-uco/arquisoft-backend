package com.arquisoft.artefactos.application.artefacto.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.CambiarAsesorArtefactoCommand;
import com.arquisoft.artefactos.application.artefacto.command.usecase.CambiarAsesorArtefactoUseCase;
import com.arquisoft.artefactos.domain.artefacto.CambioAsesorArtefactoDomain;
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
class CambiarAsesorArtefactoInteractorImplTest {

    @Mock
    private CambiarAsesorArtefactoUseCase cambiarAsesorArtefactoUseCase;

    @InjectMocks
    private CambiarAsesorArtefactoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoDelCommand() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var command = new CambiarAsesorArtefactoCommand(artefacto, asesor);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(CambioAsesorArtefactoDomain.class);
        verify(cambiarAsesorArtefactoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getArtefacto()).isEqualTo(artefacto);
        assertThat(captor.getValue().getAsesor()).isEqualTo(asesor);
    }
}
