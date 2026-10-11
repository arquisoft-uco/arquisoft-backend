package com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.model.CambiarEstadoAsesorProyectoGradoCommand;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.usecase.CambiarEstadoAsesorProyectoGradoUseCase;
import com.arquisoft.proyectos.domain.asesorproyectogrado.CambioEstadoAsesorProyectoGradoDomain;
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
class CambiarEstadoAsesorProyectoGradoInteractorImplTest {

    @Mock
    private CambiarEstadoAsesorProyectoGradoUseCase useCase;

    @InjectMocks
    private CambiarEstadoAsesorProyectoGradoInteractorImpl interactor;

    @Test
    void debeMapearElCommandYDelegar_cuandoSeEjecuta() {
        // Arrange
        var command = new CambiarEstadoAsesorProyectoGradoCommand(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "FINALIZADO");

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(CambioEstadoAsesorProyectoGradoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(command.proyectoGrado());
        assertThat(captor.getValue().getAsesorProyectoGrado()).isEqualTo(command.asesorProyectoGrado());
        assertThat(captor.getValue().getCoordinador()).isEqualTo(command.coordinador());
    }
}
