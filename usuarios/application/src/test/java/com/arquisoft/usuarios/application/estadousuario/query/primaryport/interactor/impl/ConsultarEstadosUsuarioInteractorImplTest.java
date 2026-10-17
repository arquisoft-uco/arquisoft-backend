package com.arquisoft.usuarios.application.estadousuario.query.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.estadousuario.query.readmodel.EstadoUsuarioReadModel;
import com.arquisoft.usuarios.application.estadousuario.query.usecase.ConsultarEstadosUsuarioUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadosUsuarioInteractorImplTest {

    @Mock
    private ConsultarEstadosUsuarioUseCase consultarEstadosUsuarioUseCase;

    @InjectMocks
    private ConsultarEstadosUsuarioInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_cuandoEjecuta() {
        // Arrange
        var esperado = List.of(new EstadoUsuarioReadModel("ACTIVO", "Activo", "Indica que un usuario puede desempeñarse"));
        when(consultarEstadosUsuarioUseCase.ejecutar()).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar();

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarEstadosUsuarioUseCase).ejecutar();
    }
}
