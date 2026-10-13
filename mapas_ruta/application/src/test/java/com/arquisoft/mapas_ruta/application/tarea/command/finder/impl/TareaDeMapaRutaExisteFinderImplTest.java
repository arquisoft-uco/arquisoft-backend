package com.arquisoft.mapas_ruta.application.tarea.command.finder.impl;

import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.TareaOutputPort;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TareaDeMapaRutaExisteFinderImplTest {

    @Mock
    private TareaOutputPort tareaOutputPort;

    @InjectMocks
    private TareaDeMapaRutaExisteFinderImpl finder;

    @Test
    void debeRetornarElValorDelPuerto_cuandoConsultaExistenciaPorMapaYDescripcion() {
        // Arrange
        var tarea = TareaDomain.crear(UtilUUID.generarNuevoUUID(), "Redactar capitulo", UtilUUID.generarNuevoUUID());
        when(tareaOutputPort.existePorMapaRutaYDescripcion(tarea.getMapaRuta(), tarea.getDescripcion()))
                .thenReturn(true);

        // Act & Assert
        assertThat(finder.obtener(tarea)).isTrue();
    }
}
