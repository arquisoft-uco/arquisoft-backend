package com.arquisoft.evaluaciones.domain.estudianteproyectogrado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudianteProyectoGradoDomainTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-20T10:00:00Z");

    @Test
    void debeCrearVinculoVigenteConIdGenerado_cuandoDatosValidos() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();

        // Act
        var vinculo = EstudianteProyectoGradoDomain.crear(estudiante, proyectoGrado, OCURRIDO_EN);

        // Assert
        assertThat(vinculo.getId()).isNotNull();
        assertThat(vinculo.getEstudiante()).isEqualTo(estudiante);
        assertThat(vinculo.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(vinculo.getOcurridoEn()).isEqualTo(OCURRIDO_EN);
        assertThat(vinculo.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(vinculo.estaEliminado()).isFalse();
        assertThat(vinculo.esVacio()).isFalse();
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoEstudianteProyectoYOcurridoEnSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> EstudianteProyectoGradoDomain.crear(null, null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(3);
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(EvaluacionesFields.EstudianteProyectoGrado.ESTUDIANTE);
                        assertThat(error.codigoError())
                                .isEqualTo(EvaluacionesCodes.EstudianteProyectoGrado.ESTUDIANTE_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo())
                                .isEqualTo(EvaluacionesFields.EstudianteProyectoGrado.PROYECTO_GRADO);
                        assertThat(error.codigoError())
                                .isEqualTo(EvaluacionesCodes.EstudianteProyectoGrado.PROYECTO_GRADO_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(EvaluacionesFields.EstudianteProyectoGrado.OCURRIDO_EN);
                        assertThat(error.codigoError())
                                .isEqualTo(EvaluacionesCodes.EstudianteProyectoGrado.OCURRIDO_EN_REQUERIDO);
                    });
                });
    }

    @Test
    void debeLimpiarEliminadoEnYActualizarOcurridoEn_cuandoSeReactiva() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-10-19T10:00:00Z");
        var vinculo = EstudianteProyectoGradoDomain.reconstruir(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), eliminadoEn, eliminadoEn);

        // Act
        vinculo.reactivar(OCURRIDO_EN);

        // Assert
        assertThat(vinculo.estaEliminado()).isFalse();
        assertThat(vinculo.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(vinculo.getOcurridoEn()).isEqualTo(OCURRIDO_EN);
    }

    @Test
    void debeRechazarReactivar_cuandoOcurridoEnEsNulo() {
        // Arrange
        var vinculo = EstudianteProyectoGradoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), OCURRIDO_EN);

        // Act & Assert
        assertThatThrownBy(() -> vinculo.reactivar(null)).isInstanceOf(DomainValidationException.class);
    }

    @Test
    void debeReconstruirConVacioPorDefecto_cuandoEliminadoEnEsNulo() {
        // Act
        var vinculo = EstudianteProyectoGradoDomain.reconstruir(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), OCURRIDO_EN, null);

        // Assert
        assertThat(vinculo.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(vinculo.estaEliminado()).isFalse();
    }

    @Test
    void debeFijarEliminadoEnIgualAOcurridoEn_cuandoSeRemueve() {
        // Arrange
        var vinculo = EstudianteProyectoGradoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), OCURRIDO_EN);
        var removidoEn = OCURRIDO_EN.plusSeconds(60);

        // Act
        vinculo.remover(removidoEn);

        // Assert
        assertThat(vinculo.estaEliminado()).isTrue();
        assertThat(vinculo.getEliminadoEn()).isEqualTo(removidoEn);
        assertThat(vinculo.getOcurridoEn()).isEqualTo(removidoEn);
    }

    @Test
    void debeRechazarRemover_cuandoOcurridoEnEsNulo() {
        // Arrange
        var vinculo = EstudianteProyectoGradoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), OCURRIDO_EN);

        // Act & Assert
        assertThatThrownBy(() -> vinculo.remover(null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactly(EvaluacionesFields.EstudianteProyectoGrado.OCURRIDO_EN));
    }

    @Test
    void debeReconocerseComoVacio_cuandoEsElCentinela() {
        // Assert
        assertThat(EstudianteProyectoGradoDomain.VACIO.esVacio()).isTrue();
        assertThat(UtilUUID.esPorDefecto(EstudianteProyectoGradoDomain.VACIO.getId())).isTrue();
    }

    @Test
    void debeRechazarElProyectoDeCeros_cuandoSeCrea() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var centinela = UtilUUID.obtenerUUIDPorDefecto();

        // Act & Assert
        assertThatThrownBy(() -> EstudianteProyectoGradoDomain.crear(estudiante, centinela, OCURRIDO_EN))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactly(EvaluacionesFields.EstudianteProyectoGrado.PROYECTO_GRADO));
    }
}
