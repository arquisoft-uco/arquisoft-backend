package com.arquisoft.artefactos.domain.estudianteproyectogrado;

import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudianteProyectoGradoDomainTest {

    private static final Instant ANTES = Instant.parse("2026-10-01T10:00:00Z");
    private static final Instant DESPUES = Instant.parse("2026-10-02T10:00:00Z");

    @Test
    void debeCrearVigenteConIdGenerado_cuandoDatosValidos() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act
        var vinculo = EstudianteProyectoGradoDomain.crear(estudiante, proyecto, ANTES);

        // Assert
        assertThat(UtilUUID.esPorDefecto(vinculo.getId())).isFalse();
        assertThat(vinculo.getEstudiante()).isEqualTo(estudiante);
        assertThat(vinculo.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(vinculo.getOcurridoEn()).isEqualTo(ANTES);
        assertThat(vinculo.getDestituidoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(vinculo.estaDestituido()).isFalse();
        assertThat(vinculo.esVacio()).isFalse();
    }

    @Test
    void debeAcumularErrores_cuandoTodosLosDatosSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> EstudianteProyectoGradoDomain.crear(null, null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.campo())
                        .containsExactlyInAnyOrder(
                                ArtefactosFields.EstudianteProyectoGrado.ESTUDIANTE,
                                ArtefactosFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                                ArtefactosFields.EstudianteProyectoGrado.OCURRIDO_EN));
    }

    @Test
    void debeFijarDestituidoEnYOcurridoEn_cuandoSeDestituye() {
        // Arrange
        var vinculo = EstudianteProyectoGradoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), ANTES);

        // Act
        vinculo.destituir(DESPUES);

        // Assert
        assertThat(vinculo.getDestituidoEn()).isEqualTo(DESPUES);
        assertThat(vinculo.getOcurridoEn()).isEqualTo(DESPUES);
        assertThat(vinculo.estaDestituido()).isTrue();
    }

    @Test
    void debeLimpiarDestituidoEnYFijarOcurridoEn_cuandoSeReactiva() {
        // Arrange
        var vinculo = EstudianteProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), ANTES, ANTES);

        // Act
        vinculo.reactivar(DESPUES);

        // Assert
        assertThat(vinculo.getDestituidoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(vinculo.getOcurridoEn()).isEqualTo(DESPUES);
        assertThat(vinculo.estaDestituido()).isFalse();
    }

    @Test
    void debeConservarElEstado_cuandoDestituirOReactivarRecibenOcurridoEnNulo() {
        // Arrange
        var vinculo = EstudianteProyectoGradoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), ANTES);

        // Act & Assert
        assertThatThrownBy(() -> vinculo.destituir(null)).isInstanceOf(DomainValidationException.class);
        assertThatThrownBy(() -> vinculo.reactivar(null)).isInstanceOf(DomainValidationException.class);
        assertThat(vinculo.estaDestituido()).isFalse();
    }

    @Test
    void debeReconstruirConDestituidoEnVacio_cuandoLlegaNulo() {
        // Act
        var vinculo = EstudianteProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), ANTES, null);

        // Assert
        assertThat(vinculo.getDestituidoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(EstudianteProyectoGradoDomain.VACIO.esVacio()).isTrue();
    }
}
