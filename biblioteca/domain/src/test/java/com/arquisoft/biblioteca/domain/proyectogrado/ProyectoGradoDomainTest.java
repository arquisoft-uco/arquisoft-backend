package com.arquisoft.biblioteca.domain.proyectogrado;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProyectoGradoDomainTest {

    @Test
    void debeCrearConTituloRecortado_cuandoLosDatosSonValidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-15T10:00:00Z");

        // Act
        var proyecto = ProyectoGradoDomain.crear(id, "  Sistema de riego  ", ocurridoEn);

        // Assert
        assertThat(proyecto.getId()).isEqualTo(id);
        assertThat(proyecto.getTitulo()).isEqualTo("Sistema de riego");
        assertThat(proyecto.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(proyecto.esVacio()).isFalse();
        assertThat(ProyectoGradoDomain.VACIO.esVacio()).isTrue();
    }

    @Test
    void debeAcumularErrores_cuandoFaltanIdTituloYOcurridoEn() {
        // Act & Assert
        assertThatThrownBy(() -> ProyectoGradoDomain.crear(null, " ", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                BibliotecaCodes.ProyectoGrado.ID_REQUERIDO,
                                BibliotecaCodes.ProyectoGrado.TITULO_REQUERIDO,
                                BibliotecaCodes.ProyectoGrado.OCURRIDO_EN_REQUERIDO));
    }

    @Test
    void debeActualizarTituloYFecha_cuandoSeReconstruyeYSeActualiza() {
        // Arrange
        var proyecto = ProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "Viejo", Instant.parse("2026-11-15T10:00:00Z"));
        var nuevaFecha = Instant.parse("2026-11-16T10:00:00Z");

        // Act
        proyecto.actualizar(" Nuevo ", nuevaFecha);

        // Assert
        assertThat(proyecto.getTitulo()).isEqualTo("Nuevo");
        assertThat(proyecto.getOcurridoEn()).isEqualTo(nuevaFecha);
    }
}
