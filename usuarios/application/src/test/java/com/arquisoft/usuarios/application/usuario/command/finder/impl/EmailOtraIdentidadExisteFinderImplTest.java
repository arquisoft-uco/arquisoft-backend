package com.arquisoft.usuarios.application.usuario.command.finder.impl;

import com.arquisoft.usuarios.application.usuario.command.finder.model.UnicidadOtroUsuario;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailOtraIdentidadExisteFinderImplTest {

    @Mock
    private ProveedorIdentidadOutputPort proveedorIdentidadOutputPort;

    @InjectMocks
    private EmailOtraIdentidadExisteFinderImpl finder;

    @Test
    void debeDelegarEnElPuerto_cuandoObtenerEsInvocado() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var unicidad = new UnicidadOtroUsuario(usuarioId, "correo@uco.edu.co");
        when(proveedorIdentidadOutputPort.existeEmailEnOtraIdentidad("correo@uco.edu.co", usuarioId))
                .thenReturn(true);

        // Act
        var existe = finder.obtener(unicidad);

        // Assert
        assertThat(existe).isTrue();
        verify(proveedorIdentidadOutputPort).existeEmailEnOtraIdentidad("correo@uco.edu.co", usuarioId);
    }
}
