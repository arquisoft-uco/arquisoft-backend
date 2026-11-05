package com.arquisoft.seguridad.application.autenticacion.command.result.mapper;

import com.arquisoft.seguridad.application.autenticacion.command.result.ResolucionSesionResult;
import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.model.ResolucionSesion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResolucionSesionResultMapperTest {

    @Test
    void debeMapearResueltaConElIdDeUsuario() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        var resultado = ResolucionSesionResultMapper.toResultResuelta(
                new ResolucionSesion.Resuelta("sujeto-pdp", "Ana", "ana@uco.edu.co", "tenant-1"), usuario);

        // Assert
        assertThat(resultado).isEqualTo(
                new ResolucionSesionResult.Resuelta(usuario.toString(), "Ana", "ana@uco.edu.co", "tenant-1"));
    }

    @Test
    void debeMapearSinSesion_cuandoLaResolucionNoTieneSesion() {
        // Act
        var resultado = ResolucionSesionResultMapper.toResultSinSesion();

        // Assert
        assertThat(resultado).isInstanceOf(ResolucionSesionResult.SinSesion.class);
    }

    @Test
    void debeMapearUsuarioNoRegistrado() {
        // Act
        var resultado = ResolucionSesionResultMapper.toResultUsuarioNoRegistrado();

        // Assert
        assertThat(resultado).isInstanceOf(ResolucionSesionResult.UsuarioNoRegistrado.class);
    }
}
