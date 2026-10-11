package com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.model.DestituirEstudianteProyectoGradoCommand;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.usecase.DestituirEstudianteProyectoGradoUseCase;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.DestitucionEstudianteProyectoGradoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DestituirEstudianteProyectoGradoInteractorImplTest {

    @Mock
    private DestituirEstudianteProyectoGradoUseCase useCase;

    @InjectMocks
    private DestituirEstudianteProyectoGradoInteractorImpl interactor;

    @Test
    void debeMapearElCommandYDelegar_cuandoSeEjecuta() {
        // Arrange
        var command = DestituirEstudianteProyectoGradoCommand.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(DestitucionEstudianteProyectoGradoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(command.proyectoGrado());
        assertThat(captor.getValue().getEstudiante()).isEqualTo(command.estudiante());
        assertThat(captor.getValue().getCoordinador()).isEqualTo(command.coordinador());
    }
}
