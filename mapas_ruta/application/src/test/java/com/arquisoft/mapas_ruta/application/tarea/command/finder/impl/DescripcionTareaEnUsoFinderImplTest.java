package com.arquisoft.mapas_ruta.application.tarea.command.finder.impl;

import com.arquisoft.mapas_ruta.application.tarea.command.finder.model.DescripcionTareaEnMapaRuta;
import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.TareaOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DescripcionTareaEnUsoFinderImplTest {

    @Mock
    private TareaOutputPort tareaOutputPort;

    @InjectMocks
    private DescripcionTareaEnUsoFinderImpl finder;

    @Test
    void debeDelegarEnElPuerto_cuandoConsulta() {
        // Arrange
        var entrada = new DescripcionTareaEnMapaRuta(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Redactar capitulo");
        when(tareaOutputPort.existeOtraPorMapaRutaYDescripcion(
                entrada.mapaRuta(), entrada.descripcion(), entrada.tarea())).thenReturn(true);

        // Act
        var enUso = finder.obtener(entrada);

        // Assert
        assertThat(enUso).isTrue();
    }
}
