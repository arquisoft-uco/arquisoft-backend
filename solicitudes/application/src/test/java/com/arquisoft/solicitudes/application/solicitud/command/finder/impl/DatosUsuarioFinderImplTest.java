package com.arquisoft.solicitudes.application.solicitud.command.finder.impl;

import com.arquisoft.solicitudes.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.solicitudes.application.usuario.command.secondaryport.entity.UsuarioEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatosUsuarioFinderImplTest {

    @Mock
    private UsuarioOutputPort usuarioOutputPort;

    @InjectMocks
    private DatosUsuarioFinderImpl finder;

    @Test
    void debeMapearLaReplicaADominio_cuandoElPuertoLaTiene() {
        // Arrange
        UUID usuario = UUID.randomUUID();
        when(usuarioOutputPort.buscarPorId(usuario))
                .thenReturn(Optional.of(new UsuarioEntity(usuario, "EST-1", "Ana", "ana@uco.edu.co")));

        // Act & Assert
        assertThat(finder.obtener(usuario))
                .hasValueSatisfying(dominio -> {
                    assertThat(dominio.getId()).isEqualTo(usuario);
                    assertThat(dominio.getNombre()).isEqualTo("Ana");
                    assertThat(dominio.getEmail()).isEqualTo("ana@uco.edu.co");
                });
    }

    @Test
    void debeRetornarVacio_cuandoElPuertoNoTieneLaReplica() {
        // Arrange
        UUID usuario = UUID.randomUUID();
        when(usuarioOutputPort.buscarPorId(usuario)).thenReturn(Optional.empty());

        // Act & Assert
        assertThat(finder.obtener(usuario)).isEmpty();
    }
}
