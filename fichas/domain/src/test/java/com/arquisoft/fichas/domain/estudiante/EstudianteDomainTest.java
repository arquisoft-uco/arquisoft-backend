package com.arquisoft.fichas.domain.estudiante;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteDomainTest {

    @Test
    void debeReconstruir_cuandoDatosValidos() {
        // Arrange
        var id = UUID.randomUUID();
        var identificador = "1234567890";
        var nombre = "Juan Pérez";
        var email = "juan.perez@example.com";
        var ocurridoEn = Instant.now();

        // Act
        var estudiante = EstudianteDomain.reconstruir(id, identificador, nombre, email, ocurridoEn);

        // Assert
        assertThat(estudiante).isNotNull();
        assertThat(estudiante.getId()).isEqualTo(id);
        assertThat(estudiante.getIdentificador()).isEqualTo(identificador);
        assertThat(estudiante.getNombre()).isEqualTo(nombre);
        assertThat(estudiante.getEmail()).isEqualTo(email);
        assertThat(estudiante.getOcurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeReconstruirSinValidar_cuandoReconstruirEsInvocado() {
        // Arrange
        var id = UUID.randomUUID();
        String identificador = null;
        String nombre = null;
        String email = null;
        Instant ocurridoEn = null;

        // Act
        var estudiante = EstudianteDomain.reconstruir(id, identificador, nombre, email, ocurridoEn);

        // Assert
        assertThat(estudiante).isNotNull();
        assertThat(estudiante.getId()).isEqualTo(id);
        assertThat(estudiante.getIdentificador()).isNull();
        assertThat(estudiante.getNombre()).isNull();
        assertThat(estudiante.getEmail()).isNull();
        assertThat(estudiante.getOcurridoEn()).isNull();
    }
}
