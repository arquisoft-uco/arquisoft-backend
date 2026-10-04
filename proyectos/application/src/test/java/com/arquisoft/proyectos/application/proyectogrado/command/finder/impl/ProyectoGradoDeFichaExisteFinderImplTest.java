package com.arquisoft.proyectos.application.proyectogrado.command.finder.impl;

import com.arquisoft.proyectos.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProyectoGradoDeFichaExisteFinderImplTest {

    @Mock
    private ProyectoGradoOutputPort proyectoGradoOutputPort;

    @InjectMocks
    private ProyectoGradoDeFichaExisteFinderImpl finder;

    @Test
    void debeResponderLoQueDiceElPuerto_cuandoSeConsultaPorFicha() {
        // Arrange
        var conProyecto = UUID.randomUUID();
        var sinProyecto = UUID.randomUUID();
        when(proyectoGradoOutputPort.existePorFichaPerfil(conProyecto)).thenReturn(true);
        when(proyectoGradoOutputPort.existePorFichaPerfil(sinProyecto)).thenReturn(false);

        // Act
        var existe = finder.obtener(conProyecto);
        var noExiste = finder.obtener(sinProyecto);

        // Assert
        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }
}
