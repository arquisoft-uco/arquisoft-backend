package com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.keycloak.mapper;

import com.arquisoft.shared.util.UtilTexto;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakIdentidadUsuarioMapperTest {

    @Test
    void debeUsarVacio_cuandoFaltanFirstNameOLastName() {
        // Arrange
        var usuario = new HashMap<String, Object>();
        usuario.put("lastName", null);

        // Act
        var resultado = KeycloakIdentidadUsuarioMapper.toReadModel(usuario);

        // Assert
        assertThat(resultado.nombres()).isEqualTo(UtilTexto.VACIO);
        assertThat(resultado.apellidos()).isEqualTo(UtilTexto.VACIO);
    }
}
