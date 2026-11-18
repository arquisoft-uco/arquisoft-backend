package com.arquisoft.artefactos.application.versionartefacto.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model.PurgarVersionesProyectoGradoCommand;
import com.arquisoft.artefactos.application.versionartefacto.command.result.PurgaVersionesProyectoGradoResult;
import com.arquisoft.artefactos.application.versionartefacto.command.usecase.PurgarVersionesProyectoGradoUseCase;
import com.arquisoft.artefactos.domain.versionartefacto.PurgaVersionesProyectoGradoDomain;
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
class PurgarVersionesProyectoGradoInteractorImplTest {

    @Mock
    private PurgarVersionesProyectoGradoUseCase purgarVersionesProyectoGradoUseCase;

    @InjectMocks
    private PurgarVersionesProyectoGradoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoYDevolverSuResultado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var esperado = new PurgaVersionesProyectoGradoResult(id, 3);
        when(purgarVersionesProyectoGradoUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(new PurgarVersionesProyectoGradoCommand(id));

        // Assert
        var captor = ArgumentCaptor.forClass(PurgaVersionesProyectoGradoDomain.class);
        verify(purgarVersionesProyectoGradoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(id);
        assertThat(resultado).isSameAs(esperado);
    }
}
