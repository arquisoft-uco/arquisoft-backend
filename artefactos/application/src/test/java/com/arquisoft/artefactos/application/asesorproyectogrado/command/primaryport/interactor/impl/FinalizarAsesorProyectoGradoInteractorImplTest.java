package com.arquisoft.artefactos.application.asesorproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.asesorproyectogrado.command.primaryport.model.FinalizarAsesorProyectoGradoCommand;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.result.FinalizacionAsesorProyectoGradoResult;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.usecase.FinalizarAsesorProyectoGradoUseCase;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
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
class FinalizarAsesorProyectoGradoInteractorImplTest {

    @Mock
    private FinalizarAsesorProyectoGradoUseCase finalizarAsesorProyectoGradoUseCase;

    @InjectMocks
    private FinalizarAsesorProyectoGradoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoDelCommand() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-10-02T10:00:00Z");
        var command = new FinalizarAsesorProyectoGradoCommand(asesor, proyecto, ocurridoEn);
        var esperado = new FinalizacionAsesorProyectoGradoResult.Finalizada(asesor, proyecto);
        when(finalizarAsesorProyectoGradoUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(AsesorProyectoGradoDomain.class);
        verify(finalizarAsesorProyectoGradoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getAsesor()).isEqualTo(asesor);
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(proyecto);
        assertThat(captor.getValue().getOcurridoEn()).isEqualTo(ocurridoEn);
    }
}
