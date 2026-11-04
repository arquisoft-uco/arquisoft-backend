package com.arquisoft.artefactos.application.artefacto.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.CambiarVersionRepositorioArtefactoCommand;
import com.arquisoft.artefactos.application.artefacto.command.usecase.CambiarVersionRepositorioArtefactoUseCase;
import com.arquisoft.artefactos.domain.artefacto.CambioVersionRepositorioArtefactoDomain;
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
class CambiarVersionRepositorioArtefactoInteractorImplTest {

    @Mock
    private CambiarVersionRepositorioArtefactoUseCase cambiarVersionRepositorioArtefactoUseCase;

    @InjectMocks
    private CambiarVersionRepositorioArtefactoInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCase_conElDomainMapeadoDelCommand() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID();

        // Act
        interactor.ejecutar(new CambiarVersionRepositorioArtefactoCommand(artefacto, asesor, version));

        // Assert
        var captor = ArgumentCaptor.forClass(CambioVersionRepositorioArtefactoDomain.class);
        verify(cambiarVersionRepositorioArtefactoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getArtefacto()).isEqualTo(artefacto);
        assertThat(captor.getValue().getAsesor()).isEqualTo(asesor);
        assertThat(captor.getValue().getVersionRepositorioArtefacto()).isEqualTo(version);
    }
}
