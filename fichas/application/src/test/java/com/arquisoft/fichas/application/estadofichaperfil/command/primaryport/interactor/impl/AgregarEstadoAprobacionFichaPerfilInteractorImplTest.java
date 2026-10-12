package com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.model.AgregarEstadoAprobacionFichaPerfilCommand;
import com.arquisoft.fichas.application.estadofichaperfil.command.usecase.AgregarEstadoAprobacionFichaPerfilUseCase;
import com.arquisoft.fichas.domain.estadofichaperfil.DecisionFichaPerfilDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarEstadoAprobacionFichaPerfilInteractorImplTest {

    @Mock
    private AgregarEstadoAprobacionFichaPerfilUseCase useCase;

    @InjectMocks
    private AgregarEstadoAprobacionFichaPerfilInteractorImpl interactor;

    @Test
    void debeMapearElCommandADecisionYDelegar_cuandoSeEjecuta() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var coordinador = UUID.randomUUID();
        var idEstado = UUID.randomUUID();
        var command = AgregarEstadoAprobacionFichaPerfilCommand.crear(fichaPerfil.toString(), true, coordinador);
        when(useCase.ejecutar(any(DecisionFichaPerfilDomain.class))).thenReturn(idEstado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(DecisionFichaPerfilDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getFichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(captor.getValue().isAcepta()).isTrue();
        assertThat(captor.getValue().getCoordinador()).isEqualTo(coordinador);
        assertThat(resultado).isSameAs(idEstado);
    }
}
