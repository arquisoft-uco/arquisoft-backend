package com.arquisoft.artefactos.domain.estudiante;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteDomainTest {

    @Test
    void debeReconstruir_cuandoSeEntreganLosDatos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var estudiante = EstudianteDomain.reconstruir(id, "Luis", "luis@uco.edu.co");

        // Assert
        assertThat(estudiante.getId()).isEqualTo(id);
        assertThat(estudiante.getNombre()).isEqualTo("Luis");
        assertThat(estudiante.getEmail()).isEqualTo("luis@uco.edu.co");
        assertThat(estudiante.esVacio()).isFalse();
    }

    @Test
    void vacio_debeSerVacio() {
        // Act & Assert
        assertThat(EstudianteDomain.VACIO.esVacio()).isTrue();
    }
}
