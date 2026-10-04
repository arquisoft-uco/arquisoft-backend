package com.arquisoft.usuarios.domain.bibliotecario;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BibliotecarioDomainTest {

    @Test
    void debeCrearBibliotecarioVigente_cuandoUsuarioEsValido() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        var bibliotecario = BibliotecarioDomain.crear(usuario);

        // Assert
        assertThat(bibliotecario.getUsuario()).isEqualTo(usuario);
        assertThat(bibliotecario.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(bibliotecario.estaEliminado()).isFalse();
        assertThat(bibliotecario.esVacio()).isFalse();
    }

    @Test
    void debeLanzarValidacion_cuandoUsuarioEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> BibliotecarioDomain.crear(null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(UsuariosFields.Bibliotecario.USUARIO);
                        assertThat(error.codigoError())
                                .isEqualTo(UsuariosCodes.Bibliotecario.USUARIO_REQUERIDO);
                    });
                });
    }

    @Test
    void debeReconstruirSinValidarYReflejarLaBaja_cuandoEliminadoEnEsNuloONo() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");

        // Act
        var sinBaja = BibliotecarioDomain.reconstruir(null, null);
        var conBaja = BibliotecarioDomain.reconstruir(UtilUUID.generarNuevoUUID(), eliminadoEn);

        // Assert
        assertThat(sinBaja.getUsuario()).isNull();
        assertThat(sinBaja.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(sinBaja.estaEliminado()).isFalse();
        assertThat(conBaja.getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(conBaja.estaEliminado()).isTrue();
    }

    @Test
    void debeMarcarEliminado_cuandoSeRemueve() {
        // Arrange
        var bibliotecario = BibliotecarioDomain.crear(UtilUUID.generarNuevoUUID());
        var instante = Instant.parse("2026-10-03T10:00:00Z");

        // Act
        bibliotecario.remover(instante);

        // Assert
        assertThat(bibliotecario.getEliminadoEn()).isEqualTo(instante);
        assertThat(bibliotecario.estaEliminado()).isTrue();
    }

    @Test
    void debeQuedarVigente_cuandoSeReactivaUnEliminado() {
        // Arrange
        var bibliotecario = BibliotecarioDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), Instant.parse("2026-09-24T10:00:00Z"));

        // Act
        bibliotecario.reactivar();

        // Assert
        assertThat(bibliotecario.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(bibliotecario.estaEliminado()).isFalse();
    }

    @Test
    void debeExponerCentinelaVacioNoEliminado_cuandoSeConsultaVacio() {
        // Act
        var vacio = BibliotecarioDomain.VACIO;

        // Assert
        assertThat(vacio.esVacio()).isTrue();
        assertThat(vacio.estaEliminado()).isFalse();
    }
}
