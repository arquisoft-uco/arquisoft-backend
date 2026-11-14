package com.arquisoft.entregables.domain.estadoentregableproyectogrado;

import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoEntregableProyectoGradoDomainTest {

    @Test
    void debeCrearEstadoInicial_cuandoEntregableValido() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();

        // Act
        var estado = EstadoEntregableProyectoGradoDomain.crear(entregable);

        // Assert
        assertThat(estado.getId()).isNotNull();
        assertThat(estado.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(estado.getEstadoEntregable()).isEqualTo(EstadoEntregable.NUEVO);
        assertThat(estado.getFechaActualizacion()).isNotNull();
        assertThat(estado.esVacio()).isFalse();
    }

    @Test
    void debeAcumularError_cuandoEntregableEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> EstadoEntregableProyectoGradoDomain.crear(null))
                .isInstanceOfSatisfying(DomainValidationException.class, ex -> assertThat(
                        ex.getValidationResult().tieneErroresDeCampo(
                                EntregablesFields.EstadoEntregableProyectoGrado.ENTREGABLE_PROYECTO_GRADO)).isTrue());
    }

    @Test
    void debeCrearConEstado_cuandoEstadoValido() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();

        // Act
        var estado = EstadoEntregableProyectoGradoDomain.crearConEstado(entregable, EstadoEntregable.CANCELADO);
        var otro = EstadoEntregableProyectoGradoDomain.crearConEstado(entregable, EstadoEntregable.CANCELADO);

        // Assert
        assertThat(estado.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(estado.getEstadoEntregable()).isEqualTo(EstadoEntregable.CANCELADO);
        assertThat(estado.getFechaActualizacion()).isNotNull();
        assertThat(otro.getId()).isNotEqualTo(estado.getId());
    }

    @Test
    void debeAcumularErrores_cuandoEntregableYEstadoSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> EstadoEntregableProyectoGradoDomain.crearConEstado(null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, ex -> {
                    var resultado = ex.getValidationResult();
                    assertThat(resultado.tieneErroresDeCampo(
                            EntregablesFields.EstadoEntregableProyectoGrado.ENTREGABLE_PROYECTO_GRADO)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(
                            EntregablesFields.EstadoEntregableProyectoGrado.ESTADO_ENTREGABLE)).isTrue();
                });
    }

    @Test
    void debeRechazar_cuandoEstadoEsVacio() {
        // Act & Assert
        assertThatThrownBy(() -> EstadoEntregableProyectoGradoDomain.crearConEstado(
                UtilUUID.generarNuevoUUID(), EstadoEntregable.VACIO))
                .isInstanceOfSatisfying(DomainValidationException.class, ex -> assertThat(
                        ex.getValidationResult().tieneErroresDeCampo(
                                EntregablesFields.EstadoEntregableProyectoGrado.ESTADO_ENTREGABLE)).isTrue());
    }

    @Test
    void debeReconstruir_cuandoSeIndicanTodosLosCampos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var fecha = UtilFecha.generarInstanteActual();

        // Act
        var estado = EstadoEntregableProyectoGradoDomain.reconstruir(id, entregable, EstadoEntregable.CERRADO, fecha);

        // Assert
        assertThat(estado.getId()).isEqualTo(id);
        assertThat(estado.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(estado.getEstadoEntregable()).isEqualTo(EstadoEntregable.CERRADO);
        assertThat(estado.getFechaActualizacion()).isEqualTo(fecha);
    }

    @Test
    void debeSerVacio_cuandoEsElCentinela() {
        // Act & Assert
        assertThat(EstadoEntregableProyectoGradoDomain.VACIO.esVacio()).isTrue();
        assertThat(EstadoEntregableProyectoGradoDomain.VACIO.getEstadoEntregable()).isEqualTo(EstadoEntregable.VACIO);
    }
}
