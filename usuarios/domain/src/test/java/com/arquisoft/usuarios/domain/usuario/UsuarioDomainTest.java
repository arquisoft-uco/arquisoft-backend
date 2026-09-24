package com.arquisoft.usuarios.domain.usuario;

import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioDomainTest {

    @Test
    void debeCrearUsuarioActivo_cuandoSeLeAsignaUnaIdentidad() {
        // Arrange
        var id = UUID.randomUUID();
        var registro = RegistroUsuarioDomain.crear(
                "usr001", "Ana Pérez", "ana@uco.edu.co", "573001112233",
                "Ana", "Pérez", List.of("estudiante"));

        // Act
        var usuario = UsuarioDomain.crear(id, registro);

        // Assert
        assertThat(usuario.getId()).isEqualTo(id);
        assertThat(usuario.getIdentificador()).isEqualTo("usr001");
        assertThat(usuario.getNombre()).isEqualTo("Ana Pérez");
        assertThat(usuario.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(usuario.getContacto()).isEqualTo("573001112233");
        assertThat(usuario.getEstado()).isEqualTo(EstadoUsuario.ACTIVO);
    }

    @Test
    void debeReconstruirSinValidar_cuandoReconstruirEsInvocado() {
        // Arrange
        var id = UUID.randomUUID();

        // Act
        var usuario = UsuarioDomain.reconstruir(
                id, "usr002", "Juan Pérez", "juan@uco.edu.co", "573001112233", EstadoUsuario.INACTIVO);

        // Assert
        assertThat(usuario.getId()).isEqualTo(id);
        assertThat(usuario.getIdentificador()).isEqualTo("usr002");
        assertThat(usuario.getNombre()).isEqualTo("Juan Pérez");
        assertThat(usuario.getEmail()).isEqualTo("juan@uco.edu.co");
        assertThat(usuario.getContacto()).isEqualTo("573001112233");
        assertThat(usuario.getEstado()).isEqualTo(EstadoUsuario.INACTIVO);
        assertThat(usuario.esVacio()).isFalse();
    }

    @Test
    void debeExponerElCentinelaVacio_cuandoSeConsultaVacio() {
        // Assert
        assertThat(UsuarioDomain.VACIO.esVacio()).isTrue();
        assertThat(UsuarioDomain.VACIO.getEstado()).isEqualTo(EstadoUsuario.VACIO);
    }

    @Test
    void debeAsignarSoloLosCamposNoNulos_cuandoSeModificaConDatosParciales() {
        // Arrange
        var id = UUID.randomUUID();
        var usuario = UsuarioDomain.reconstruir(
                id, "usr003", "Nombre Original", "original@uco.edu.co", "573001112233", EstadoUsuario.ACTIVO);
        var datos = new ModificacionUsuarioDomain.DatosModificacionUsuario(
                null, "Nombre Nuevo", null, null, null, null);
        var modificacion = ModificacionUsuarioDomain.crear(id, datos, List.of());

        // Act
        usuario.modificar(modificacion);

        // Assert
        assertThat(usuario.getNombre()).isEqualTo("Nombre Nuevo");
        assertThat(usuario.getIdentificador()).isEqualTo("usr003");
        assertThat(usuario.getEmail()).isEqualTo("original@uco.edu.co");
        assertThat(usuario.getContacto()).isEqualTo("573001112233");
        assertThat(usuario.getId()).isEqualTo(id);
        assertThat(usuario.getEstado()).isEqualTo(EstadoUsuario.ACTIVO);
    }

    @Test
    void debeConservarEstadoEId_cuandoSeModificanTodosLosCamposPersonales() {
        // Arrange
        var id = UUID.randomUUID();
        var usuario = UsuarioDomain.reconstruir(
                id, "usr004", "Nombre Original", "original@uco.edu.co", "573001112233", EstadoUsuario.ACTIVO);
        var datos = new ModificacionUsuarioDomain.DatosModificacionUsuario(
                "usr005", "Nombre Actualizado", "actualizado@uco.edu.co", "573009998877", null, null);
        var modificacion = ModificacionUsuarioDomain.crear(id, datos, List.of());

        // Act
        usuario.modificar(modificacion);

        // Assert
        assertThat(usuario.getIdentificador()).isEqualTo("usr005");
        assertThat(usuario.getNombre()).isEqualTo("Nombre Actualizado");
        assertThat(usuario.getEmail()).isEqualTo("actualizado@uco.edu.co");
        assertThat(usuario.getContacto()).isEqualTo("573009998877");
        assertThat(usuario.getId()).isEqualTo(id);
        assertThat(usuario.getEstado()).isEqualTo(EstadoUsuario.ACTIVO);
    }

    @Test
    void debeIndicarActivo_cuandoEstadoEsActivo() {
        // Arrange
        var usuario = UsuarioDomain.reconstruir(UUID.randomUUID(), "usr006", "Nombre",
                "correo@uco.edu.co", "573001112233", EstadoUsuario.ACTIVO);

        // Act
        var activo = usuario.estaActivo();

        // Assert
        assertThat(activo).isTrue();
    }

    @Test
    void debeIndicarInactivo_cuandoEstadoEsInactivo() {
        // Arrange
        var usuario = UsuarioDomain.reconstruir(UUID.randomUUID(), "usr007", "Nombre",
                "correo@uco.edu.co", "573001112233", EstadoUsuario.INACTIVO);

        // Act
        var activo = usuario.estaActivo();

        // Assert
        assertThat(activo).isFalse();
    }
}
