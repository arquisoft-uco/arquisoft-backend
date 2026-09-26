package com.arquisoft.proyectos.application.coordinador.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.coordinador.command.primaryport.model.RemoverCoordinadorCommand;
import com.arquisoft.proyectos.application.coordinador.command.result.RemocionCoordinadorResult;
import com.arquisoft.proyectos.application.coordinador.command.usecase.RemoverCoordinadorUseCase;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
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
class RemoverCoordinadorInteractorImplTest {

    @Mock
    private RemoverCoordinadorUseCase removerCoordinadorUseCase;

    @InjectMocks
    private RemoverCoordinadorInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoDelCommand() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-09-24T10:00:00Z");
        var command = RemoverCoordinadorCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);
        var resultadoEsperado = new RemocionCoordinadorResult.Removida(id);
        when(removerCoordinadorUseCase.ejecutar(any())).thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(CoordinadorDomain.class);
        verify(removerCoordinadorUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(id);
        assertThat(captor.getValue().getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(resultado).isSameAs(resultadoEsperado);
    }
}
