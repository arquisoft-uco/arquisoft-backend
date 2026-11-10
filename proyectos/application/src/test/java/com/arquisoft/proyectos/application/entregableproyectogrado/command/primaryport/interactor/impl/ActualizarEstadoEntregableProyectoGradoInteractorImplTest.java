package com.arquisoft.proyectos.application.entregableproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.entregableproyectogrado.command.primaryport.model.ActualizarEstadoEntregableProyectoGradoCommand;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.result.ActualizacionEstadoEntregableProyectoGradoResult;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.usecase.ActualizarEstadoEntregableProyectoGradoUseCase;
import com.arquisoft.proyectos.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoentregable.EstadoEntregable;
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
class ActualizarEstadoEntregableProyectoGradoInteractorImplTest {

    @Mock
    private ActualizarEstadoEntregableProyectoGradoUseCase actualizarEstadoEntregableProyectoGradoUseCase;

    @InjectMocks
    private ActualizarEstadoEntregableProyectoGradoInteractorImpl interactor;

    @Test
    void debeMapearElCommandYRetornarElResultDelUseCase_cuandoSeEjecuta() {
        // Arrange
        var command = new ActualizarEstadoEntregableProyectoGradoCommand(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), 2, "CERRADO", Instant.parse("2026-11-01T10:00:00Z"));
        var esperado = new ActualizacionEstadoEntregableProyectoGradoResult.Actualizado(
                command.entregableProyectoGrado(), "CERRADO");
        when(actualizarEstadoEntregableProyectoGradoUseCase.ejecutar(
                any(EntregableProyectoGradoDomain.class))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(EntregableProyectoGradoDomain.class);
        verify(actualizarEstadoEntregableProyectoGradoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(command.entregableProyectoGrado());
        assertThat(captor.getValue().getEstadoEntregable()).isEqualTo(EstadoEntregable.CERRADO);
        assertThat(resultado).isSameAs(esperado);
    }
}
