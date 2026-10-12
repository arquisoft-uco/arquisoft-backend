package com.arquisoft.usuarios.domain.usuario;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.tuple;

class ModificacionUsuarioDomainTest {

    @Test
    void debeDerivarNombreCompleto_cuandoLlegaNombresYApellidos() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var datos = new ModificacionUsuarioDomain.DatosModificacionUsuario(
                null, null, null, "Nuevo", "Nombre");

        // Act
        var modificacion = ModificacionUsuarioDomain.crear(usuarioId, datos, List.of());

        // Assert
        assertThat(modificacion.getUsuario()).isEqualTo(usuarioId);
        assertThat(modificacion.getNombre()).isEqualTo("Nuevo Nombre");
        assertThat(modificacion.getNombres()).isEqualTo("Nuevo");
        assertThat(modificacion.getApellidos()).isEqualTo("Nombre");
        assertThat(modificacion.getIdentificador()).isNull();
        assertThat(modificacion.getEmail()).isNull();
        assertThat(modificacion.getContacto()).isNull();
    }

    @Test
    void debeRecortarAntesDeDerivarElNombre_cuandoLlegaConEspacios() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var datos = new ModificacionUsuarioDomain.DatosModificacionUsuario(
                null, null, null, "  Juan Carlos ", " Perez  ");

        // Act
        var modificacion = ModificacionUsuarioDomain.crear(usuarioId, datos, List.of());

        // Assert
        assertThat(modificacion.getNombres()).isEqualTo("Juan Carlos");
        assertThat(modificacion.getApellidos()).isEqualTo("Perez");
        assertThat(modificacion.getNombre()).isEqualTo("Juan Carlos Perez");
    }

    @Test
    void debeRechazar_cuandoLlegaNombresSinApellidos() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var datos = new ModificacionUsuarioDomain.DatosModificacionUsuario(
                null, null, null, "Juan", null);

        // Act
        var excepcion = catchDomainValidationException(usuarioId, datos);

        // Assert
        assertThat(excepcion.getValidationResult().getErrores())
                .extracting("campo", "codigoError")
                .containsExactly(tuple(
                        "apellidos", UsuariosCodes.Usuario.APELLIDOS_REQUERIDO));
    }

    @Test
    void debeRechazar_cuandoLlegaApellidosSinNombres() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var datos = new ModificacionUsuarioDomain.DatosModificacionUsuario(
                null, null, null, null, "Perez");

        // Act
        var excepcion = catchDomainValidationException(usuarioId, datos);

        // Assert
        assertThat(excepcion.getValidationResult().getErrores())
                .extracting("campo", "codigoError")
                .containsExactly(tuple(
                        "nombres", UsuariosCodes.Usuario.NOMBRES_REQUERIDO));
    }

    @Test
    void debeRechazar_cuandoElNombreCompletoTieneFormatoInvalido() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var datos = new ModificacionUsuarioDomain.DatosModificacionUsuario(
                null, null, null, "Juan1", "Perez");

        // Act
        var excepcion = catchDomainValidationException(usuarioId, datos);

        // Assert
        assertThat(excepcion.getValidationResult().getErrores())
                .extracting("codigoError")
                .containsExactly(UsuariosCodes.Usuario.NOMBRE_FORMATO);
    }

    @Test
    void debeCrearSoloConRoles_cuandoNoHayDatosPersonales() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var datos = new ModificacionUsuarioDomain.DatosModificacionUsuario(
                null, null, null, null, null);

        // Act
        var modificacion = ModificacionUsuarioDomain.crear(usuarioId, datos, List.of("estudiante"));

        // Assert
        assertThat(modificacion.getRoles()).containsExactly("estudiante");
        assertThat(modificacion.modificaDatos()).isFalse();
        assertThat(modificacion.modificaIdentidad()).isFalse();
    }

    @Test
    void debeAcumularVariosErrores_cuandoVariosCamposInvalidosLlegan() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var datos = new ModificacionUsuarioDomain.DatosModificacionUsuario(
                null, "no-es-un-correo", "abcXYZ", null, null);

        // Act
        var excepcion = catchDomainValidationException(usuarioId, datos);

        // Assert
        assertThat(excepcion.getValidationResult().getErrores())
                .extracting("campo")
                .contains("email", "contacto");
    }

    @Test
    void debeAceptarCamposNulos_cuandoSoloAlgunosCamposCambian() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var datos = new ModificacionUsuarioDomain.DatosModificacionUsuario(
                "usr999", "correo@uco.edu.co", null, "Juan", "Perez");

        // Act
        var modificacion = ModificacionUsuarioDomain.crear(usuarioId, datos, List.of());

        // Assert
        assertThat(modificacion.getIdentificador()).isEqualTo("usr999");
        assertThat(modificacion.getEmail()).isEqualTo("correo@uco.edu.co");
        assertThat(modificacion.getNombres()).isEqualTo("Juan");
        assertThat(modificacion.getApellidos()).isEqualTo("Perez");
        assertThat(modificacion.getNombre()).isEqualTo("Juan Perez");
        assertThat(modificacion.getContacto()).isNull();
    }

    @Test
    void debeIndicarQueModificaDatosYNoIdentidad_cuandoSoloCambiaContacto() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var datos = new ModificacionUsuarioDomain.DatosModificacionUsuario(
                null, null, "3001112233", null, null);

        // Act
        var modificacion = ModificacionUsuarioDomain.crear(usuarioId, datos, List.of());

        // Assert
        assertThat(modificacion.modificaDatos()).isTrue();
        assertThat(modificacion.modificaIdentidad()).isFalse();
    }

    @Test
    void debeIndicarQueModificaIdentidad_cuandoCambiaEmail() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var datos = new ModificacionUsuarioDomain.DatosModificacionUsuario(
                null, "otro@uco.edu.co", null, null, null);

        // Act
        var modificacion = ModificacionUsuarioDomain.crear(usuarioId, datos, List.of());

        // Assert
        assertThat(modificacion.modificaIdentidad()).isTrue();
    }

    @Test
    void debeConfirmarPertenenciaDeRol_cuandoSeConsultaContieneRol() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var datos = new ModificacionUsuarioDomain.DatosModificacionUsuario(
                null, null, null, null, null);

        // Act
        var modificacion = ModificacionUsuarioDomain.crear(usuarioId, datos, List.of("estudiante", "asesor"));

        // Assert
        assertThat(modificacion.contieneRol("estudiante")).isTrue();
        assertThat(modificacion.contieneRol("coordinador")).isFalse();
    }

    private DomainValidationException catchDomainValidationException(
            UUID usuarioId, ModificacionUsuarioDomain.DatosModificacionUsuario datos) {
        return (DomainValidationException) catchThrowable(
                () -> ModificacionUsuarioDomain.crear(usuarioId, datos, List.of()));
    }
}
