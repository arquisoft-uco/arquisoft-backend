package com.arquisoft.biblioteca.domain.estudianteproyectogrado;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudianteProyectoGradoDomainTest {

    @Test
    void debeCrearVigente_cuandoLosDatosSonValidos() {
        // Arrange
        var autor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-15T10:00:00Z");

        // Act
        var vinculo = EstudianteProyectoGradoDomain.crear(autor, proyecto, ocurridoEn);

        // Assert
        assertThat(vinculo.getId()).isNotNull();
        assertThat(vinculo.getAutor()).isEqualTo(autor);
        assertThat(vinculo.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(vinculo.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(vinculo.estaEliminado()).isFalse();
        assertThat(EstudianteProyectoGradoDomain.VACIO.esVacio()).isTrue();
    }

    @Test
    void debeAcumularErrores_cuandoFaltanAutorProyectoYOcurridoEn() {
        // Act & Assert
        assertThatThrownBy(() -> EstudianteProyectoGradoDomain.crear(null, null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                BibliotecaCodes.EstudianteProyectoGrado.AUTOR_REQUERIDO,
                                BibliotecaCodes.EstudianteProyectoGrado.PROYECTO_GRADO_REQUERIDO,
                                BibliotecaCodes.EstudianteProyectoGrado.OCURRIDO_EN_REQUERIDO));
    }

    @Test
    void debeRemoverYReactivar_cuandoSeAplicanEventosPosteriores() {
        // Arrange
        var vinculo = EstudianteProyectoGradoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), Instant.parse("2026-11-15T10:00:00Z"));
        var retiro = Instant.parse("2026-11-16T10:00:00Z");

        // Act
        vinculo.remover(retiro);
        var eliminadoTrasRemover = vinculo.estaEliminado();
        var eliminadoEn = vinculo.getEliminadoEn();
        vinculo.reactivar(Instant.parse("2026-11-17T10:00:00Z"));

        // Assert
        assertThat(eliminadoTrasRemover).isTrue();
        assertThat(eliminadoEn).isEqualTo(retiro);
        assertThat(vinculo.estaEliminado()).isFalse();
    }
}
