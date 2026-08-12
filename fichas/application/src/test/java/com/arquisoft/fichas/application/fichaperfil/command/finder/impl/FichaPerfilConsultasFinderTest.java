package com.arquisoft.fichas.application.fichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.FichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.ModificacionFichaPerfilDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Los tres finders de consulta de fichaperfil comparten el mismo puerto, asi que
 * se prueban juntos: cada uno solo traslada el resultado sin interpretarlo.
 */
@ExtendWith(MockitoExtension.class)
class FichaPerfilConsultasFinderTest {

    @Mock
    private FichaPerfilOutputPort fichaPerfilOutputPort;

    @InjectMocks
    private FichaPerfilExisteFinderImpl existeFinder;

    @InjectMocks
    private TituloFichaPerfilExisteFinderImpl tituloExisteFinder;

    @InjectMocks
    private TituloEnOtraFichaExisteFinderImpl tituloEnOtraFichaFinder;

    @Test
    void debeTrasladarLaExistenciaDeLaFicha_cuandoElPuertoResponde() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        when(fichaPerfilOutputPort.existePorId(fichaId)).thenReturn(true);

        // Act & Assert
        assertThat(existeFinder.obtener(fichaId)).isTrue();
    }

    @Test
    void debeTrasladarLaAusenciaDeLaFicha_cuandoElPuertoResponde() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        when(fichaPerfilOutputPort.existePorId(fichaId)).thenReturn(false);

        // Act & Assert
        assertThat(existeFinder.obtener(fichaId)).isFalse();
    }

    @Test
    void debeTrasladarSiElTituloYaEstaTomado_cuandoElPuertoResponde() {
        // Arrange
        when(fichaPerfilOutputPort.existePorTituloProyecto("Titulo tomado")).thenReturn(true);

        // Act & Assert
        assertThat(tituloExisteFinder.obtener("Titulo tomado")).isTrue();
    }

    @Test
    void debeTrasladarSiElTituloEstaLibre_cuandoElPuertoResponde() {
        // Arrange
        when(fichaPerfilOutputPort.existePorTituloProyecto("Titulo libre")).thenReturn(false);

        // Act & Assert
        assertThat(tituloExisteFinder.obtener("Titulo libre")).isFalse();
    }

    @Test
    void debeExcluirLaPropiaFicha_cuandoConsultaElTituloEnOtraFicha() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        var modificacion = ModificacionFichaPerfilDomain.crear(fichaId, "Titulo", UUID.randomUUID());
        when(fichaPerfilOutputPort.existeTituloEnOtraFicha(fichaId, "Titulo")).thenReturn(true);

        // Act & Assert
        assertThat(tituloEnOtraFichaFinder.obtener(modificacion)).isTrue();
    }
}
