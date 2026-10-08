package com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.model.AsignarEstudiantesProyectoGradoCommand;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.usecase.AsignarEstudiantesProyectoGradoUseCase;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AsignarEstudiantesProyectoGradoInteractorImplTest {

    @Mock
    private AsignarEstudiantesProyectoGradoUseCase useCase;

    @InjectMocks
    private AsignarEstudiantesProyectoGradoInteractorImpl interactor;

    @Test
    void debeMapearElCommandALaAgregacionYDelegar_cuandoSeEjecuta() {
        // Arrange
        var proyectoGrado = UUID.randomUUID();
        var estudiantes = List.of(UUID.randomUUID(), UUID.randomUUID());
        var command = new AsignarEstudiantesProyectoGradoCommand(proyectoGrado, estudiantes);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(AgregacionEstudiantesProyectoGradoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(captor.getValue().getEstudiantes()).containsExactlyElementsOf(estudiantes);
    }
}
