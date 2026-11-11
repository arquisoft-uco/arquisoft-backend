package com.arquisoft.proyectos.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model.CancelarProyectoGradoCommand;
import com.arquisoft.proyectos.application.proyectogrado.command.usecase.CancelarProyectoGradoUseCase;
import com.arquisoft.proyectos.domain.proyectogrado.CancelacionProyectoGradoDomain;
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
class CancelarProyectoGradoInteractorImplTest {

    @Mock
    private CancelarProyectoGradoUseCase cancelarProyectoGradoUseCase;

    @InjectMocks
    private CancelarProyectoGradoInteractorImpl interactor;

    @Test
    void debeConvertirYDelegar_cuandoSeEjecuta() {
        // Arrange
        var command = new CancelarProyectoGradoCommand(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Falta de avance");

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(CancelacionProyectoGradoDomain.class);
        verify(cancelarProyectoGradoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(command.proyectoGrado());
        assertThat(captor.getValue().getCoordinador()).isEqualTo(command.coordinador());
        assertThat(captor.getValue().getMotivo()).isEqualTo(command.motivo());
    }
}
