package com.arquisoft.artefactos.domain.estudiante;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteDomainTest {

    @Test
    void debeEstarEliminado_soloCuandoTieneFechaDeBaja() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var vigente = EstudianteDomain.reconstruir(id, "1", "Laura", "l@uco.edu.co", null);
        var eliminado = EstudianteDomain.reconstruir(id, "1", "Laura", "l@uco.edu.co", Instant.now());

        // Assert
        assertThat(vigente.estaEliminado()).isFalse();
        assertThat(eliminado.estaEliminado()).isTrue();
        assertThat(vigente.getNombre()).isEqualTo("Laura");
    }
}
