package com.arquisoft.proyectos.application.proyectogrado.query.finder.impl;

import com.arquisoft.proyectos.application.proyectogrado.query.secondaryport.ProyectoGradoAccesoQueryOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProyectoGradoCoordinadorQueryFinderImplTest {

    @Mock
    private ProyectoGradoAccesoQueryOutputPort outputPort;

    @InjectMocks
    private ProyectoGradoCoordinadorQueryFinderImpl finder;

    @Test
    void debeRetornarCoordinador_cuandoProyectoExiste() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerCoordinadorPorId(proyectoGrado)).thenReturn(Optional.of(coordinador));

        // Act
        var resultado = finder.obtener(proyectoGrado);

        // Assert
        assertThat(resultado).isEqualTo(coordinador);
    }

    @Test
    void debeRetornarUuidPorDefecto_cuandoProyectoNoExiste() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerCoordinadorPorId(proyectoGrado)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(proyectoGrado);

        // Assert
        assertThat(UtilUUID.esPorDefecto(resultado)).isTrue();
    }
}
