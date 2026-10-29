package com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.model.CambiarEstadoEntregableProyectoGradoCommand;
import com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.CambiarEstadoEntregableProyectoGradoUseCase;
import com.arquisoft.entregables.domain.entregableproyectogrado.CambioEstadoEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
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
class CambiarEstadoEntregableProyectoGradoInteractorImplTest {

    @Mock
    private CambiarEstadoEntregableProyectoGradoUseCase cambiarEstadoEntregableProyectoGradoUseCase;

    @InjectMocks
    private CambiarEstadoEntregableProyectoGradoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElDomainMapeado_cuandoSeEjecuta() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();

        // Act
        interactor.ejecutar(CambiarEstadoEntregableProyectoGradoCommand.crear(entregable, "CERRADO"));

        // Assert
        var captor = ArgumentCaptor.forClass(CambioEstadoEntregableProyectoGradoDomain.class);
        verify(cambiarEstadoEntregableProyectoGradoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(captor.getValue().getEstadoEntregable()).isEqualTo(EstadoEntregable.CERRADO);
    }
}
