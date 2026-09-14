package com.arquisoft.proyectos.application.asesor.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.asesor.command.primaryport.model.AgregarAsesorCommand;
import com.arquisoft.proyectos.application.asesor.command.result.AgregacionAsesorResult;
import com.arquisoft.proyectos.application.asesor.command.usecase.AgregarAsesorProyectosUseCase;
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
class AgregarAsesorProyectosInteractorImplTest {

    @Mock
    private AgregarAsesorProyectosUseCase agregarAsesorProyectosUseCase;

    @InjectMocks
    private AgregarAsesorProyectosInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoDelCommand() {
        // Arrange
        var id = UUID.randomUUID();
        var command = AgregarAsesorCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());
        var resultadoEsperado = new AgregacionAsesorResult.Agregada(id);
        when(agregarAsesorProyectosUseCase.ejecutar(any())).thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        verify(agregarAsesorProyectosUseCase).ejecutar(any());
        assertThat(resultado).isSameAs(resultadoEsperado);
    }
}
