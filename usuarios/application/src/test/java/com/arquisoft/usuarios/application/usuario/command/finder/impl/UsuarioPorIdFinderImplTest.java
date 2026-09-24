package com.arquisoft.usuarios.application.usuario.command.finder.impl;

import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.UsuarioEntity;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
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
class UsuarioPorIdFinderImplTest {

    @Mock
    private UsuarioOutputPort usuarioOutputPort;

    @InjectMocks
    private UsuarioPorIdFinderImpl finder;

    @Test
    void debeDevolverElDomainMapeado_cuandoElUsuarioExiste() {
        // Arrange
        var id = UUID.randomUUID();
        var entity = new UsuarioEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO.getId());
        when(usuarioOutputPort.obtenerPorId(id)).thenReturn(Optional.of(entity));

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getIdentificador()).isEqualTo("20161020123");
        assertThat(resultado.getNombre()).isEqualTo("Ana Perez");
        assertThat(resultado.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(resultado.getEstado()).isEqualTo(EstadoUsuario.ACTIVO);
    }

    @Test
    void debeDevolverVacio_cuandoElUsuarioNoExiste() {
        // Arrange
        var id = UUID.randomUUID();
        when(usuarioOutputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isEqualTo(UsuarioDomain.VACIO);
    }
}
