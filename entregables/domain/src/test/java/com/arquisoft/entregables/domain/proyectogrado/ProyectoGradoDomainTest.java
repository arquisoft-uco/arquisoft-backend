package com.arquisoft.entregables.domain.proyectogrado;

import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProyectoGradoDomainTest {

    @Test
    void debeCrearConNombreTalCual_cuandoDatosValidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var proyecto = ProyectoGradoDomain.crear(id, "Sistema de gestion");

        // Assert
        assertThat(proyecto.getId()).isEqualTo(id);
        assertThat(proyecto.getNombre()).isEqualTo("Sistema de gestion");
        assertThat(proyecto.getEstadoProyecto()).isEqualTo(EstadoProyecto.EN_PROCESO);
        assertThat(proyecto.esVacio()).isFalse();
    }

    @Test
    void debeReconstruirSinValidarYExponerElVacio_cuandoLlegaDePersistencia() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var proyecto = ProyectoGradoDomain.reconstruir(id, null, EstadoProyecto.FINALIZADO);
        var vacio = ProyectoGradoDomain.VACIO;

        // Assert
        assertThat(proyecto.getId()).isEqualTo(id);
        assertThat(proyecto.getEstadoProyecto()).isEqualTo(EstadoProyecto.FINALIZADO);
        assertThat(proyecto.esVacio()).isFalse();
        assertThat(vacio.esVacio()).isTrue();
        assertThat(UtilUUID.esPorDefecto(vacio.getId())).isTrue();
        assertThat(vacio.getEstadoProyecto()).isEqualTo(EstadoProyecto.VACIO);
    }

    @Test
    void debeAcumularErrores_cuandoIdNuloYNombreEnBlanco() {
        // Act & Assert
        assertThatThrownBy(() -> ProyectoGradoDomain.crear(null, " "))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.campo())
                        .containsExactlyInAnyOrder(
                                EntregablesFields.ProyectoGrado.ID, EntregablesFields.ProyectoGrado.NOMBRE));
    }

    @Test
    void debeCrear_cuandoElNombreEsMuyLargo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var nombre = "a".repeat(500);

        // Act
        var proyecto = ProyectoGradoDomain.crear(id, nombre);

        // Assert
        assertThat(proyecto.getNombre()).isEqualTo(nombre);
    }
}
