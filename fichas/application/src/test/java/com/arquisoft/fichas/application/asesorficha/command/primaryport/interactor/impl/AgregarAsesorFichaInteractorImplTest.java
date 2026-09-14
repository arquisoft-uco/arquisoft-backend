package com.arquisoft.fichas.application.asesorficha.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.asesorficha.command.primaryport.model.AgregarAsesorFichaCommand;
import com.arquisoft.fichas.application.asesorficha.command.result.AgregacionAsesorFichaResult;
import com.arquisoft.fichas.application.asesorficha.command.usecase.AgregarAsesorFichaFichasUseCase;
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
class AgregarAsesorFichaInteractorImplTest {

    @Mock
    private AgregarAsesorFichaFichasUseCase agregarAsesorFichaFichasUseCase;

    @InjectMocks
    private AgregarAsesorFichaInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_enAgregarAsesorFichaInteractor() {
        // Arrange
        var id = UUID.randomUUID();
        var command = AgregarAsesorFichaCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());
        var esperado = new AgregacionAsesorFichaResult.Agregada(id);
        when(agregarAsesorFichaFichasUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(agregarAsesorFichaFichasUseCase).ejecutar(any());
    }
}
