package com.arquisoft.entregables.application.estadoentregableproyectogrado.query.validator.impl;

import com.arquisoft.entregables.domain.asesorproyectogrado.exception.AsesorNoAsignadoProyectoGradoException;
import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.exception.EntregableProyectoGradoNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEstadosEntregableProyectoGradoValidatorImplTest {

    private final ConsultarEstadosEntregableProyectoGradoValidatorImpl validator =
            new ConsultarEstadosEntregableProyectoGradoValidatorImpl();

    private static EntregableProyectoGradoDomain existente() {
        return EntregableProyectoGradoDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 1,
                UtilUUID.generarNuevoUUID());
    }

    @Test
    void debePasar_cuandoElAsesorEsElVigenteDelProyecto() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(UtilUUID.generarNuevoUUID(), existente(), asesor, asesor))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoElEntregableNoExiste() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                UtilUUID.generarNuevoUUID(), EntregableProyectoGradoDomain.VACIO, asesor, asesor))
                .isInstanceOf(EntregableProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarAsesorNoAsignado_cuandoElEntregableExistePeroElProyectoNoTieneAsesorVigente() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(UtilUUID.generarNuevoUUID(), existente(),
                UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.generarNuevoUUID()))
                .isInstanceOf(AsesorNoAsignadoProyectoGradoException.class);
    }

    @Test
    void debeLanzarAsesorNoAsignadoConElProyectoDelEntregable_cuandoElAsesorNoEsElVigenteDelProyecto() {
        // Arrange
        var entregable = existente();
        var solicitante = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entregable.getId(), entregable,
                UtilUUID.generarNuevoUUID(), solicitante))
                .isInstanceOfSatisfying(AsesorNoAsignadoProyectoGradoException.class, ex ->
                        assertThat(ex.getMessage()).contains(
                                entregable.getProyectoGrado().toString(), solicitante.toString()));
    }
}
