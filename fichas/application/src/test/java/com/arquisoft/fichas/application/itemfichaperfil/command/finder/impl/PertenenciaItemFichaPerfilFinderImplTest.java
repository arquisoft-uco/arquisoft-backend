package com.arquisoft.fichas.application.itemfichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.ItemFichaPerfilOutputPort;
import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.entity.PertenenciaItemFichaPerfilEntity;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.model.ItemDeEstudiante;
import com.arquisoft.fichas.domain.itemfichaperfil.model.PertenenciaItemFichaPerfil;
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
class PertenenciaItemFichaPerfilFinderImplTest {

    @Mock
    private ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;

    @InjectMocks
    private PertenenciaItemFichaPerfilFinderImpl finder;

    private final UUID item = UUID.randomUUID();
    private final UUID estudiante = UUID.randomUUID();
    private final UUID fichaPerfil = UUID.randomUUID();

    @Test
    void debeMapearFichaPropiedadYEstado_cuandoElItemExisteConEstado() {
        // Arrange
        var estadoId = UUID.randomUUID();
        when(itemFichaPerfilOutputPort.obtenerPertenencia(item, estudiante))
                .thenReturn(Optional.of(new PertenenciaItemFichaPerfilEntity(
                        fichaPerfil, true, estadoId, EstadoFicha.EN_CONSTRUCCION.getId(), Instant.now())));

        // Act
        var resultado = finder.obtener(new ItemDeEstudiante(item, estudiante));

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(resultado.esPropietario()).isTrue();
        assertThat(resultado.estadoActual().getId()).isEqualTo(estadoId);
        assertThat(resultado.estadoActual().getEstadoFicha()).isEqualTo(EstadoFicha.EN_CONSTRUCCION);
    }

    @Test
    void debeDevolverEstadoVacio_cuandoLaFichaNoTieneEstado() {
        // Arrange
        when(itemFichaPerfilOutputPort.obtenerPertenencia(item, estudiante))
                .thenReturn(Optional.of(new PertenenciaItemFichaPerfilEntity(fichaPerfil, false, null, null, null)));

        // Act
        var resultado = finder.obtener(new ItemDeEstudiante(item, estudiante));

        // Assert
        assertThat(resultado.esPropietario()).isFalse();
        assertThat(resultado.estadoActual()).isEqualTo(EstadoFichaPerfilDomain.VACIO);
    }

    @Test
    void debeDevolverVacio_cuandoElItemNoExiste() {
        // Arrange
        when(itemFichaPerfilOutputPort.obtenerPertenencia(item, estudiante)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(new ItemDeEstudiante(item, estudiante));

        // Assert
        assertThat(resultado).isEqualTo(PertenenciaItemFichaPerfil.VACIO);
    }
}
