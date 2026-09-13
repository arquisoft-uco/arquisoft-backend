package com.arquisoft.usuarios.application.estudiante.command.finder.impl;

import com.arquisoft.usuarios.application.estudiante.command.secondaryport.EstudianteOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudianteUsuarioExisteFinderImplTest {

    @Mock
    private EstudianteOutputPort estudianteOutputPort;

    @InjectMocks
    private EstudianteUsuarioExisteFinderImpl finder;

    @Test
    void debeDelegarEnElOutputPort_enEstudianteUsuarioExisteFinder() {
        // Arrange
        var usuario = UUID.randomUUID();
        when(estudianteOutputPort.existePorUsuario(usuario)).thenReturn(true);

        // Act
        var resultado = finder.obtener(usuario);

        // Assert
        assertThat(resultado).isTrue();
    }
}
