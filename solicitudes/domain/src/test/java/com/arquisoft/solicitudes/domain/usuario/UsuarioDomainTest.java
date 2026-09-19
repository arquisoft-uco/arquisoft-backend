package com.arquisoft.solicitudes.domain.usuario;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UsuarioDomainTest {

    @Test
    void debeCrearLaReplica_cuandoLosCincoCamposSonValidos() {
        // Arrange
        UUID id = UUID.randomUUID();
        Instant ocurridoEn = Instant.now();

        // Act
        UsuarioDomain usuario = UsuarioDomain.crear(id, "EST-001", "Ana Estudiante", "ana@uco.edu.co", ocurridoEn);

        // Assert
        assertThat(usuario.getId()).isEqualTo(id);
        assertThat(usuario.getIdentificador()).isEqualTo("EST-001");
        assertThat(usuario.getNombre()).isEqualTo("Ana Estudiante");
        assertThat(usuario.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(usuario.getOcurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoLosCincoCamposFaltan() {
        // Act
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> UsuarioDomain.crear(null, "  ", "", null, null));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Usuario.ID)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Usuario.IDENTIFICADOR)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Usuario.NOMBRE)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Usuario.EMAIL)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Usuario.OCURRIDO_EN)).isTrue();
    }

    @Test
    void debeReconstruirSinValidar_cuandoReconstruirEsInvocado() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        UsuarioDomain usuario = UsuarioDomain.reconstruir(id, null, null, null, null);

        // Assert
        assertThat(usuario.getId()).isEqualTo(id);
        assertThat(usuario.getNombre()).isNull();
        assertThat(usuario.getOcurridoEn()).isNull();
    }

    @Test
    void debeReportarVacio_cuandoEsElCentinela() {
        // Act & Assert
        assertThat(UsuarioDomain.VACIO.esVacio()).isTrue();
        assertThat(UsuarioDomain.crear(UUID.randomUUID(), "EST-001", "Ana Estudiante", "ana@uco.edu.co", Instant.now())
                .esVacio()).isFalse();
    }
}
