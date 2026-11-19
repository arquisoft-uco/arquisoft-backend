package com.arquisoft.shared.catalogoparametros.application.command.primaryport.interactor.impl;

import com.arquisoft.shared.catalogoparametros.application.command.result.RefrescoParametrosCatalogoResult;
import com.arquisoft.shared.catalogoparametros.application.command.usecase.RefrescarParametrosCatalogoUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefrescarParametrosCatalogoInteractorImplTest {

    @Mock
    private RefrescarParametrosCatalogoUseCase useCase;

    @InjectMocks
    private RefrescarParametrosCatalogoInteractorImpl interactor;

    @Test
    void debeDelegar_alUseCase() {
        // Arrange
        var esperado = new RefrescoParametrosCatalogoResult.Refrescado(2);
        when(useCase.ejecutar()).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar();

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar();
    }
}
