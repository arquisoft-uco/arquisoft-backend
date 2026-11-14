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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioPorCorreoQueryFinderImplTest {

    private static final String CORREO = "ana@uco.edu.co";

    @Mock
    private UsuarioAccesoQueryOutputPort usuarioAccesoQueryOutputPort;

    @InjectMocks
    private UsuarioPorCorreoQueryFinderImpl finder;

    @Test
    void debeRetornarDomainConEstadoYEliminadoEn_cuandoElCorreoExiste() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var eliminadoEn = Instant.parse("2026-09-25T10:15:30Z");
        var entity = new UsuarioAccesoEntity(usuario, "1001", "Ana Ramirez", CORREO,
                "573001112233", "INACTIVO", eliminadoEn);
        when(usuarioAccesoQueryOutputPort.obtenerPorCorreo(CORREO)).thenReturn(Optional.of(entity));

        // Act
        var resultado = finder.obtener(CORREO);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getId()).isEqualTo(usuario);
        assertThat(resultado.getEstado()).isEqualTo(EstadoUsuario.INACTIVO);
        assertThat(resultado.estaEliminado()).isTrue();
        verify(usuarioAccesoQueryOutputPort, times(1)).obtenerPorCorreo(CORREO);
    }

    @Test
    void debeRetornarVacio_cuandoElCorreoNoExiste() {
        // Arrange
        when(usuarioAccesoQueryOutputPort.obtenerPorCorreo(CORREO)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(CORREO);

        // Assert
        assertThat(resultado).isSameAs(UsuarioDomain.VACIO);
    }
}
