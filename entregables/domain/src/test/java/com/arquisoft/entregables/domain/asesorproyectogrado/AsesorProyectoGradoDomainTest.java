package com.arquisoft.entregables.domain.asesorproyectogrado;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorProyectoGradoDomainTest {

    @Test
    void debeCrearVigenteConIdGenerado_cuandoDatosValidos() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-10T10:00:00Z");

        // Act
        var vinculo = AsesorProyectoGradoDomain.crear(asesor, proyecto, ocurridoEn);

        // Assert
        assertThat(UtilUUID.esPorDefecto(vinculo.getId())).isFalse();
        assertThat(vinculo.getAsesor()).isEqualTo(asesor);
        assertThat(vinculo.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(vinculo.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(vinculo.estaFinalizado()).isFalse();
        assertThat(vinculo.esVacio()).isFalse();
    }

    @Test
    void debeAcumularErrores_cuandoAsesorProyectoYFechaSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> AsesorProyectoGradoDomain.crear(null, null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting(error -> error.campo()).containsExactlyInAnyOrder(
                            EntregablesFields.AsesorProyectoGrado.ASESOR,
                            EntregablesFields.AsesorProyectoGrado.PROYECTO_GRADO,
                            EntregablesFields.AsesorProyectoGrado.OCURRIDO_EN);
                    assertThat(errores).extracting(error -> error.codigoError()).containsExactlyInAnyOrder(
                            EntregablesCodes.AsesorProyectoGrado.ASESOR_REQUERIDO,
                            EntregablesCodes.AsesorProyectoGrado.PROYECTO_GRADO_REQUERIDO,
                            EntregablesCodes.AsesorProyectoGrado.OCURRIDO_EN_REQUERIDO);
                });
    }

    @Test
    void debeReconstruirSinRevalidarYExponerElVacio_cuandoLlegaDePersistencia() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var finalizadoEn = Instant.parse("2026-11-11T10:00:00Z");

        // Act
        var finalizado = AsesorProyectoGradoDomain.reconstruir(id, null, null, null, finalizadoEn);
        var sinFinalizar = AsesorProyectoGradoDomain.reconstruir(id, null, null, null, null);

        // Assert
        assertThat(finalizado.getId()).isEqualTo(id);
        assertThat(finalizado.estaFinalizado()).isTrue();
        assertThat(sinFinalizar.getFinalizadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(sinFinalizar.estaFinalizado()).isFalse();
        assertThat(AsesorProyectoGradoDomain.VACIO.esVacio()).isTrue();
    }

    @Test
    void debeFinalizarYLuegoReactivar_cuandoLlegaUnaFechaValida() {
        // Arrange
        var vinculo = AsesorProyectoGradoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), Instant.parse("2026-11-10T10:00:00Z"));
        var cierre = Instant.parse("2026-11-12T10:00:00Z");
        var reasignacion = Instant.parse("2026-11-13T10:00:00Z");

        // Act
        vinculo.finalizar(cierre);
        var finalizado = vinculo.estaFinalizado();
        var finalizadoEn = vinculo.getFinalizadoEn();
        vinculo.reactivar(reasignacion);

        // Assert
        assertThat(finalizado).isTrue();
        assertThat(finalizadoEn).isEqualTo(cierre);
        assertThat(vinculo.estaFinalizado()).isFalse();
        assertThat(vinculo.getOcurridoEn()).isEqualTo(reasignacion);
    }

    @Test
    void debeRechazarFinalizarYReactivar_cuandoLaFechaEsNula() {
        // Arrange
        var vinculo = AsesorProyectoGradoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), Instant.now());

        // Act & Assert
        assertThatThrownBy(() -> vinculo.finalizar(null)).isInstanceOf(DomainValidationException.class);
        assertThatThrownBy(() -> vinculo.reactivar(null)).isInstanceOf(DomainValidationException.class);
        assertThat(vinculo.estaFinalizado()).isFalse();
    }
}
