package com.arquisoft.solicitudes.application.usuario.command.finder.impl;

import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import com.arquisoft.solicitudes.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.solicitudes.application.usuario.command.secondaryport.entity.UsuarioEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
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
    void debeDelegarEnElOutputPort_cuandoElUsuarioExiste() {
        // Arrange
        var id = UUID.randomUUID();
        var entity = new UsuarioEntity(id, "EST-9", "Nombre Completo", "n@uco.edu.co", Instant.now());
        when(usuarioOutputPort.buscarPorId(id)).thenReturn(Optional.of(entity));

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado.getId()).isEqualTo(id);
    }

    @Test
    void debeDevolverVacio_cuandoElUsuarioNoExiste() {
        // Arrange
        var id = UUID.randomUUID();
        when(usuarioOutputPort.buscarPorId(id)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isEqualTo(UsuarioDomain.VACIO);
    }
}
