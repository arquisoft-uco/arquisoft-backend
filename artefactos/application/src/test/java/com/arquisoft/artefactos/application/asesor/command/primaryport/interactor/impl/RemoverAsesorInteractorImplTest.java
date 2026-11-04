package com.arquisoft.artefactos.application.asesor.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.asesor.command.primaryport.model.RemoverAsesorCommand;
import com.arquisoft.artefactos.application.asesor.command.result.RemocionAsesorResult;
import com.arquisoft.artefactos.application.asesor.command.usecase.RemoverAsesorUseCase;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
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
class RemoverAsesorInteractorImplTest {

    @Mock
    private RemoverAsesorUseCase removerAsesorUseCase;

    @InjectMocks
    private RemoverAsesorInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoDelCommand() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-09-24T10:00:00Z");
        var command = RemoverAsesorCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);
        var resultadoEsperado = new RemocionAsesorResult.Removida(id);
        when(removerAsesorUseCase.ejecutar(any())).thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(AsesorDomain.class);
        verify(removerAsesorUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(id);
        assertThat(captor.getValue().getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(resultado).isSameAs(resultadoEsperado);
    }
}
