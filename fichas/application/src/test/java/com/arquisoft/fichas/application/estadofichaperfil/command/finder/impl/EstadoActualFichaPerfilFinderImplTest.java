package com.arquisoft.fichas.application.estadofichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.estadoficha.command.secondaryport.entity.EstadoFichaEntity;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.entity.EstadoFichaPerfilEntity;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
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
    void debeConvertirLaEntidadADominio_cuandoLaFichaTieneEstado() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        when(estadoFichaPerfilOutputPort.obtenerEstadoActual(fichaId))
                .thenReturn(Optional.of(entidadCon(fichaId, EstadoFicha.EN_CONSTRUCCION.getId())));

        // Act & Assert
        assertThat(finder.obtener(fichaId)).contains(EstadoFicha.EN_CONSTRUCCION.getId());
    }

    @Test
    void debeDegradarAVacio_cuandoElCatalogoTraeUnEstadoDesconocido() {
        // Arrange — un id sin constante equivalente no puede reventar la consulta: la rule
        // ya trata VACIO como "estado no encontrado" (422).
        UUID fichaId = UUID.randomUUID();
        when(estadoFichaPerfilOutputPort.obtenerEstadoActual(fichaId))
                .thenReturn(Optional.of(entidadCon(fichaId, "ESTADO_INVENTADO")));

        // Act & Assert
        assertThat(finder.obtener(fichaId)).contains(EstadoFicha.VACIO.getId());
    }

    @Test
    void debeDevolverVacio_cuandoLaFichaNoTieneEstado() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        when(estadoFichaPerfilOutputPort.obtenerEstadoActual(fichaId)).thenReturn(Optional.empty());

        // Act & Assert — el finder nunca lanza; la ausencia la interpreta la rule
        assertThat(finder.obtener(fichaId)).isEmpty();
    }

    private EstadoFichaPerfilEntity entidadCon(UUID fichaId, String estadoFichaId) {
        return EstadoFichaPerfilEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaId)
                .estadoFicha(EstadoFichaEntity.builder().id(estadoFichaId).build())
                .fechaActualizacion(Instant.now())
                .build();
    }
}
