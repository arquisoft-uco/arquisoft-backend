package com.arquisoft.seguridad.application.mapeoroles.command.primaryport.interactor.impl;

import com.arquisoft.seguridad.application.mapeoroles.command.result.ActualizacionMapeoRolesResult;
import com.arquisoft.seguridad.application.mapeoroles.command.usecase.ActualizarMapeoRolesUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarMapeoRolesInteractorImplTest {

    @Mock
    private ActualizarMapeoRolesUseCase actualizarMapeoRolesUseCase;

    @Test
    void debeDelegarEnElCasoDeUso() {
        // Arrange
        var esperado = new ActualizacionMapeoRolesResult.Omitida();
        when(actualizarMapeoRolesUseCase.ejecutar()).thenReturn(esperado);

        // Act
        var resultado = new ActualizarMapeoRolesInteractorImpl(actualizarMapeoRolesUseCase).ejecutar();

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(actualizarMapeoRolesUseCase).ejecutar();
    }
}
