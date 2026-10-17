package com.arquisoft.fichas.application.representantecomite.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.representantecomite.command.primaryport.model.AgregarRepresentanteComiteCommand;
import com.arquisoft.fichas.application.representantecomite.command.result.AgregacionRepresentanteComiteResult;
import com.arquisoft.fichas.application.representantecomite.command.usecase.AgregarRepresentanteComiteUseCase;
import com.arquisoft.fichas.domain.representantecomite.RepresentanteComiteDomain;
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
class AgregarRepresentanteComiteInteractorImplTest {

    @Mock
    private AgregarRepresentanteComiteUseCase agregarRepresentanteComiteUseCase;

    @InjectMocks
    private AgregarRepresentanteComiteInteractorImpl interactor;

    @Test
    void debeMapearElCommandYDelegarEnElUseCase_cuandoSeEjecuta() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-09-24T10:00:00Z");
        var command = AgregarRepresentanteComiteCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);
        var esperado = new AgregacionRepresentanteComiteResult.Agregada(id);
        when(agregarRepresentanteComiteUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(RepresentanteComiteDomain.class);
        verify(agregarRepresentanteComiteUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(id);
        assertThat(captor.getValue().getIdentificador()).isEqualTo("20161020123");
        assertThat(captor.getValue().getNombre()).isEqualTo("Ana Perez");
        assertThat(captor.getValue().getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(captor.getValue().getOcurridoEn()).isEqualTo(ocurridoEn);
    }
}
