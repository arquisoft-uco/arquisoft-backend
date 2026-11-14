package com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model.ActualizarEstadoProyectoGradoCommand;
import com.arquisoft.artefactos.application.proyectogrado.command.result.ActualizacionEstadoProyectoGradoResult;
import com.arquisoft.artefactos.application.proyectogrado.command.usecase.ActualizarEstadoProyectoGradoUseCase;
import com.arquisoft.artefactos.domain.proyectogrado.ActualizacionEstadoProyectoGradoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.model.EstadoProyecto;
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
class ActualizarEstadoProyectoGradoInteractorImplTest {

    @Mock
    private ActualizarEstadoProyectoGradoUseCase actualizarEstadoProyectoGradoUseCase;

    @InjectMocks
    private ActualizarEstadoProyectoGradoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoYDevolverSuResultado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-02T10:00:00Z");
        var esperado = new ActualizacionEstadoProyectoGradoResult.Actualizado(id, "ATRASADO");
        when(actualizarEstadoProyectoGradoUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(new ActualizarEstadoProyectoGradoCommand(id, "ATRASADO", ocurridoEn));

        // Assert
        var captor = ArgumentCaptor.forClass(ActualizacionEstadoProyectoGradoDomain.class);
        verify(actualizarEstadoProyectoGradoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(id);
        assertThat(captor.getValue().getEstadoProyecto()).isEqualTo(EstadoProyecto.ATRASADO);
        assertThat(captor.getValue().getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(resultado).isSameAs(esperado);
    }
}
