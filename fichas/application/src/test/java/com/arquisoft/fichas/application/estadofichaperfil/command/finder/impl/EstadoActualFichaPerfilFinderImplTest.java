package com.arquisoft.fichas.application.estadofichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.entity.EstadoFichaPerfilEntity;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadoficha.exception.EstadoFichaNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstadoActualFichaPerfilFinderImplTest {

    @Mock
    private EstadoFichaPerfilOutputPort estadoFichaPerfilOutputPort;

    @InjectMocks
    private EstadoActualFichaPerfilFinderImpl finder;

    @Test
    void debeConvertirLaEntidadADominio_cuandoLaFichaTieneEstado() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        when(estadoFichaPerfilOutputPort.obtenerEstadoActual(fichaId))
                .thenReturn(Optional.of(entidadCon(fichaId, EstadoFicha.EN_CONSTRUCCION.getId())));

        // Act & Assert
        assertThat(finder.obtener(fichaId))
                .map(EstadoFichaPerfilDomain::getEstadoFicha)
                .contains(EstadoFicha.EN_CONSTRUCCION);
    }

    @Test
    void debeLanzarExcepcion_cuandoElCatalogoTraeUnEstadoDesconocido() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        when(estadoFichaPerfilOutputPort.obtenerEstadoActual(fichaId))
                .thenReturn(Optional.of(entidadCon(fichaId, "ESTADO_INVENTADO")));

        // Act & Assert
        assertThatThrownBy(() -> finder.obtener(fichaId))
                .isInstanceOf(EstadoFichaNoEncontradoException.class);
    }

    @Test
    void debeDevolverVacio_cuandoLaFichaNoTieneEstado() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        when(estadoFichaPerfilOutputPort.obtenerEstadoActual(fichaId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThat(finder.obtener(fichaId)).isEmpty();
    }

    private EstadoFichaPerfilEntity entidadCon(UUID fichaId, String estadoFichaId) {
        return new EstadoFichaPerfilEntity(UUID.randomUUID(), fichaId, estadoFichaId, Instant.now());
    }
}
