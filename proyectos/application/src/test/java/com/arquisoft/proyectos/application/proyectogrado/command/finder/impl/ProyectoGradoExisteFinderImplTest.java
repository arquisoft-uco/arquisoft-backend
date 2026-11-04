package com.arquisoft.proyectos.application.proyectogrado.command.finder.impl;

import com.arquisoft.proyectos.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProyectoGradoExisteFinderImplTest {

    @Mock
    private ProyectoGradoOutputPort proyectoGradoOutputPort;

    @InjectMocks
    private ProyectoGradoExisteFinderImpl finder;

    @Test
    void debeDevolverLaRespuestaDelPuerto_cuandoSeConsultaLaExistencia() {
        // Arrange
        var existente = UtilUUID.generarNuevoUUID();
        var inexistente = UtilUUID.generarNuevoUUID();
        when(proyectoGradoOutputPort.existePorId(existente)).thenReturn(true);
        when(proyectoGradoOutputPort.existePorId(inexistente)).thenReturn(false);

        // Act
        var existe = finder.obtener(existente);
        var noExiste = finder.obtener(inexistente);

        // Assert
        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }
}
