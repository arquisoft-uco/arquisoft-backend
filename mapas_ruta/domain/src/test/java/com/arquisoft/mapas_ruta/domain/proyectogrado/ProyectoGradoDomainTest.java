package com.arquisoft.mapas_ruta.domain.proyectogrado;

import com.arquisoft.mapas_ruta.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProyectoGradoDomainTest {

    @Test
    void debeReconstruirConTodosLosCampos_cuandoVienenDeLaBase() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-01T10:00:00Z");

        // Act
        var proyecto = ProyectoGradoDomain.reconstruir(
                id, EstadoProyectoGrado.EN_PROCESO, coordinador, fichaPerfil, "Titulo", ocurridoEn);

        // Assert
        assertThat(proyecto.getId()).isEqualTo(id);
        assertThat(proyecto.getEstadoProyectoGrado()).isEqualTo(EstadoProyectoGrado.EN_PROCESO);
        assertThat(proyecto.getCoordinador()).isEqualTo(coordinador);
        assertThat(proyecto.getFichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(proyecto.getTituloProyecto()).isEqualTo("Titulo");
        assertThat(proyecto.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(proyecto.esVacio()).isFalse();
    }

    @Test
    void debeIdentificarElCentinela_cuandoSeConsultaEsVacio() {
        // Act & Assert
        assertThat(ProyectoGradoDomain.VACIO.esVacio()).isTrue();
        assertThat(ProyectoGradoDomain.VACIO.getEstadoProyectoGrado()).isEqualTo(EstadoProyectoGrado.VACIO);
        assertThat(UtilUUID.esPorDefecto(ProyectoGradoDomain.VACIO.getId())).isTrue();
    }

    @Test
    void debeCrearEnProceso_cuandoDatosValidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-01T10:00:00Z");

        // Act
        var proyecto = ProyectoGradoDomain.crear(id, coordinador, fichaPerfil, "Titulo", ocurridoEn);

        // Assert
        assertThat(proyecto.getId()).isEqualTo(id);
        assertThat(proyecto.getCoordinador()).isEqualTo(coordinador);
        assertThat(proyecto.getFichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(proyecto.getTituloProyecto()).isEqualTo("Titulo");
        assertThat(proyecto.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(proyecto.getEstadoProyectoGrado()).isEqualTo(EstadoProyectoGrado.EN_PROCESO);
    }

    @Test
    void debeAcumularFieldErrors_cuandoFaltanDatos() {
        // Act & Assert
        assertThatThrownBy(() -> ProyectoGradoDomain.crear(null, null, null, " ", null))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        MapasRutaCodes.ProyectoGrado.ID_REQUERIDO,
                                        MapasRutaCodes.ProyectoGrado.COORDINADOR_REQUERIDO,
                                        MapasRutaCodes.ProyectoGrado.FICHA_PERFIL_REQUERIDA,
                                        MapasRutaCodes.ProyectoGrado.TITULO_PROYECTO_REQUERIDO,
                                        MapasRutaCodes.ProyectoGrado.OCURRIDO_EN_REQUERIDO));
    }

    @Test
    void debeReconstruirConOcurridoEnPorDefecto_cuandoEsNulo() {
        // Act
        var proyecto = ProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), EstadoProyectoGrado.ATRASADO, UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "Titulo", null);

        // Assert
        assertThat(proyecto.getOcurridoEn()).isEqualTo(UtilFecha.VACIO);
    }
}
