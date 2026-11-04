package com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.primaryport.model.AgregarEstudiantesProyectoGradoCommand;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.result.AgregacionEstudiantesProyectoGradoResult;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.usecase.AgregarEstudiantesProyectoGradoUseCase;
import com.arquisoft.evaluaciones.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarEstudiantesProyectoGradoInteractorImplTest {

    @Mock
    private AgregarEstudiantesProyectoGradoUseCase agregarEstudiantesProyectoGradoUseCase;

    @InjectMocks
    private AgregarEstudiantesProyectoGradoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElDomainMapeadoYDevolverElResult_cuandoEjecuta() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var command = new AgregarEstudiantesProyectoGradoCommand(
                proyectoGrado, List.of(estudiante), Instant.parse("2026-10-20T10:00:00Z"));
        var esperado = new AgregacionEstudiantesProyectoGradoResult(proyectoGrado, 1, 0, 0, 0);
        when(agregarEstudiantesProyectoGradoUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(AgregacionEstudiantesProyectoGradoDomain.class);
        verify(agregarEstudiantesProyectoGradoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(captor.getValue().getVinculos()).hasSize(1);
    }
}
