package com.arquisoft.entregables.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.entregables.application.proyectogrado.command.primaryport.model.FinalizarProyectoGradoCommand;
import com.arquisoft.entregables.application.proyectogrado.command.result.FinalizacionProyectoGradoResult;
import com.arquisoft.entregables.application.proyectogrado.command.usecase.FinalizarProyectoGradoUseCase;
import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.entregables.domain.proyectogrado.FinalizacionProyectoGradoDomain;
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
class FinalizarProyectoGradoInteractorImplTest {

    @Mock
    private FinalizarProyectoGradoUseCase finalizarProyectoGradoUseCase;

    @InjectMocks
    private FinalizarProyectoGradoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoDelCommand() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var esperado = new FinalizacionProyectoGradoResult.Finalizado(proyectoGrado);
        when(finalizarProyectoGradoUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(new FinalizarProyectoGradoCommand(proyectoGrado));

        // Assert
        var captor = ArgumentCaptor.forClass(FinalizacionProyectoGradoDomain.class);
        verify(finalizarProyectoGradoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(captor.getValue().getEstadoNuevo()).isEqualTo(EstadoProyecto.FINALIZADO);
        assertThat(resultado).isSameAs(esperado);
    }
}
