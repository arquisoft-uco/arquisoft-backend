package com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.primaryport.model.AgregarCoordinadorProyectoGradoCommand;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.result.AgregacionCoordinadorProyectoGradoResult;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.usecase.AgregarCoordinadorProyectoGradoUseCase;
import com.arquisoft.evaluaciones.domain.coordinadorproyectogrado.CoordinadorProyectoGradoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class AgregarCoordinadorProyectoGradoInteractorImplTest {

    @Mock
    private AgregarCoordinadorProyectoGradoUseCase useCase;

    @InjectMocks
    private AgregarCoordinadorProyectoGradoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElDominioMapeado_cuandoSeEjecuta() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var command = AgregarCoordinadorProyectoGradoCommand.crear(proyecto.toString(),
                UtilUUID.generarNuevoUUID().toString(), "Coordinador", "coord@uco.edu.co",
                Instant.parse("2026-11-19T10:00:00Z"));
        var esperado = new AgregacionCoordinadorProyectoGradoResult.Agregada(proyecto);
        when(useCase.ejecutar(any(CoordinadorProyectoGradoDomain.class))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        var dominio = ArgumentCaptor.forClass(CoordinadorProyectoGradoDomain.class);
        verify(useCase).ejecutar(dominio.capture());
        assertThat(dominio.getValue().getProyectoGrado()).isEqualTo(proyecto);
        assertThat(resultado).isSameAs(esperado);
    }
}
