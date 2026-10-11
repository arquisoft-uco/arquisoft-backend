package com.arquisoft.usuarios.application.usuario.command.validator.impl;

import com.arquisoft.shared.message.constant.UsuariosRealmRoles;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.administrador.AdministradorDomain;
import com.arquisoft.usuarios.domain.asesor.AsesorDomain;
import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.usuarios.domain.bibliotecario.BibliotecarioDomain;
import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;
import com.arquisoft.usuarios.domain.representantecomite.RepresentanteComiteDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioEliminadoException;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioNoEncontradoException;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioRolesVigentesException;
import com.arquisoft.usuarios.domain.usuario.model.RolesUsuario;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EliminarUsuarioValidatorImplTest {

    private final EliminarUsuarioValidatorImpl validator = new EliminarUsuarioValidatorImpl();

    private static UsuarioDomain usuario(UUID id, Instant eliminadoEn) {
        return UsuarioDomain.reconstruir(id, "usr001", "Nombre", "correo@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO, eliminadoEn);
    }

    private static RolesUsuario roles(UUID id, EstudianteDomain estudiante, AsesorDomain asesor,
                                      AsesorFichaDomain asesorFicha, CoordinadorDomain coordinador,
                                      RepresentanteComiteDomain representanteComite) {
        return new RolesUsuario(id, estudiante, asesor, asesorFicha, coordinador, representanteComite,
                AdministradorDomain.VACIO, BibliotecarioDomain.VACIO);
    }

    private static RolesUsuario rolesConAdministrador(UUID id, AdministradorDomain administrador) {
        return new RolesUsuario(id, EstudianteDomain.VACIO, AsesorDomain.VACIO, AsesorFichaDomain.VACIO,
                CoordinadorDomain.VACIO, RepresentanteComiteDomain.VACIO, administrador, BibliotecarioDomain.VACIO);
    }

    @Test
    void debeLanzarUsuarioNoEncontrado_cuandoElEncontradoEsVacio() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(id, UsuarioDomain.VACIO, roles(id, EstudianteDomain.VACIO,
                AsesorDomain.VACIO, AsesorFichaDomain.VACIO, CoordinadorDomain.VACIO, RepresentanteComiteDomain.VACIO)))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    void debeLanzarUsuarioNoEncontradoAntesQueRolesVigentes_cuandoNoExisteYTieneRolVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(id, UsuarioDomain.VACIO, roles(id, EstudianteDomain.crear(id),
                AsesorDomain.VACIO, AsesorFichaDomain.VACIO, CoordinadorDomain.VACIO, RepresentanteComiteDomain.VACIO)))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    void debeLanzarUsuarioEliminado_cuandoElUsuarioYaEstaEliminado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var eliminado = usuario(id, Instant.parse("2026-09-25T10:15:30Z"));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(id, eliminado, roles(id, EstudianteDomain.VACIO,
                AsesorDomain.VACIO, AsesorFichaDomain.VACIO, CoordinadorDomain.VACIO, RepresentanteComiteDomain.VACIO)))
                .isInstanceOf(UsuarioEliminadoException.class);
    }

    @Test
    void debeLanzarRolesVigentes_cuandoElUsuarioConservaUnRolVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(id, usuario(id, UtilFecha.VACIO), roles(id, EstudianteDomain.VACIO,
                AsesorDomain.VACIO, AsesorFichaDomain.VACIO, CoordinadorDomain.crear(id),
                RepresentanteComiteDomain.VACIO)))
                .isInstanceOf(UsuarioRolesVigentesException.class);
    }

    @Test
    void debeLanzarRolesVigentes_cuandoElRepresentanteComiteSigueVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(id, usuario(id, UtilFecha.VACIO), roles(id, EstudianteDomain.VACIO,
                AsesorDomain.VACIO, AsesorFichaDomain.VACIO, CoordinadorDomain.VACIO,
                RepresentanteComiteDomain.crear(id))))
                .isInstanceOf(UsuarioRolesVigentesException.class)
                .hasMessageContaining(UsuariosRealmRoles.REPRESENTANTE_COMITE);
    }

    @Test
    void noDebeLanzar_cuandoElRepresentanteComiteYaFueRemovido() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var removido = RepresentanteComiteDomain.reconstruir(id, Instant.parse("2026-09-20T12:00:00Z"));

        // Act & Assert
        assertThatCode(() -> validator.validar(id, usuario(id, UtilFecha.VACIO), roles(id, EstudianteDomain.VACIO,
                AsesorDomain.VACIO, AsesorFichaDomain.VACIO, CoordinadorDomain.VACIO, removido)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarRolesVigentes_cuandoElAdministradorSigueVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(id, usuario(id, UtilFecha.VACIO),
                rolesConAdministrador(id, AdministradorDomain.crear(id))))
                .isInstanceOf(UsuarioRolesVigentesException.class)
                .hasMessageContaining(UsuariosRealmRoles.ADMINISTRADOR);
    }

    @Test
    void noDebeLanzar_cuandoElAdministradorYaFueRemovido() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var removido = AdministradorDomain.reconstruir(id, Instant.parse("2026-09-20T12:00:00Z"));

        // Act & Assert
        assertThatCode(() -> validator.validar(id, usuario(id, UtilFecha.VACIO),
                rolesConAdministrador(id, removido)))
                .doesNotThrowAnyException();
    }

    @Test
    void noDebeLanzar_cuandoExisteNoEstaEliminadoYNoTieneRolesVigentes() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var removido = Instant.parse("2026-09-20T12:00:00Z");

        // Act & Assert
        assertThatCode(() -> validator.validar(id, usuario(id, UtilFecha.VACIO), roles(id, EstudianteDomain.VACIO,
                AsesorDomain.reconstruir(id, removido), AsesorFichaDomain.VACIO, CoordinadorDomain.VACIO, RepresentanteComiteDomain.VACIO)))
                .doesNotThrowAnyException();
    }
}
