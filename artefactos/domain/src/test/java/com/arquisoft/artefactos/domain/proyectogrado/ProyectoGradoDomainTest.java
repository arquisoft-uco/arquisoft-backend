package com.arquisoft.artefactos.domain.proyectogrado;

import com.arquisoft.artefactos.domain.proyectogrado.model.EstadoProyecto;
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
        assertThat(ProyectoGradoDomain.VACIO.getEntregadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(ProyectoGradoDomain.VACIO.getOcurridoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(ProyectoGradoDomain.VACIO.getEstadoProyecto()).isEqualTo(EstadoProyecto.VACIO);
        assertThat(ProyectoGradoDomain.VACIO.estaCongelado()).isFalse();
    }

    @Test
    void debeCrearEnProcesoSinFechas_cuandoDatosValidos() {
        // Act
        var proyecto = ProyectoGradoDomain.crear(UtilUUID.generarNuevoUUID(), "Proyecto");

        // Assert
        assertThat(proyecto.getEntregadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(proyecto.getOcurridoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(proyecto.getEstadoProyecto()).isEqualTo(EstadoProyecto.EN_PROCESO);
        assertThat(proyecto.estaEntregado()).isFalse();
        assertThat(proyecto.estaFinalizado()).isFalse();
        assertThat(proyecto.estaCancelado()).isFalse();
        assertThat(proyecto.estaCongelado()).isFalse();
    }

    @Test
    void debeEstarCongelado_cuandoEntregado() {
        // Arrange
        var entregadoEn = Instant.parse("2026-11-01T10:00:00Z");

        // Act
        var proyecto = ProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "P", entregadoEn, EstadoProyecto.EN_PROCESO, null);

        // Assert
        assertThat(proyecto.getEntregadoEn()).isEqualTo(entregadoEn);
        assertThat(proyecto.estaEntregado()).isTrue();
        assertThat(proyecto.estaConcluido()).isFalse();
        assertThat(proyecto.estaCongelado()).isTrue();
    }

    @Test
    void debeEstarFinalizadoYCongelado_cuandoElEstadoEsFinalizado() {
        // Arrange
        var ocurridoEn = Instant.parse("2026-11-02T10:00:00Z");

        // Act
        var proyecto = ProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "P", UtilFecha.VACIO, EstadoProyecto.FINALIZADO, ocurridoEn);

        // Assert
        assertThat(proyecto.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(proyecto.estaFinalizado()).isTrue();
        assertThat(proyecto.estaCancelado()).isFalse();
        assertThat(proyecto.estaConcluido()).isTrue();
        assertThat(proyecto.estaCongelado()).isTrue();
    }

    @Test
    void debeEstarCanceladoYCongelado_cuandoElEstadoEsCancelado() {
        // Act
        var proyecto = ProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "P", UtilFecha.VACIO, EstadoProyecto.CANCELADO,
                Instant.parse("2026-11-03T10:00:00Z"));

        // Assert
        assertThat(proyecto.estaCancelado()).isTrue();
        assertThat(proyecto.estaFinalizado()).isFalse();
        assertThat(proyecto.estaConcluido()).isTrue();
        assertThat(proyecto.estaCongelado()).isTrue();
    }

    @Test
    void debeNoCongelarNiConcluir_cuandoElEstadoEsAtrasado() {
        // Act
        var proyecto = ProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "P", UtilFecha.VACIO, EstadoProyecto.ATRASADO,
                Instant.parse("2026-11-03T10:00:00Z"));

        // Assert
        assertThat(proyecto.estaFinalizado()).isFalse();
        assertThat(proyecto.estaCancelado()).isFalse();
        assertThat(proyecto.estaConcluido()).isFalse();
        assertThat(proyecto.estaCongelado()).isFalse();
    }

    @Test
    void debeReconstruirSinValidarYConFechasPorDefecto_cuandoDatosInvalidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var proyecto = ProyectoGradoDomain.reconstruir(id, " ", null, EstadoProyecto.EN_PROCESO, null);

        // Assert
        assertThat(proyecto.getId()).isEqualTo(id);
        assertThat(proyecto.getNombre()).isEqualTo(" ");
        assertThat(proyecto.getEntregadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(proyecto.getOcurridoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(proyecto.esVacio()).isFalse();
    }
}
