package com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.model.GenerarPaqueteEntregableCommand;
import com.arquisoft.artefactos.application.paqueteentregable.command.result.GeneracionPaqueteEntregableResult;
import com.arquisoft.artefactos.application.paqueteentregable.command.usecase.GenerarPaqueteEntregableUseCase;
import com.arquisoft.artefactos.domain.paqueteentregable.GeneracionPaqueteEntregableDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerarPaqueteEntregableInteractorImplTest {

    @Mock
    private GenerarPaqueteEntregableUseCase generarPaqueteEntregableUseCase;

    @InjectMocks
    private GenerarPaqueteEntregableInteractorImpl interactor;

    @Test
    void debeDelegarEnElMapperYElUseCase() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var command = GenerarPaqueteEntregableCommand.crear(entregable.toString(), proyecto.toString(), 3);
        var esperado = new GeneracionPaqueteEntregableResult.Duplicado(entregable);
        var captor = ArgumentCaptor.forClass(GeneracionPaqueteEntregableDomain.class);
        when(generarPaqueteEntregableUseCase.ejecutar(captor.capture())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        verify(generarPaqueteEntregableUseCase).ejecutar(captor.getValue());
        assertThat(captor.getValue().getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(proyecto);
        assertThat(captor.getValue().getVersionEntregable()).isEqualTo(3);
        assertThat(resultado).isSameAs(esperado);
    }
}
