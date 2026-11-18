package com.arquisoft.proyectos.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model.CambiarEstadoProyectoGradoCommand;
import com.arquisoft.proyectos.application.proyectogrado.command.usecase.CambiarEstadoProyectoGradoUseCase;
import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.proyectos.domain.proyectogrado.CambioEstadoProyectoGradoDomain;
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
class CambiarEstadoProyectoGradoInteractorImplTest {

    @Mock
    private CambiarEstadoProyectoGradoUseCase cambiarEstadoProyectoGradoUseCase;

    @InjectMocks
    private CambiarEstadoProyectoGradoInteractorImpl interactor;

    @Test
    void debeMapearElCommandYDelegar_cuandoSeEjecuta() {
        // Arrange
        var command = new CambiarEstadoProyectoGradoCommand(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "FINALIZADO");

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(CambioEstadoProyectoGradoDomain.class);
        verify(cambiarEstadoProyectoGradoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(command.proyectoGrado());
        assertThat(captor.getValue().getAsesor()).isEqualTo(command.asesor());
        assertThat(captor.getValue().getEstadoNuevo()).isEqualTo(EstadoProyecto.FINALIZADO);
    }
}
