package com.arquisoft.artefactos.domain.asesorproyectogrado;

import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorProyectoGradoDomainTest {

    private static final Instant ANTES = Instant.parse("2026-10-01T10:00:00Z");
    private static final Instant DESPUES = Instant.parse("2026-10-02T10:00:00Z");

    @Test
    void debeCrearVigenteConIdGenerado_cuandoDatosValidos() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act
        var vinculo = AsesorProyectoGradoDomain.crear(asesor, proyecto, ANTES);

        // Assert
        assertThat(UtilUUID.esPorDefecto(vinculo.getId())).isFalse();
        assertThat(vinculo.getAsesor()).isEqualTo(asesor);
        assertThat(vinculo.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(vinculo.getOcurridoEn()).isEqualTo(ANTES);
        assertThat(vinculo.getFinalizadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(vinculo.estaFinalizado()).isFalse();
        assertThat(vinculo.esVacio()).isFalse();
    }

    @Test
    void debeAcumularErrores_cuandoTodosLosDatosSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> AsesorProyectoGradoDomain.crear(null, null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.campo())
                        .containsExactlyInAnyOrder(
                                ArtefactosFields.AsesorProyectoGrado.ASESOR,
                                ArtefactosFields.AsesorProyectoGrado.PROYECTO_GRADO,
                                ArtefactosFields.AsesorProyectoGrado.OCURRIDO_EN));
    }

    @Test
    void debeFijarFinalizadoEnYOcurridoEn_cuandoSeFinaliza() {
        // Arrange
        var vinculo = AsesorProyectoGradoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), ANTES);

        // Act
        vinculo.finalizar(DESPUES);

        // Assert
        assertThat(vinculo.getFinalizadoEn()).isEqualTo(DESPUES);
        assertThat(vinculo.getOcurridoEn()).isEqualTo(DESPUES);
        assertThat(vinculo.estaFinalizado()).isTrue();
    }

    @Test
    void debeLimpiarFinalizadoEnYFijarOcurridoEn_cuandoSeReactiva() {
        // Arrange
        var vinculo = AsesorProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                ANTES, ANTES);

        // Act
        vinculo.reactivar(DESPUES);

        // Assert
        assertThat(vinculo.getFinalizadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(vinculo.getOcurridoEn()).isEqualTo(DESPUES);
        assertThat(vinculo.estaFinalizado()).isFalse();
    }

    @Test
    void debeConservarElEstado_cuandoFinalizarOReactivarRecibenOcurridoEnNulo() {
        // Arrange
        var vinculo = AsesorProyectoGradoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), ANTES);

        // Act & Assert
        assertThatThrownBy(() -> vinculo.finalizar(null)).isInstanceOf(DomainValidationException.class);
        assertThatThrownBy(() -> vinculo.reactivar(null)).isInstanceOf(DomainValidationException.class);
        assertThat(vinculo.estaFinalizado()).isFalse();
    }

    @Test
    void debeReconstruirConFinalizadoEnVacio_cuandoLlegaNulo() {
        // Act
        var vinculo = AsesorProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), ANTES, null);

        // Assert
        assertThat(vinculo.getFinalizadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(AsesorProyectoGradoDomain.VACIO.esVacio()).isTrue();
    }
}
