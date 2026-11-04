package com.arquisoft.artefactos.application.estudiante.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.estudiante.command.primaryport.model.RemoverEstudianteCommand;
import com.arquisoft.artefactos.application.estudiante.command.result.RemocionEstudianteResult;
import com.arquisoft.artefactos.application.estudiante.command.usecase.RemoverEstudianteUseCase;
import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
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
class RemoverEstudianteInteractorImplTest {

    @Mock
    private RemoverEstudianteUseCase removerEstudianteUseCase;

    @InjectMocks
    private RemoverEstudianteInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoDelCommand() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-09-24T10:00:00Z");
        var command = RemoverEstudianteCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);
        var resultadoEsperado = new RemocionEstudianteResult.Removida(id);
        when(removerEstudianteUseCase.ejecutar(any())).thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(EstudianteDomain.class);
        verify(removerEstudianteUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(id);
        assertThat(captor.getValue().getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(resultado).isSameAs(resultadoEsperado);
    }
}
