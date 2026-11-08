package com.arquisoft.artefactos.domain.proyectogrado;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.message.constant.ArtefactosLimits;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProyectoGradoDomainTest {

    @Test
    void debeCrearConNombreRecortado_cuandoDatosValidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var proyecto = ProyectoGradoDomain.crear(id, "  Sistema de gestion  ");

        // Assert
        assertThat(proyecto.getId()).isEqualTo(id);
        assertThat(proyecto.getNombre()).isEqualTo("Sistema de gestion");
    }

    @Test
    void debeAcumularErrores_cuandoIdNuloYNombreEnBlanco() {
        // Act & Assert
        assertThatThrownBy(() -> ProyectoGradoDomain.crear(null, " "))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.campo())
                        .containsExactlyInAnyOrder(
                                ArtefactosFields.ProyectoGrado.ID, ArtefactosFields.ProyectoGrado.NOMBRE));
    }

    @Test
    void debeRechazar_cuandoElNombreSuperaElMaximo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var nombre = "a".repeat(ArtefactosLimits.ProyectoGrado.NOMBRE_MAX + 1);

        // Act & Assert
        assertThatThrownBy(() -> ProyectoGradoDomain.crear(id, nombre))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(ArtefactosCodes.ProyectoGrado.NOMBRE_DEMASIADO_LARGO)));
    }

    @Test
    void debeSerVacio_cuandoEsElCentinela() {
        // Act & Assert
        assertThat(ProyectoGradoDomain.VACIO.esVacio()).isTrue();
        assertThat(UtilUUID.esPorDefecto(ProyectoGradoDomain.VACIO.getId())).isTrue();
        assertThat(ProyectoGradoDomain.VACIO.getNombre()).isEmpty();
    }

    @Test
    void debeCrearSinFechas_cuandoDatosValidos() {
        // Act
        var proyecto = ProyectoGradoDomain.crear(UtilUUID.generarNuevoUUID(), "Proyecto");

        // Assert
        assertThat(proyecto.getEntregadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(proyecto.getFinalizadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(proyecto.estaEntregado()).isFalse();
        assertThat(proyecto.estaFinalizado()).isFalse();
        assertThat(proyecto.estaCongelado()).isFalse();
    }

    @Test
    void debeReconstruirConFechasYEstarCongelado_cuandoEntregado() {
        // Arrange
        var entregadoEn = Instant.parse("2026-11-01T10:00:00Z");

        // Act
        var proyecto = ProyectoGradoDomain.reconstruir(UtilUUID.generarNuevoUUID(), "P", entregadoEn, UtilFecha.VACIO);

        // Assert
        assertThat(proyecto.getEntregadoEn()).isEqualTo(entregadoEn);
        assertThat(proyecto.estaEntregado()).isTrue();
        assertThat(proyecto.estaFinalizado()).isFalse();
        assertThat(proyecto.estaCongelado()).isTrue();
    }

    @Test
    void debeEstarCongelado_cuandoSoloFinalizado() {
        // Arrange
        var finalizadoEn = Instant.parse("2026-11-02T10:00:00Z");

        // Act
        var proyecto = ProyectoGradoDomain.reconstruir(UtilUUID.generarNuevoUUID(), "P", UtilFecha.VACIO, finalizadoEn);

        // Assert
        assertThat(proyecto.getFinalizadoEn()).isEqualTo(finalizadoEn);
        assertThat(proyecto.estaEntregado()).isFalse();
        assertThat(proyecto.estaFinalizado()).isTrue();
        assertThat(proyecto.estaCongelado()).isTrue();
    }

    @Test
    void debeSerVacioSinFechas_cuandoEsElCentinela() {
        // Act & Assert
        assertThat(ProyectoGradoDomain.VACIO.getEntregadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(ProyectoGradoDomain.VACIO.getFinalizadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(ProyectoGradoDomain.VACIO.estaCongelado()).isFalse();
    }

    @Test
    void debeReconstruirSinValidar_cuandoDatosInvalidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var proyecto = ProyectoGradoDomain.reconstruir(id, " ", null, null);

        // Assert
        assertThat(proyecto.getId()).isEqualTo(id);
        assertThat(proyecto.getNombre()).isEqualTo(" ");
        assertThat(proyecto.esVacio()).isFalse();
    }
}
