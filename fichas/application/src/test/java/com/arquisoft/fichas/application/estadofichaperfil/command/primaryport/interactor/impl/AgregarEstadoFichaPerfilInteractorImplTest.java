package com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.model.AgregarEstadoFichaPerfilCommand;
import com.arquisoft.fichas.application.estadofichaperfil.command.usecase.AgregarEstadoFichaPerfilUseCase;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.AgregacionEstadoFichaPerfilDomain;
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
class AgregarEstadoFichaPerfilInteractorImplTest {

    @Mock
    private AgregarEstadoFichaPerfilUseCase useCase;

    @InjectMocks
    private AgregarEstadoFichaPerfilInteractorImpl interactor;

    @Test
    void debeMapearElCommandALaAgregacionYDelegar_cuandoSeEjecuta() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var asesorFicha = UUID.randomUUID();
        var idEstado = UUID.randomUUID();
        var command = AgregarEstadoFichaPerfilCommand.crear(fichaPerfil, "DISPONIBLE_PARA_EVALUACION", asesorFicha);
        when(useCase.ejecutar(any(AgregacionEstadoFichaPerfilDomain.class))).thenReturn(idEstado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(AgregacionEstadoFichaPerfilDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getFichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(captor.getValue().getAsesorFicha()).isEqualTo(asesorFicha);
        assertThat(captor.getValue().getEstado().getEstadoFicha()).isEqualTo(EstadoFicha.DISPONIBLE_PARA_EVALUACION);
        assertThat(resultado).isSameAs(idEstado);
    }
}
