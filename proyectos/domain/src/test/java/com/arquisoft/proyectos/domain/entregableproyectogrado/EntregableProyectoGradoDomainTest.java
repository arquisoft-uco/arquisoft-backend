package com.arquisoft.proyectos.domain.entregableproyectogrado;

import com.arquisoft.proyectos.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntregableProyectoGradoDomainTest {

    @Test
    void debeCrearConEstadoConvertido_cuandoDatosValidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-01T10:00:00Z");

        // Act
        var entregable = EntregableProyectoGradoDomain.crear(id, proyectoGrado, 2, "CERRADO", ocurridoEn);

        // Assert
        assertThat(entregable.getId()).isEqualTo(id);
        assertThat(entregable.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(entregable.getVersion()).isEqualTo(2);
        assertThat(entregable.getEstadoEntregable()).isEqualTo(EstadoEntregable.CERRADO);
        assertThat(entregable.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(entregable.esVacio()).isFalse();
    }

    @Test
    void debeCrearConRechazado_cuandoElEstadoEsRechazado() {
        // Act
        var entregable = EntregableProyectoGradoDomain.crear(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), 1, "RECHAZADO", Instant.parse("2026-11-01T10:00:00Z"));

        // Assert
        assertThat(entregable.getEstadoEntregable()).isEqualTo(EstadoEntregable.RECHAZADO);
    }

    @Test
    void debeAcumularErrores_cuandoFaltanDatosYElEstadoEsInvalido() {
        // Act & Assert
        assertThatThrownBy(() -> EntregableProyectoGradoDomain.crear(null, null, 0, " ", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                ProyectosCodes.EntregableProyectoGrado.ID_REQUERIDO,
                                ProyectosCodes.EntregableProyectoGrado.PROYECTO_GRADO_REQUERIDO,
                                ProyectosCodes.EntregableProyectoGrado.VERSION_INVALIDA,
                                ProyectosCodes.EntregableProyectoGrado.ESTADO_ENTREGABLE_REQUERIDO,
                                ProyectosCodes.EntregableProyectoGrado.OCURRIDO_EN_REQUERIDO));
        assertThatThrownBy(() -> EntregableProyectoGradoDomain.crear(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), 1, "INEXISTENTE", Instant.now()))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(ProyectosCodes.EntregableProyectoGrado.ESTADO_ENTREGABLE_INVALIDO)));
    }

    @Test
    void debeReconstruirSinValidarYExponerElVacio_cuandoLlegaDePersistencia() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var entregable = EntregableProyectoGradoDomain.reconstruir(id, null, 0, EstadoEntregable.CANCELADO, null);
        var vacio = EntregableProyectoGradoDomain.VACIO;

        // Assert
        assertThat(entregable.getId()).isEqualTo(id);
        assertThat(entregable.getProyectoGrado()).isNull();
        assertThat(entregable.esVacio()).isFalse();
        assertThat(vacio.esVacio()).isTrue();
        assertThat(UtilUUID.esPorDefecto(vacio.getId())).isTrue();
        assertThat(vacio.getEstadoEntregable()).isEqualTo(EstadoEntregable.VACIO);
        assertThat(vacio.getOcurridoEn()).isEqualTo(UtilFecha.VACIO);
    }
}
