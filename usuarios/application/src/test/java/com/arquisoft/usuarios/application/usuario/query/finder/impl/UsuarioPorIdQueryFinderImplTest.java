package com.arquisoft.usuarios.application.usuario.query.finder.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.usuario.query.secondaryport.UsuarioAccesoQueryOutputPort;
import com.arquisoft.usuarios.application.usuario.query.secondaryport.entity.UsuarioAccesoEntity;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioPorIdQueryFinderImplTest {

    @Mock
    private UsuarioAccesoQueryOutputPort usuarioAccesoQueryOutputPort;

    @InjectMocks
    private UsuarioPorIdQueryFinderImpl finder;

    @Test
    void debeRetornarDomainConEstadoYEliminadoEn_cuandoElUsuarioExiste() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var eliminadoEn = Instant.parse("2026-09-25T10:15:30Z");
        var entity = new UsuarioAccesoEntity(usuario, "1001", "Ana Ramirez", "ana@uco.edu.co",
                "573001112233", "INACTIVO", eliminadoEn);
        when(usuarioAccesoQueryOutputPort.obtenerPorId(usuario)).thenReturn(Optional.of(entity));

        // Act
        var resultado = finder.obtener(usuario);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getId()).isEqualTo(usuario);
        assertThat(resultado.getEstado()).isEqualTo(EstadoUsuario.INACTIVO);
        assertThat(resultado.getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(resultado.estaEliminado()).isTrue();
    }

    @Test
    void debeRetornarVacio_cuandoElUsuarioNoExiste() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        when(usuarioAccesoQueryOutputPort.obtenerPorId(usuario)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(usuario);

        // Assert
        assertThat(resultado).isSameAs(UsuarioDomain.VACIO);
    }
}
