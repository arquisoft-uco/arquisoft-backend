package com.arquisoft.seguridad.application.autenticacion.command.finder.impl;

import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.UsuarioPorCorreoOutputPort;
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
class UsuarioIdPorCorreoFinderImplTest {

    @Mock
    private UsuarioPorCorreoOutputPort usuarioPorCorreoOutputPort;

    @InjectMocks
    private UsuarioIdPorCorreoFinderImpl finder;

    @Test
    void debeDevolverElId_cuandoElPuertoLoEncuentra() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        when(usuarioPorCorreoOutputPort.obtenerIdPorCorreo("ana@uco.edu.co")).thenReturn(Optional.of(usuario));

        // Act
        var resultado = finder.obtener("ana@uco.edu.co");

        // Assert
        assertThat(resultado).isEqualTo(usuario);
    }

    @Test
    void debeDevolverElIdPorDefecto_cuandoElPuertoDevuelveVacio() {
        // Arrange
        when(usuarioPorCorreoOutputPort.obtenerIdPorCorreo("ana@uco.edu.co")).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener("ana@uco.edu.co");

        // Assert
        assertThat(UtilUUID.esPorDefecto(resultado)).isTrue();
    }
}
