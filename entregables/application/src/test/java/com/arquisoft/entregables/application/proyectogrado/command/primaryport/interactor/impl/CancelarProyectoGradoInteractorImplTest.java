package com.arquisoft.entregables.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.entregables.application.proyectogrado.command.primaryport.model.CancelarProyectoGradoCommand;
import com.arquisoft.entregables.application.proyectogrado.command.result.CancelacionProyectoGradoResult;
import com.arquisoft.entregables.application.proyectogrado.command.usecase.CancelarProyectoGradoUseCase;
import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.entregables.domain.proyectogrado.CancelacionProyectoGradoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CancelarProyectoGradoInteractorImplTest {

    @Mock
    private CancelarProyectoGradoUseCase cancelarProyectoGradoUseCase;

    @InjectMocks
    private CancelarProyectoGradoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoDelCommand() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var esperado = new CancelacionProyectoGradoResult.Cancelado(proyectoGrado, 1, 2);
        when(cancelarProyectoGradoUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(new CancelarProyectoGradoCommand(proyectoGrado));

        // Assert
        var captor = ArgumentCaptor.forClass(CancelacionProyectoGradoDomain.class);
        verify(cancelarProyectoGradoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(captor.getValue().getEstadoNuevo()).isEqualTo(EstadoProyecto.CANCELADO);
        assertThat(captor.getValue().getPurgaPaquetes().getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(captor.getValue().getPurgaArtefactos().getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(resultado).isSameAs(esperado);
    }
}
