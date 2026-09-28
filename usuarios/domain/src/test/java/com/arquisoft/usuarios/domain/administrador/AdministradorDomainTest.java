package com.arquisoft.usuarios.domain.administrador;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdministradorDomainTest {

    @Test
    void debeCrearAdministradorVigente_cuandoUsuarioEsValido() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        var administrador = AdministradorDomain.crear(usuario);

        // Assert
        assertThat(administrador.getUsuario()).isEqualTo(usuario);
        assertThat(administrador.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(administrador.estaEliminado()).isFalse();
        assertThat(administrador.esVacio()).isFalse();
    }

    @Test
    void debeLanzarValidacion_cuandoUsuarioEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> AdministradorDomain.crear(null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(UsuariosFields.Administrador.USUARIO);
                        assertThat(error.codigoError())
                                .isEqualTo(UsuariosCodes.Administrador.USUARIO_REQUERIDO);
                    });
                });
    }

    @Test
    void debeReconstruirSinValidar_cuandoEliminadoEnEsNulo() {
        // Act
        var administrador = AdministradorDomain.reconstruir(null, null);

        // Assert
        assertThat(administrador.getUsuario()).isNull();
        assertThat(administrador.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(administrador.estaEliminado()).isFalse();
    }

    @Test
    void debeQuedarVigente_cuandoSeReactivaUnEliminado() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");
        var administrador = AdministradorDomain.reconstruir(UtilUUID.generarNuevoUUID(), eliminadoEn);
        var estabaEliminado = administrador.estaEliminado();

        // Act
        administrador.reactivar();

        // Assert
        assertThat(estabaEliminado).isTrue();
        assertThat(administrador.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(administrador.estaEliminado()).isFalse();
    }

    @Test
    void debeQuedarEliminado_cuandoSeRemueve() {
        // Arrange
        var administrador = AdministradorDomain.crear(UtilUUID.generarNuevoUUID());
        var instante = Instant.parse("2026-09-28T10:00:00Z");

        // Act
        administrador.remover(instante);

        // Assert
        assertThat(administrador.getEliminadoEn()).isEqualTo(instante);
        assertThat(administrador.estaEliminado()).isTrue();
    }

    @Test
    void debeExponerCentinelaVacioNoEliminado_cuandoSeConsultaVacio() {
        // Act
        var vacio = AdministradorDomain.VACIO;

        // Assert
        assertThat(vacio.esVacio()).isTrue();
        assertThat(vacio.estaEliminado()).isFalse();
    }
}
