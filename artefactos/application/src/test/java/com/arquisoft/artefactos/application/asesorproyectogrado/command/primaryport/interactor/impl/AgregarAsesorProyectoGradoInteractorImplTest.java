package com.arquisoft.artefactos.application.asesorproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.asesorproyectogrado.command.primaryport.model.AgregarAsesorProyectoGradoCommand;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.result.AgregacionAsesorProyectoGradoResult;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.usecase.AgregarAsesorProyectoGradoUseCase;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AgregacionAsesorProyectoGradoDomain;
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
class AgregarAsesorProyectoGradoInteractorImplTest {

    @Mock
    private AgregarAsesorProyectoGradoUseCase agregarAsesorProyectoGradoUseCase;

    @InjectMocks
    private AgregarAsesorProyectoGradoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoDelCommand() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-10-02T10:00:00Z");
        var command = new AgregarAsesorProyectoGradoCommand(asesor, proyecto, ocurridoEn);
        var esperado = new AgregacionAsesorProyectoGradoResult.Agregada(asesor, proyecto);
        when(agregarAsesorProyectoGradoUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(AgregacionAsesorProyectoGradoDomain.class);
        verify(agregarAsesorProyectoGradoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getVinculo().getAsesor()).isEqualTo(asesor);
        assertThat(captor.getValue().getVinculo().getProyectoGrado()).isEqualTo(proyecto);
        assertThat(captor.getValue().getVinculo().getOcurridoEn()).isEqualTo(ocurridoEn);
    }
}
