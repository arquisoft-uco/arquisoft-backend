package com.arquisoft.entregables.application.paqueteentregable.command.primaryport.interactor.impl;

import com.arquisoft.entregables.application.paqueteentregable.command.primaryport.model.PurgarPaquetesProyectoGradoCommand;
import com.arquisoft.entregables.application.paqueteentregable.command.result.PurgaPaquetesProyectoGradoResult;
import com.arquisoft.entregables.application.paqueteentregable.command.usecase.PurgarPaquetesProyectoGradoUseCase;
import com.arquisoft.entregables.domain.paqueteentregable.PurgaPaquetesProyectoGradoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurgarPaquetesProyectoGradoInteractorImplTest {

    @Mock
    private PurgarPaquetesProyectoGradoUseCase purgarPaquetesProyectoGradoUseCase;

    @InjectMocks
    private PurgarPaquetesProyectoGradoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseYDevolverSuResultado_cuandoSePurgaUnProyecto() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var esperado = new PurgaPaquetesProyectoGradoResult(proyecto, 1);
        when(purgarPaquetesProyectoGradoUseCase.ejecutar(any(PurgaPaquetesProyectoGradoDomain.class)))
                .thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(PurgarPaquetesProyectoGradoCommand.crear(proyecto.toString()));

        // Assert
        verify(purgarPaquetesProyectoGradoUseCase).ejecutar(any(PurgaPaquetesProyectoGradoDomain.class));
        assertThat(resultado).isSameAs(esperado);
    }
}
