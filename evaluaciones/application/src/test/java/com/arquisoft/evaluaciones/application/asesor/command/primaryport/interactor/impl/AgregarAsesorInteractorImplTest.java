package com.arquisoft.evaluaciones.application.asesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.asesor.command.primaryport.model.AgregarAsesorCommand;
import com.arquisoft.evaluaciones.application.asesor.command.result.AgregacionAsesorResult;
import com.arquisoft.evaluaciones.application.asesor.command.usecase.AgregarAsesorUseCase;
import com.arquisoft.evaluaciones.domain.asesor.AsesorDomain;
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
class AgregarAsesorInteractorImplTest {

    @Mock
    private AgregarAsesorUseCase agregarAsesorUseCase;

    @InjectMocks
    private AgregarAsesorInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoDelCommand() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-09-24T10:00:00Z");
        var command = AgregarAsesorCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);
        var resultadoEsperado = new AgregacionAsesorResult.Agregada(id);
        when(agregarAsesorUseCase.ejecutar(any())).thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        var captor = ArgumentCaptor.forClass(AsesorDomain.class);
        verify(agregarAsesorUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(id);
        assertThat(captor.getValue().getIdentificador()).isEqualTo("20161020123");
        assertThat(captor.getValue().getNombre()).isEqualTo("Ana Perez");
        assertThat(captor.getValue().getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(captor.getValue().getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(captor.getValue().estaEliminado()).isFalse();
    }
}
