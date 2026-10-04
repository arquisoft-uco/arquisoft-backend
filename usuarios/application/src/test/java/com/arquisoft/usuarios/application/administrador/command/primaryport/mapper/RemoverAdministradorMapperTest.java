package com.arquisoft.usuarios.application.administrador.command.primaryport.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.administrador.command.primaryport.model.RemoverAdministradorCommand;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RemoverAdministradorMapperTest {

    @Test
    void debeMapearUsuarioYActor_cuandoSeConvierteElComando() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var actor = UtilUUID.generarNuevoUUID();
        var comando = RemoverAdministradorCommand.crear(usuario, actor);

        // Act
        var dominio = RemoverAdministradorMapper.toDomain(comando);

        // Assert
        assertThat(dominio.getUsuario()).isEqualTo(usuario);
        assertThat(dominio.getActor()).isEqualTo(actor);
    }
}
