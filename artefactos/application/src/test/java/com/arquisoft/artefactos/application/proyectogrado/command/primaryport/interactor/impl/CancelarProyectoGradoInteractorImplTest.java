package com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model.CancelarProyectoGradoCommand;
import com.arquisoft.artefactos.application.proyectogrado.command.result.CancelacionProyectoGradoResult;
import com.arquisoft.artefactos.application.proyectogrado.command.usecase.CancelarProyectoGradoUseCase;
import com.arquisoft.artefactos.domain.proyectogrado.CancelacionProyectoGradoDomain;
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
class CancelarProyectoGradoInteractorImplTest {

    @Mock
    private CancelarProyectoGradoUseCase cancelarProyectoGradoUseCase;

    @InjectMocks
    private CancelarProyectoGradoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoYDevolverSuResultado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-01T10:00:00Z");
        var esperado = new CancelacionProyectoGradoResult.Cancelado(id);
        when(cancelarProyectoGradoUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(new CancelarProyectoGradoCommand(id, ocurridoEn));

        // Assert
        var captor = ArgumentCaptor.forClass(CancelacionProyectoGradoDomain.class);
        verify(cancelarProyectoGradoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(id);
        assertThat(captor.getValue().getCanceladoEn()).isEqualTo(ocurridoEn);
        assertThat(resultado).isSameAs(esperado);
    }
}
