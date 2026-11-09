package com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.model.PurgarPaquetesAnterioresCommand;
import com.arquisoft.artefactos.application.paqueteentregable.command.result.PurgaPaquetesAnterioresResult;
import com.arquisoft.artefactos.application.paqueteentregable.command.usecase.PurgarPaquetesAnterioresUseCase;
import com.arquisoft.artefactos.domain.paqueteentregable.PurgaPaquetesAnterioresDomain;
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
class PurgarPaquetesAnterioresInteractorImplTest {

    @Mock
    private PurgarPaquetesAnterioresUseCase purgarPaquetesAnterioresUseCase;

    @InjectMocks
    private PurgarPaquetesAnterioresInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoYDevolverSuResultado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var esperado = new PurgaPaquetesAnterioresResult(id, 1);
        when(purgarPaquetesAnterioresUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(new PurgarPaquetesAnterioresCommand(id, 3));

        // Assert
        var captor = ArgumentCaptor.forClass(PurgaPaquetesAnterioresDomain.class);
        verify(purgarPaquetesAnterioresUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(id);
        assertThat(captor.getValue().getVersionEntregable()).isEqualTo(3);
        assertThat(resultado).isSameAs(esperado);
    }
}
