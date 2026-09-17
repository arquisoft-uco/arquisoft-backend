package com.arquisoft.proyectos.application.estudiante.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.estudiante.command.primaryport.model.RemoverEstudianteCommand;
import com.arquisoft.proyectos.application.estudiante.command.result.RemocionEstudianteResult;
import com.arquisoft.proyectos.application.estudiante.command.usecase.RemoverEstudianteUseCase;
import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

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
    void debeDelegarEnElUseCase_enRemoverEstudianteInteractor() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();
        var command = RemoverEstudianteCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);
        var esperado = new RemocionEstudianteResult.Removida(id);
        when(removerEstudianteUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(EstudianteDomain.class);
        verify(removerEstudianteUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(id);
        assertThat(captor.getValue().getOcurridoEn()).isEqualTo(ocurridoEn);
    }
}
