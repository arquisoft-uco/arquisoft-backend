package com.arquisoft.biblioteca.domain.estudianteproyectogrado;

import com.arquisoft.biblioteca.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregacionEstudiantesProyectoGradoDomainTest {

    @Test
    void debeCrearElCompuesto_cuandoHayProyectoYVinculos() {
        // Arrange
        var ocurridoEn = Instant.parse("2026-11-15T10:00:00Z");
        var proyecto = ProyectoGradoDomain.crear(UtilUUID.generarNuevoUUID(), "Titulo", ocurridoEn);
        var vinculo = EstudianteProyectoGradoDomain.crear(UtilUUID.generarNuevoUUID(), proyecto.getId(), ocurridoEn);

        // Act
        var agregacion = AgregacionEstudiantesProyectoGradoDomain.crear(proyecto, List.of(vinculo));

        // Assert
        assertThat(agregacion.getProyecto()).isSameAs(proyecto);
        assertThat(agregacion.getVinculos()).containsExactly(vinculo);
    }

    @Test
    void debeAcumularErrores_cuandoFaltanProyectoYVinculos() {
        // Act & Assert
        assertThatThrownBy(() -> AgregacionEstudiantesProyectoGradoDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                BibliotecaCodes.EstudianteProyectoGrado.PROYECTO_GRADO_REQUERIDO,
                                BibliotecaCodes.EstudianteProyectoGrado.AUTORES_REQUERIDOS));
    }
}
