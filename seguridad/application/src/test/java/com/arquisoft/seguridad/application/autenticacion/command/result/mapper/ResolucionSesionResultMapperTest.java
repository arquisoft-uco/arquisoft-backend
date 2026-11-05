package com.arquisoft.seguridad.application.autenticacion.command.result.mapper;

import com.arquisoft.seguridad.application.autenticacion.command.result.ResolucionSesionResult;
import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.model.ResolucionSesion;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResolucionSesionResultMapperTest {

    @Test
    void debeMapearResuelta_cuandoLaResolucionTraeIdentidad() {
        // Act
        var resultado = ResolucionSesionResultMapper.toResult(
                new ResolucionSesion.Resuelta("sujeto-1", "Ana", "ana@uco.edu.co", "tenant-1"));

        // Assert
        assertThat(resultado).isEqualTo(
                new ResolucionSesionResult.Resuelta("sujeto-1", "Ana", "ana@uco.edu.co", "tenant-1"));
    }

    @Test
    void debeMapearSinSesion_cuandoLaResolucionNoTieneSesion() {
        // Act
        var resultado = ResolucionSesionResultMapper.toResult(new ResolucionSesion.SinSesion());

        // Assert
        assertThat(resultado).isInstanceOf(ResolucionSesionResult.SinSesion.class);
    }
}
