package com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.model.PurgarPaquetesProyectoGradoCommand;
import com.arquisoft.artefactos.application.paqueteentregable.command.result.PurgaPaquetesProyectoGradoResult;
import com.arquisoft.artefactos.application.paqueteentregable.command.usecase.PurgarPaquetesProyectoGradoUseCase;
import com.arquisoft.artefactos.domain.paqueteentregable.PurgaPaquetesProyectoGradoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    void debeDelegarEnElUseCase_conElDomainMapeadoYDevolverSuResultado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var esperado = new PurgaPaquetesProyectoGradoResult(id, 2);
        when(purgarPaquetesProyectoGradoUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(new PurgarPaquetesProyectoGradoCommand(id));

        // Assert
        var captor = ArgumentCaptor.forClass(PurgaPaquetesProyectoGradoDomain.class);
        verify(purgarPaquetesProyectoGradoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(id);
        assertThat(resultado).isSameAs(esperado);
    }
}
