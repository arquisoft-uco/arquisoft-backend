package com.arquisoft.fichas.application.coordinador.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.coordinador.command.primaryport.model.AgregarCoordinadorCommand;
import com.arquisoft.fichas.application.coordinador.command.result.AgregacionCoordinadorResult;
import com.arquisoft.fichas.application.coordinador.command.usecase.AgregarCoordinadorFichasUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarCoordinadorInteractorImplTest {

    @Mock
    private AgregarCoordinadorFichasUseCase agregarCoordinadorFichasUseCase;

    @InjectMocks
    private AgregarCoordinadorInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_enAgregarCoordinadorInteractor() {
        // Arrange
        var id = UUID.randomUUID();
        var command = AgregarCoordinadorCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());
        var esperado = new AgregacionCoordinadorResult.Agregada(id);
        when(agregarCoordinadorFichasUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(agregarCoordinadorFichasUseCase).ejecutar(any());
    }
}
