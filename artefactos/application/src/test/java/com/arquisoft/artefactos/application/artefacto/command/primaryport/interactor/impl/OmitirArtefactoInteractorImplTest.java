package com.arquisoft.artefactos.application.artefacto.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.OmitirArtefactoCommand;
import com.arquisoft.artefactos.application.artefacto.command.usecase.OmitirArtefactoUseCase;
import com.arquisoft.artefactos.domain.artefacto.OmisionArtefactoDomain;
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
class OmitirArtefactoInteractorImplTest {

    @Mock
    private OmitirArtefactoUseCase omitirArtefactoUseCase;

    @InjectMocks
    private OmitirArtefactoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoDelCommand() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var command = new OmitirArtefactoCommand(artefacto, asesor);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(OmisionArtefactoDomain.class);
        verify(omitirArtefactoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getArtefacto()).isEqualTo(artefacto);
        assertThat(captor.getValue().getAsesor()).isEqualTo(asesor);
    }
}
