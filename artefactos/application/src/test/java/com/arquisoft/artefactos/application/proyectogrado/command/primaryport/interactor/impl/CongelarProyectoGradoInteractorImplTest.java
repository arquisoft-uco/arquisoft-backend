package com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model.CongelarProyectoGradoCommand;
import com.arquisoft.artefactos.application.proyectogrado.command.result.CongelacionProyectoGradoResult;
import com.arquisoft.artefactos.application.proyectogrado.command.usecase.CongelarProyectoGradoUseCase;
import com.arquisoft.artefactos.domain.proyectogrado.CongelacionProyectoGradoDomain;
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
class CongelarProyectoGradoInteractorImplTest {

    @Mock
    private CongelarProyectoGradoUseCase congelarProyectoGradoUseCase;

    @InjectMocks
    private CongelarProyectoGradoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoYDevolverSuResultado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-01T10:00:00Z");
        var esperado = new CongelacionProyectoGradoResult.Congelado(id);
        when(congelarProyectoGradoUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(new CongelarProyectoGradoCommand(id, ocurridoEn));

        // Assert
        var captor = ArgumentCaptor.forClass(CongelacionProyectoGradoDomain.class);
        verify(congelarProyectoGradoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(id);
        assertThat(captor.getValue().getEntregadoEn()).isEqualTo(ocurridoEn);
        assertThat(resultado).isSameAs(esperado);
    }
}
