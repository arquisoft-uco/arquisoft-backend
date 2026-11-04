package com.arquisoft.usuarios.domain.usuario;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import org.junit.jupiter.api.Test;

import java.time.Instant;
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
        assertThat(usuario.estaActivo()).isTrue();
        assertThat(usuario.estaEliminado()).isFalse();
        assertThat(usuario.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
    }

    @Test
    void debeReconstruirSinValidar_cuandoReconstruirEsInvocado() {
        // Arrange
        var id = UUID.randomUUID();

        // Act
        var usuario = UsuarioDomain.reconstruir(
                id, "usr002", "Juan Pérez", "juan@uco.edu.co", "573001112233", EstadoUsuario.INACTIVO, UtilFecha.VACIO);

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
                id, "usr003", "Nombre Original", "original@uco.edu.co", "573001112233", EstadoUsuario.ACTIVO, UtilFecha.VACIO);
        var datos = new ModificacionUsuarioDomain.DatosModificacionUsuario(
                null, null, null, "Nombre", "Nuevo");
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
                id, "usr004", "Nombre Original", "original@uco.edu.co", "573001112233", EstadoUsuario.ACTIVO, UtilFecha.VACIO);
        var datos = new ModificacionUsuarioDomain.DatosModificacionUsuario(
                "usr005", "actualizado@uco.edu.co", "573009998877", "Nombre", "Actualizado");
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
    void debeModificarYConservarEstadoInactivo_cuandoElUsuarioEstaInactivo() {
        // Arrange
        var id = UUID.randomUUID();
        var usuario = UsuarioDomain.reconstruir(id, "usr007", "Nombre",
                "correo@uco.edu.co", "573001112233", EstadoUsuario.INACTIVO, UtilFecha.VACIO);
        var datos = new ModificacionUsuarioDomain.DatosModificacionUsuario(
                null, null, null, "Nombre", "Corregido");
        var modificacion = ModificacionUsuarioDomain.crear(id, datos, List.of());

        // Act
        usuario.modificar(modificacion);

        // Assert
        assertThat(usuario.getNombre()).isEqualTo("Nombre Corregido");
        assertThat(usuario.getEstado()).isEqualTo(EstadoUsuario.INACTIVO);
    }

    @Test
    void debeConservarEliminado_cuandoSeModifica() {
        // Arrange
        var id = UUID.randomUUID();
        var eliminadoEn = Instant.parse("2026-09-25T10:15:30Z");
        var usuario = UsuarioDomain.reconstruir(id, "usr008", "Nombre",
                "correo@uco.edu.co", "573001112233", EstadoUsuario.INACTIVO, eliminadoEn);
        var datos = new ModificacionUsuarioDomain.DatosModificacionUsuario(
                null, null, null, "Nombre", "Corregido");
        var modificacion = ModificacionUsuarioDomain.crear(id, datos, List.of());

        // Act
        usuario.modificar(modificacion);

        // Assert
        assertThat(usuario.estaEliminado()).isTrue();
        assertThat(usuario.getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(usuario.getEstado()).isEqualTo(EstadoUsuario.INACTIVO);
    }

    @Test
    void debeReconstruirVigente_cuandoEliminadoEnEsNulo() {
        // Act
        var usuario = UsuarioDomain.reconstruir(UUID.randomUUID(), "usr009", "Nombre",
                "correo@uco.edu.co", "573001112233", EstadoUsuario.ACTIVO, null);

        // Assert
        assertThat(usuario.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(usuario.estaEliminado()).isFalse();
    }

    @Test
    void debeMarcarEliminadoSinCambiarEstado_cuandoSeElimina() {
        // Arrange
        var usuario = UsuarioDomain.reconstruir(UUID.randomUUID(), "usr010", "Nombre",
                "correo@uco.edu.co", "573001112233", EstadoUsuario.ACTIVO, UtilFecha.VACIO);
        var instante = Instant.parse("2026-09-26T08:00:00Z");

        // Act
        usuario.eliminar(instante);

        // Assert
        assertThat(usuario.estaEliminado()).isTrue();
        assertThat(usuario.getEliminadoEn()).isEqualTo(instante);
        assertThat(usuario.getEstado()).isEqualTo(EstadoUsuario.ACTIVO);
        assertThat(usuario.estaActivo()).isTrue();
    }

    @Test
    void debeActivarYRestaurar_cuandoSeActivaUnUsuarioEliminado() {
        // Arrange
        var usuario = UsuarioDomain.reconstruir(UUID.randomUUID(), "usr012", "Nombre",
                "correo@uco.edu.co", "573001112233", EstadoUsuario.INACTIVO, Instant.parse("2026-09-20T08:00:00Z"));

        // Act
        usuario.cambiarEstado(EstadoUsuario.ACTIVO);

        // Assert
        assertThat(usuario.getEstado()).isEqualTo(EstadoUsuario.ACTIVO);
        assertThat(usuario.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(usuario.estaEliminado()).isFalse();
    }

    @Test
    void debeInactivarConservandoEliminadoEn_cuandoElUsuarioYaEstaEliminado() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-26T08:00:00Z");
        var usuario = UsuarioDomain.reconstruir(UUID.randomUUID(), "usr013", "Nombre",
                "correo@uco.edu.co", "573001112233", EstadoUsuario.ACTIVO, eliminadoEn);

        // Act
        usuario.cambiarEstado(EstadoUsuario.INACTIVO);

        // Assert
        assertThat(usuario.getEstado()).isEqualTo(EstadoUsuario.INACTIVO);
        assertThat(usuario.getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(usuario.estaEliminado()).isTrue();
    }

    @Test
    void debeActivarSinMarcarEliminado_cuandoElUsuarioInactivoEstaVigente() {
        // Arrange
        var usuario = UsuarioDomain.reconstruir(UUID.randomUUID(), "usr014", "Nombre",
                "correo@uco.edu.co", "573001112233", EstadoUsuario.INACTIVO, UtilFecha.VACIO);

        // Act
        usuario.cambiarEstado(EstadoUsuario.ACTIVO);

        // Assert
        assertThat(usuario.estaActivo()).isTrue();
        assertThat(usuario.estaEliminado()).isFalse();
    }

    @Test
    void debeIndicarNoActivo_cuandoElUsuarioEstaInactivo() {
        // Arrange
        var usuario = UsuarioDomain.reconstruir(UUID.randomUUID(), "usr011", "Nombre",
                "correo@uco.edu.co", "573001112233", EstadoUsuario.INACTIVO, UtilFecha.VACIO);

        // Act
        var activo = usuario.estaActivo();

        // Assert
        assertThat(activo).isFalse();
    }
}
