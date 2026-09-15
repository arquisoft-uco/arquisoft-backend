package com.arquisoft.proyectos.application.estudiante.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.estudiante.command.primaryport.model.AgregarEstudianteCommand;
import com.arquisoft.proyectos.application.estudiante.command.result.AgregacionEstudianteResult;
import com.arquisoft.proyectos.application.estudiante.command.usecase.AgregarEstudianteUseCase;
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
class AgregarEstudianteInteractorImplTest {

    @Mock
    private AgregarEstudianteUseCase agregarEstudianteUseCase;

    @InjectMocks
    private AgregarEstudianteInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoDelCommand() {
        // Arrange
        var id = UUID.randomUUID();
        var command = AgregarEstudianteCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());
        var resultadoEsperado = new AgregacionEstudianteResult.Agregada(id);
        when(agregarEstudianteUseCase.ejecutar(any())).thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        verify(agregarEstudianteUseCase).ejecutar(any());
        assertThat(resultado).isSameAs(resultadoEsperado);
    }
}
