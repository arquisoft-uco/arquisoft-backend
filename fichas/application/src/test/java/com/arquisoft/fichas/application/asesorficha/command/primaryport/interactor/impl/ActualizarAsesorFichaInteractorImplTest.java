package com.arquisoft.fichas.application.asesorficha.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.asesorficha.command.primaryport.model.ActualizarAsesorFichaCommand;
import com.arquisoft.fichas.application.asesorficha.command.result.ActualizacionAsesorFichaResult;
import com.arquisoft.fichas.application.asesorficha.command.usecase.ActualizarAsesorFichaUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarAsesorFichaInteractorImplTest {

    @Mock
    private ActualizarAsesorFichaUseCase actualizarAsesorFichaUseCase;

    private ActualizarAsesorFichaInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDominioMapeadoDelCommand() {
        // Arrange
        interactor = new ActualizarAsesorFichaInteractorImpl(actualizarAsesorFichaUseCase);
        var id = UUID.randomUUID();
        var command = ActualizarAsesorFichaCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());
        var resultadoEsperado = new ActualizacionAsesorFichaResult.Actualizada(id);
        when(actualizarAsesorFichaUseCase.ejecutar(ArgumentMatchers.any())).thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        verify(actualizarAsesorFichaUseCase).ejecutar(ArgumentMatchers.argThat(
                d -> d.getId().equals(id) && d.getIdentificador().equals("20161020123")));
    }
}
