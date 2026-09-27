package com.arquisoft.usuarios.domain.representantecomite;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RepresentanteComiteDomainTest {

    @Test
    void debeCrearRepresentanteComiteVigente_cuandoUsuarioEsValido() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        var representanteComite = RepresentanteComiteDomain.crear(usuario);

        // Assert
        assertThat(representanteComite.getUsuario()).isEqualTo(usuario);
        assertThat(representanteComite.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(representanteComite.estaEliminado()).isFalse();
        assertThat(representanteComite.esVacio()).isFalse();
    }

    @Test
    void debeLanzarValidacion_cuandoUsuarioEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> RepresentanteComiteDomain.crear(null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(UsuariosFields.RepresentanteComite.USUARIO);
                        assertThat(error.codigoError())
                                .isEqualTo(UsuariosCodes.RepresentanteComite.USUARIO_REQUERIDO);
                    });
                });
    }

    @Test
    void debeReconstruirSinValidar_cuandoEliminadoEnEsNulo() {
        // Act
        var representanteComite = RepresentanteComiteDomain.reconstruir(null, null);

        // Assert
        assertThat(representanteComite.getUsuario()).isNull();
        assertThat(representanteComite.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(representanteComite.estaEliminado()).isFalse();
    }

    @Test
    void debeQuedarVigente_cuandoSeReactivaUnEliminado() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");
        var representanteComite = RepresentanteComiteDomain.reconstruir(UtilUUID.generarNuevoUUID(), eliminadoEn);
        var estabaEliminado = representanteComite.estaEliminado();

        // Act
        representanteComite.reactivar();

        // Assert
        assertThat(estabaEliminado).isTrue();
        assertThat(representanteComite.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(representanteComite.estaEliminado()).isFalse();
    }

    @Test
    void debeQuedarEliminadoEnElInstante_cuandoSeRemueveUnVigente() {
        // Arrange
        var instante = Instant.parse("2026-09-27T10:00:00Z");
        var representanteComite = RepresentanteComiteDomain.crear(UtilUUID.generarNuevoUUID());

        // Act
        representanteComite.remover(instante);

        // Assert
        assertThat(representanteComite.getEliminadoEn()).isEqualTo(instante);
        assertThat(representanteComite.estaEliminado()).isTrue();
    }

    @Test
    void debeExponerCentinelaVacioNoEliminado_cuandoSeConsultaVacio() {
        // Act
        var vacio = RepresentanteComiteDomain.VACIO;

        // Assert
        assertThat(vacio.esVacio()).isTrue();
        assertThat(vacio.estaEliminado()).isFalse();
    }
}
