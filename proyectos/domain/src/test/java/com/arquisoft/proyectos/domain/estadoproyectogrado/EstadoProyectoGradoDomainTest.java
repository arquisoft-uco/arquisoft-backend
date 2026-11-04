package com.arquisoft.proyectos.domain.estadoproyectogrado;

import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoProyectoGradoDomainTest {

    @Test
    void debeCrearEnProcesoConIdYFecha_cuandoElProyectoEsValido() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var antes = UtilFecha.generarInstanteActual();

        // Act
        var estado = EstadoProyectoGradoDomain.crear(proyectoGrado);

        // Assert
        assertThat(estado.getId()).isNotNull();
        assertThat(UtilUUID.esPorDefecto(estado.getId())).isFalse();
        assertThat(estado.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(estado.getEstadoProyecto()).isEqualTo(EstadoProyecto.EN_PROCESO);
        assertThat(estado.getFechaActualizacion()).isBetween(antes, UtilFecha.generarInstanteActual());
        assertThat(estado.esVacio()).isFalse();
    }

    @Test
    void debeAcumularFieldError_cuandoElProyectoGradoEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> EstadoProyectoGradoDomain.crear(null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).singleElement().satisfies(error -> {
                        assertThat(error.campo()).isEqualTo(ProyectosFields.EstadoProyectoGrado.PROYECTO_GRADO);
                        assertThat(error.codigoError())
                                .isEqualTo(ProyectosCodes.EstadoProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO);
                    });
                });
    }

    @Test
    void debeReconstruirSinValidar_cuandoLlegaDePersistencia() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var fecha = Instant.parse("2026-10-01T10:00:00Z");

        // Act
        var estado = EstadoProyectoGradoDomain.reconstruir(id, null, EstadoProyecto.FINALIZADO, fecha);

        // Assert
        assertThat(estado.getId()).isEqualTo(id);
        assertThat(estado.getProyectoGrado()).isNull();
        assertThat(estado.getEstadoProyecto()).isEqualTo(EstadoProyecto.FINALIZADO);
        assertThat(estado.getFechaActualizacion()).isEqualTo(fecha);
        assertThat(estado.esVacio()).isFalse();
    }

    @Test
    void debeSerVacioConValoresPorDefecto_cuandoEsElCentinela() {
        // Act
        var vacio = EstadoProyectoGradoDomain.VACIO;

        // Assert
        assertThat(vacio.esVacio()).isTrue();
        assertThat(UtilUUID.esPorDefecto(vacio.getId())).isTrue();
        assertThat(UtilUUID.esPorDefecto(vacio.getProyectoGrado())).isTrue();
        assertThat(vacio.getEstadoProyecto()).isEqualTo(EstadoProyecto.VACIO);
        assertThat(vacio.getFechaActualizacion()).isEqualTo(UtilFecha.VACIO);
    }
}
