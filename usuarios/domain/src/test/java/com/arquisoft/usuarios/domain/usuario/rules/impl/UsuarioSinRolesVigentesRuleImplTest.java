package com.arquisoft.usuarios.domain.usuario.rules.impl;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosRealmRoles;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.asesor.AsesorDomain;
import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;
import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioRolesVigentesException;
import com.arquisoft.usuarios.domain.usuario.model.RolesUsuario;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioSinRolesVigentesRuleImplTest {

    private static final Instant ELIMINADO_EN = Instant.parse("2026-09-20T12:00:00Z");

    private final UsuarioSinRolesVigentesRuleImpl rule = new UsuarioSinRolesVigentesRuleImpl();

    @Test
    void noDebeLanzar_cuandoElUsuarioNuncaTuvoRoles() {
        // Arrange
        var roles = new RolesUsuario(UtilUUID.generarNuevoUUID(), EstudianteDomain.VACIO, AsesorDomain.VACIO,
                AsesorFichaDomain.VACIO, CoordinadorDomain.VACIO);

        // Act & Assert
        assertThatCode(() -> rule.validar(roles)).doesNotThrowAnyException();
    }

    @Test
    void noDebeLanzar_cuandoTodosSusRolesEstanEliminados() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var roles = new RolesUsuario(usuario,
                EstudianteDomain.reconstruir(usuario, ELIMINADO_EN),
                AsesorDomain.reconstruir(usuario, ELIMINADO_EN),
                AsesorFichaDomain.reconstruir(usuario, ELIMINADO_EN),
                CoordinadorDomain.reconstruir(usuario, ELIMINADO_EN));

        // Act & Assert
        assertThatCode(() -> rule.validar(roles)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarConElRolVigente_cuandoSoloUnRolSigueVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var roles = new RolesUsuario(usuario,
                EstudianteDomain.VACIO,
                AsesorDomain.reconstruir(usuario, UtilFecha.VACIO),
                AsesorFichaDomain.reconstruir(usuario, ELIMINADO_EN),
                CoordinadorDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(roles))
                .isInstanceOfSatisfying(UsuarioRolesVigentesException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(UsuariosCodes.Usuario.ROLES_VIGENTES);
                    assertThat(ex.getMessage())
                            .contains(usuario.toString())
                            .contains(": " + UsuariosRealmRoles.ASESOR + ".")
                            .doesNotContain(UsuariosRealmRoles.ESTUDIANTE)
                            .doesNotContain(UsuariosRealmRoles.COORDINADOR);
                });
    }

    @Test
    void debeListarTodosLosRolesVigentes_cuandoVariosSiguenVigentes() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var roles = new RolesUsuario(usuario,
                EstudianteDomain.reconstruir(usuario, UtilFecha.VACIO),
                AsesorDomain.reconstruir(usuario, UtilFecha.VACIO),
                AsesorFichaDomain.reconstruir(usuario, UtilFecha.VACIO),
                CoordinadorDomain.reconstruir(usuario, UtilFecha.VACIO));

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(roles))
                .isInstanceOf(UsuarioRolesVigentesException.class)
                .hasMessageContaining(String.join(", ", UsuariosRealmRoles.ESTUDIANTE, UsuariosRealmRoles.ASESOR,
                        UsuariosRealmRoles.ASESOR_FICHA, UsuariosRealmRoles.COORDINADOR));
    }
}
