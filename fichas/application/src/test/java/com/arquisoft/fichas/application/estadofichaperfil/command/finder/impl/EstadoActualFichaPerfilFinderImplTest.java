package com.arquisoft.fichas.application.estadofichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstadoActualFichaPerfilFinderImplTest {

    @Mock
    private EstadoFichaPerfilOutputPort estadoFichaPerfilOutputPort;

    @InjectMocks
    private EstadoActualFichaPerfilFinderImpl finder;

    @Test
    void debeDevolverElEstadoActual_cuandoLaFichaLoTiene() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        when(estadoFichaPerfilOutputPort.obtenerEstadoActual(fichaId))
                .thenReturn(Optional.of(EstadoFicha.EN_CONSTRUCCION));

        // Act & Assert
        assertThat(finder.obtener(fichaId)).contains(EstadoFicha.EN_CONSTRUCCION);
    }

    @Test
    void debeDevolverVacio_cuandoLaFichaNoTieneEstado() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        when(estadoFichaPerfilOutputPort.obtenerEstadoActual(fichaId)).thenReturn(Optional.empty());

        // Act & Assert — el finder nunca lanza; la ausencia la interpreta la rule
        assertThat(finder.obtener(fichaId)).isEmpty();
    }
}
