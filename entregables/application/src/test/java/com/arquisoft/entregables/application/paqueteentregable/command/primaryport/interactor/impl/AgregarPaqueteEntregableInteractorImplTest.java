package com.arquisoft.entregables.application.paqueteentregable.command.primaryport.interactor.impl;

import com.arquisoft.entregables.application.paqueteentregable.command.primaryport.model.AgregarPaqueteEntregableCommand;
import com.arquisoft.entregables.application.paqueteentregable.command.primaryport.model.DatosArchivoPaquete;
import com.arquisoft.entregables.application.paqueteentregable.command.result.AgregacionPaqueteEntregableResult;
import com.arquisoft.entregables.application.paqueteentregable.command.usecase.AgregarPaqueteEntregableUseCase;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarPaqueteEntregableInteractorImplTest {

    @Mock
    private AgregarPaqueteEntregableUseCase agregarPaqueteEntregableUseCase;

    @InjectMocks
    private AgregarPaqueteEntregableInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoDelCommand() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var command = AgregarPaqueteEntregableCommand.crear(UtilUUID.generarNuevoUUID().toString(),
                entregable.toString(), new DatosArchivoPaquete("bucket", "paquetes/x.zip", "p.zip", 10L),
                Instant.now(), null);
        var esperado = new AgregacionPaqueteEntregableResult.Agregado(entregable);
        when(agregarPaqueteEntregableUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        verify(agregarPaqueteEntregableUseCase).ejecutar(any());
        assertThat(resultado).isSameAs(esperado);
    }
}
