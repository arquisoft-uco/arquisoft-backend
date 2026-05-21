package com.arquisoft.fichas.domain.model;

import com.arquisoft.shared.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FichaPerfilTest {

    // ─── Helpers de Arrange ───────────────────────────────────────────────────

    private AsesorFicha asesorValido() {
        return AsesorFicha.rebuild(UUID.randomUUID(), "DOC-001", "Asesor Test", "asesor@test.com");
    }

    // ─── Tests ────────────────────────────────────────────────────────────────

    @Test
    void debeReconstruirSinEventos_cuandoRebuildEsInvocado() {
        // Arrange
        UUID id = UUID.randomUUID();
        String titulo = "Proyecto de Grado Arquisoft";
        AsesorFicha asesor = asesorValido();

        // Act
        FichaPerfil ficha = FichaPerfil.rebuild(id, titulo, asesor);

        // Assert
        assertThat(ficha.getId()).isEqualTo(id);
        assertThat(ficha.getTituloProyecto()).isEqualTo(titulo);
        assertThat(ficha.getAsesorFicha()).isEqualTo(asesor);
    }

    @Test
    void debeLanzarExcepcion_cuandoTituloProyectoEsNuloOVacioEnBuild() {
        // Arrange
        AsesorFicha asesor = asesorValido();

        // Act & Assert — null
        assertThatThrownBy(() -> FichaPerfil.build(null, asesor))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("nulo ni vacío");

        // Act & Assert — blank (solo espacios)
        assertThatThrownBy(() -> FichaPerfil.build("   ", asesor))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("nulo ni vacío");

        // Act & Assert — cadena vacía
        assertThatThrownBy(() -> FichaPerfil.build("", asesor))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("nulo ni vacío");
    }

    @Test
    void debeLanzarExcepcion_cuandoTituloProyectoSuperaLongitudMaxima() {
        // Arrange
        AsesorFicha asesor = asesorValido();
        String tituloLargo = "A".repeat(101); // 101 caracteres — supera el máximo de 100

        // Act & Assert
        assertThatThrownBy(() -> FichaPerfil.build(tituloLargo, asesor))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("100 caracteres");
    }
}
