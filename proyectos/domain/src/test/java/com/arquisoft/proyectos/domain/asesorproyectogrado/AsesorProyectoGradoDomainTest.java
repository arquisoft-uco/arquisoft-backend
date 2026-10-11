package com.arquisoft.proyectos.domain.asesorproyectogrado;

import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorProyectoGradoDomainTest {

    @Test
    void debeCrearConEstadoAsignadoYFechaActual_cuandoDatosValidos() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var antes = UtilFecha.generarInstanteActual();

        // Act
        var vinculo = AsesorProyectoGradoDomain.crear(asesor, proyectoGrado);

        // Assert
        assertThat(vinculo.getId()).isNotNull();
        assertThat(vinculo.getAsesor()).isEqualTo(asesor);
        assertThat(vinculo.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(vinculo.getEstadoAsesor()).isEqualTo(EstadoAsesor.ASIGNADO);
        assertThat(vinculo.getFechaAsignado()).isAfterOrEqualTo(antes);
    }

    @Test
    void debeAcumularFieldErrors_cuandoAsesorYProyectoSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> AsesorProyectoGradoDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                ProyectosCodes.AsesorProyectoGrado.ASESOR_ID_REQUERIDO,
                                ProyectosCodes.AsesorProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO));
    }

    @Test
    void debeCrearConEstado_cuandoDatosValidos() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var antes = UtilFecha.generarInstanteActual();

        // Act
        var vinculo = AsesorProyectoGradoDomain.crearConEstado(asesor, proyectoGrado, EstadoAsesor.FINALIZADO);

        // Assert
        assertThat(vinculo.getId()).isNotNull();
        assertThat(vinculo.getAsesor()).isEqualTo(asesor);
        assertThat(vinculo.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(vinculo.getEstadoAsesor()).isEqualTo(EstadoAsesor.FINALIZADO);
        assertThat(vinculo.getFechaAsignado()).isAfterOrEqualTo(antes);
    }

    @Test
    void debeAcumularFieldErrors_cuandoFaltanAsesorProyectoYEstado() {
        // Act & Assert
        assertThatThrownBy(() -> AsesorProyectoGradoDomain.crearConEstado(null, null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                ProyectosCodes.AsesorProyectoGrado.ASESOR_ID_REQUERIDO,
                                ProyectosCodes.AsesorProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO,
                                ProyectosCodes.AsesorProyectoGrado.ESTADO_ASESOR_REQUERIDO));
    }

    @Test
    void debeSerVacio_cuandoEsElCentinela() {
        // Arrange
        var vinculo = AsesorProyectoGradoDomain.crear(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThat(AsesorProyectoGradoDomain.VACIO.esVacio()).isTrue();
        assertThat(AsesorProyectoGradoDomain.VACIO.getEstadoAsesor()).isEqualTo(EstadoAsesor.VACIO);
        assertThat(vinculo.esVacio()).isFalse();
    }

    @Test
    void debeReconstruirSinValidar_cuandoReconstruirEsInvocado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var fecha = UtilFecha.generarInstanteActual();

        // Act
        var vinculo = AsesorProyectoGradoDomain.reconstruir(id, null, null, EstadoAsesor.FINALIZADO, fecha);

        // Assert
        assertThat(vinculo.getId()).isEqualTo(id);
        assertThat(vinculo.getAsesor()).isNull();
        assertThat(vinculo.getEstadoAsesor()).isEqualTo(EstadoAsesor.FINALIZADO);
        assertThat(vinculo.getFechaAsignado()).isEqualTo(fecha);
    }
}
