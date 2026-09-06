package com.arquisoft.solicitudes.domain.usuario;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UsuarioDomainTest {

    @Test
    void debeCrearLaReplica_cuandoLosCuatroCamposSonValidos() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        UsuarioDomain usuario = UsuarioDomain.crear(id, "EST-001", "Ana Estudiante", "ana@uco.edu.co");

        // Assert
        assertThat(usuario.getId()).isEqualTo(id);
        assertThat(usuario.getIdentificador()).isEqualTo("EST-001");
        assertThat(usuario.getNombre()).isEqualTo("Ana Estudiante");
        assertThat(usuario.getEmail()).isEqualTo("ana@uco.edu.co");
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoLosCuatroCamposFaltan() {
        // Act
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> UsuarioDomain.crear(null, "  ", "", null));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Usuario.ID)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Usuario.IDENTIFICADOR)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Usuario.NOMBRE)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Usuario.EMAIL)).isTrue();
    }

    @Test
    void debeReconstruirSinValidar_cuandoReconstruirEsInvocado() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        UsuarioDomain usuario = UsuarioDomain.reconstruir(id, null, null, null);

        // Assert
        assertThat(usuario.getId()).isEqualTo(id);
        assertThat(usuario.getNombre()).isNull();
    }
}
