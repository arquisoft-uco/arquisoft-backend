package com.arquisoft.fichas.application.tipoitem.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.tipoitem.query.readmodel.TipoItemReadModel;
import com.arquisoft.fichas.application.tipoitem.query.usecase.ConsultarTiposItemUseCase;
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
class ConsultarTiposItemInteractorImplTest {

    @Mock
    private ConsultarTiposItemUseCase consultarTiposItemUseCase;

    @InjectMocks
    private ConsultarTiposItemInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_cuandoSeEjecuta() {
        // Arrange
        List<TipoItemReadModel> tiposEsperados = List.of(
                new TipoItemReadModel("OBJETIVO_GENERAL", "Objetivo General", "Proposito principal del proyecto."));
        when(consultarTiposItemUseCase.ejecutar()).thenReturn(tiposEsperados);

        // Act
        List<TipoItemReadModel> resultado = interactor.ejecutar();

        // Assert
        assertThat(resultado).isSameAs(tiposEsperados);
        verify(consultarTiposItemUseCase).ejecutar();
    }
}
