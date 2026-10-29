package com.arquisoft.artefactos.application.estudianteproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.primaryport.model.DestituirEstudianteProyectoGradoCommand;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.result.DestitucionEstudianteProyectoGradoResult;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.usecase.DestituirEstudianteProyectoGradoUseCase;
import com.arquisoft.artefactos.domain.estudianteproyectogrado.DestitucionEstudianteProyectoGradoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DestituirEstudianteProyectoGradoInteractorImplTest {

    @Mock
    private DestituirEstudianteProyectoGradoUseCase destituirEstudianteProyectoGradoUseCase;

    @InjectMocks
    private DestituirEstudianteProyectoGradoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoDelCommand() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var command = new DestituirEstudianteProyectoGradoCommand(
                estudiante, proyecto, Instant.parse("2026-10-02T10:00:00Z"));
        var esperado = new DestitucionEstudianteProyectoGradoResult.Destituida(estudiante, proyecto);
        when(destituirEstudianteProyectoGradoUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(DestitucionEstudianteProyectoGradoDomain.class);
        verify(destituirEstudianteProyectoGradoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getVinculo().getEstudiante()).isEqualTo(estudiante);
        assertThat(captor.getValue().getDesvinculacion().getProyectoGrado()).isEqualTo(proyecto);
    }
}
