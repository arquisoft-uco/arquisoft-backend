package com.arquisoft.mapas_ruta.application.asignacionproyecto.query.finder.impl;

import com.arquisoft.mapas_ruta.application.asignacionproyecto.query.secondaryport.ProyectoGradoDeEstudianteQueryOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProyectoGradoDeEstudianteQueryFinderImplTest {

    @Mock
    private ProyectoGradoDeEstudianteQueryOutputPort outputPort;

    @InjectMocks
    private ProyectoGradoDeEstudianteQueryFinderImpl finder;

    @Test
    void debeRetornarElProyecto_cuandoElPuertoLoResuelve() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerProyectoGrado(estudiante)).thenReturn(Optional.of(proyectoGrado));

        // Act
        var resultado = finder.obtener(estudiante);

        // Assert
        assertThat(resultado).isEqualTo(proyectoGrado);
        verify(outputPort, times(1)).obtenerProyectoGrado(estudiante);
    }

    @Test
    void debeRetornarElUuidPorDefecto_cuandoElPuertoNoResuelveProyecto() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerProyectoGrado(estudiante)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(estudiante);

        // Assert
        assertThat(resultado).isEqualTo(UtilUUID.obtenerUUIDPorDefecto());
        assertThat(UtilUUID.esPorDefecto(resultado)).isTrue();
    }
}
