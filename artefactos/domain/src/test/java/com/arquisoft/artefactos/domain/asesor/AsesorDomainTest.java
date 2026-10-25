package com.arquisoft.artefactos.domain.asesor;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorDomainTest {

    @Test
    void debeReconstruir_cuandoSeEntreganLosDatos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var asesor = AsesorDomain.reconstruir(id, "Ana", "ana@uco.edu.co");

        // Assert
        assertThat(asesor.getId()).isEqualTo(id);
        assertThat(asesor.getNombre()).isEqualTo("Ana");
        assertThat(asesor.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(asesor.esVacio()).isFalse();
    }

    @Test
    void vacio_debeSerVacio() {
        // Act & Assert
        assertThat(AsesorDomain.VACIO.esVacio()).isTrue();
    }
}
