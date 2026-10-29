package com.arquisoft.entregables.application.estudianteproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.entregables.application.estudianteproyectogrado.command.primaryport.model.RemoverEstudianteProyectoGradoCommand;
import com.arquisoft.entregables.application.estudianteproyectogrado.command.result.RemocionEstudianteProyectoGradoResult;
import com.arquisoft.entregables.application.estudianteproyectogrado.command.usecase.RemoverEstudianteProyectoGradoUseCase;
import com.arquisoft.entregables.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
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
class RemoverEstudianteProyectoGradoInteractorImplTest {

    @Mock
    private RemoverEstudianteProyectoGradoUseCase removerEstudianteProyectoGradoUseCase;

    @InjectMocks
    private RemoverEstudianteProyectoGradoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElDomainMapeadoYDevolverElResult_cuandoEjecuta() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-10-21T10:00:00Z");
        var command = new RemoverEstudianteProyectoGradoCommand(proyectoGrado, estudiante, ocurridoEn);
        var esperado = new RemocionEstudianteProyectoGradoResult.Removida(proyectoGrado, estudiante);
        when(removerEstudianteProyectoGradoUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(EstudianteProyectoGradoDomain.class);
        verify(removerEstudianteProyectoGradoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(captor.getValue().getEstudiante()).isEqualTo(estudiante);
        assertThat(captor.getValue().getOcurridoEn()).isEqualTo(ocurridoEn);
    }
}
