package com.arquisoft.artefactos.domain.estudianteartefacto.model;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CupoEstudiantesArtefactoTest {

    @Test
    void debeExcluirVinculadosYConservarOrden_cuandoCalculaNuevos() {
        // Arrange
        var a = UtilUUID.generarNuevoUUID();
        var b = UtilUUID.generarNuevoUUID();
        var c = UtilUUID.generarNuevoUUID();
        var cupo = new CupoEstudiantesArtefacto(List.of(c, a, b), List.of(a), List.of(a, b, c));

        // Act
        var nuevos = cupo.nuevos();

        // Assert
        assertThat(nuevos).containsExactly(c, b);
    }

    @Test
    void debeUsarListasVacias_cuandoLasListasSonNulas() {
        // Act
        var cupo = new CupoEstudiantesArtefacto(null, null, null);

        // Assert
        assertThat(cupo.nuevos()).isEmpty();
        assertThat(cupo.vigentesProyecto()).isEmpty();
    }
}
