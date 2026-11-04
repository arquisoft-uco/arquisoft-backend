package com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.interactor.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.model.ActualizarAdministradorCommand;
import com.arquisoft.repositorio_artefactos.application.administrador.command.result.ActualizacionAdministradorResult;
import com.arquisoft.repositorio_artefactos.application.administrador.command.usecase.ActualizarAdministradorUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarAdministradorInteractorImplTest {

    @Mock
    private ActualizarAdministradorUseCase actualizarAdministradorUseCase;

    private ActualizarAdministradorInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDominioMapeadoDelCommand() {
        // Arrange
        interactor = new ActualizarAdministradorInteractorImpl(actualizarAdministradorUseCase);
        var id = UtilUUID.generarNuevoUUID();
        var command = ActualizarAdministradorCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());
        var resultadoEsperado = new ActualizacionAdministradorResult.Actualizada(id);
        when(actualizarAdministradorUseCase.ejecutar(ArgumentMatchers.any())).thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        verify(actualizarAdministradorUseCase).ejecutar(ArgumentMatchers.argThat(
                d -> d.getId().equals(id) && d.getIdentificador().equals("20161020123")));
    }
}
