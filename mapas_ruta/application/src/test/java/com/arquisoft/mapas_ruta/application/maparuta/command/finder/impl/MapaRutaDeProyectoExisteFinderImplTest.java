package com.arquisoft.mapas_ruta.application.maparuta.command.finder.impl;

import com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport.MapaRutaOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapaRutaDeProyectoExisteFinderImplTest {

    @Mock
    private MapaRutaOutputPort mapaRutaOutputPort;

    @InjectMocks
    private MapaRutaDeProyectoExisteFinderImpl finder;

    @Test
    void debeRetornarElValorDelPuerto_cuandoConsultaExistenciaPorProyecto() {
        // Arrange
        var conMapa = UtilUUID.generarNuevoUUID();
        var sinMapa = UtilUUID.generarNuevoUUID();
        when(mapaRutaOutputPort.existePorProyectoGrado(conMapa)).thenReturn(true);
        when(mapaRutaOutputPort.existePorProyectoGrado(sinMapa)).thenReturn(false);

        // Act & Assert
        assertThat(finder.obtener(conMapa)).isTrue();
        assertThat(finder.obtener(sinMapa)).isFalse();
    }
}
