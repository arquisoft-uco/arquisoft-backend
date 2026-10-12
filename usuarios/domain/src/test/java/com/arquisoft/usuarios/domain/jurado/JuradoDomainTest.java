package com.arquisoft.usuarios.domain.jurado;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JuradoDomainTest {

    @Test
    void debeCrearJuradoVigente_cuandoUsuarioEsValido() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        var jurado = JuradoDomain.crear(usuario);

        // Assert
        assertThat(jurado.getUsuario()).isEqualTo(usuario);
        assertThat(jurado.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(jurado.estaEliminado()).isFalse();
        assertThat(jurado.esVacio()).isFalse();
    }

    @Test
    void debeLanzarValidacion_cuandoUsuarioEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> JuradoDomain.crear(null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(UsuariosFields.Jurado.USUARIO);
                        assertThat(error.codigoError())
                                .isEqualTo(UsuariosCodes.Jurado.USUARIO_REQUERIDO);
                    });
                });
    }

    @Test
    void debeReconstruirSinValidarYReflejarLaBaja_cuandoEliminadoEnEsNuloONo() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");

        // Act
        var sinBaja = JuradoDomain.reconstruir(null, null);
        var conBaja = JuradoDomain.reconstruir(UtilUUID.generarNuevoUUID(), eliminadoEn);

        // Assert
        assertThat(sinBaja.getUsuario()).isNull();
        assertThat(sinBaja.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(sinBaja.estaEliminado()).isFalse();
        assertThat(conBaja.getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(conBaja.estaEliminado()).isTrue();
    }

    @Test
    void debeQuedarVigente_cuandoSeReactivaUnEliminado() {
        // Arrange
        var jurado = JuradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), Instant.parse("2026-09-24T10:00:00Z"));

        // Act
        jurado.reactivar();

        // Assert
        assertThat(jurado.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(jurado.estaEliminado()).isFalse();
    }

    @Test
    void debeExponerCentinelaVacioNoEliminado_cuandoSeConsultaVacio() {
        // Act
        var vacio = JuradoDomain.VACIO;

        // Assert
        assertThat(vacio.esVacio()).isTrue();
        assertThat(vacio.estaEliminado()).isFalse();
    }
}
