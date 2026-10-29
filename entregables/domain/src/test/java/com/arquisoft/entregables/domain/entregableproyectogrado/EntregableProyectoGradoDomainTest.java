package com.arquisoft.entregables.domain.entregableproyectogrado;

import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntregableProyectoGradoDomainTest {

    @Test
    void debeCrearConVersionUnoYEstadoInicial_cuandoNoHayEntregablesPrevios() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var entregable = EntregableProyectoGradoDomain.crear(proyecto, 0, asesor);

        // Assert
        assertThat(entregable.getId()).isNotNull();
        assertThat(entregable.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(entregable.getAsesor()).isEqualTo(asesor);
        assertThat(entregable.getVersion()).isEqualTo(1);
        assertThat(entregable.getEstadoEntregable()).isEqualTo(EstadoEntregable.DISPONIBLE_EVALUACION);
    }

    @Test
    void debeCrearConVersionSiguiente_cuandoHayEntregablesPrevios() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var entregable = EntregableProyectoGradoDomain.crear(proyecto, 4, asesor);

        // Assert
        assertThat(entregable.getVersion()).isEqualTo(5);
    }

    @Test
    void debeAcumularFieldErrors_cuandoFaltanProyectoAsesorYLaUltimaVersionEsNegativa() {
        // Act & Assert
        assertThatThrownBy(() -> EntregableProyectoGradoDomain.crear(null, -1, null))
                .isInstanceOfSatisfying(DomainValidationException.class, ex -> assertThat(ex.getValidationResult().getErrores())
                        .extracting("codigoError")
                        .containsExactlyInAnyOrder(
                                EntregablesCodes.EntregableProyectoGrado.PROYECTO_GRADO_REQUERIDO,
                                EntregablesCodes.EntregableProyectoGrado.ULTIMA_VERSION_INVALIDA,
                                EntregablesCodes.EntregableProyectoGrado.ASESOR_REQUERIDO));
    }

    @Test
    void debeReconstruirConTodosLosCamposSinRevalidar_cuandoSeLeeDelPuerto() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var entregable = EntregableProyectoGradoDomain.reconstruir(id, proyecto, 3, asesor, EstadoEntregable.APROBADO);

        // Assert
        assertThat(entregable.getId()).isEqualTo(id);
        assertThat(entregable.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(entregable.getVersion()).isEqualTo(3);
        assertThat(entregable.getAsesor()).isEqualTo(asesor);
        assertThat(entregable.getEstadoEntregable()).isEqualTo(EstadoEntregable.APROBADO);
        assertThat(entregable.esVacio()).isFalse();
    }

    @Test
    void debeSerVacio_cuandoEsElCentinela() {
        // Act & Assert
        assertThat(EntregableProyectoGradoDomain.VACIO.esVacio()).isTrue();
        assertThat(EntregableProyectoGradoDomain.VACIO.getEstadoEntregable()).isEqualTo(EstadoEntregable.VACIO);
        assertThat(UtilUUID.esPorDefecto(EntregableProyectoGradoDomain.VACIO.getId())).isTrue();
    }
}
