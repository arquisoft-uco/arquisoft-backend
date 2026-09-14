package com.arquisoft.proyectos.application.coordinador.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.coordinador.command.primaryport.model.AgregarCoordinadorCommand;
import com.arquisoft.proyectos.application.coordinador.command.result.AgregacionCoordinadorResult;
import com.arquisoft.proyectos.application.coordinador.command.usecase.AgregarCoordinadorProyectosUseCase;
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
    private AgregarCoordinadorProyectosUseCase agregarCoordinadorProyectosUseCase;

    @InjectMocks
    private AgregarCoordinadorInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoDelCommand() {
        // Arrange
        var id = UUID.randomUUID();
        var command = AgregarCoordinadorCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());
        var resultadoEsperado = new AgregacionCoordinadorResult.Agregada(id);
        when(agregarCoordinadorProyectosUseCase.ejecutar(any())).thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        verify(agregarCoordinadorProyectosUseCase).ejecutar(any());
        assertThat(resultado).isSameAs(resultadoEsperado);
    }
}
