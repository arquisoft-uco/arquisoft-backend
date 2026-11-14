package com.arquisoft.entregables.domain.asesorproyectogrado.rules.impl;

import com.arquisoft.entregables.domain.asesorproyectogrado.exception.AsesorNoAsignadoProyectoGradoException;
import com.arquisoft.entregables.domain.asesorproyectogrado.model.VigenciaAsesorProyectoGrado;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorAsignadoProyectoGradoRuleImplTest {

    private final AsesorAsignadoProyectoGradoRuleImpl rule = new AsesorAsignadoProyectoGradoRuleImpl();

    @Test
    void debePasar_cuandoElSolicitanteEsElAsesorVigente() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var vigencia = new VigenciaAsesorProyectoGrado(UtilUUID.generarNuevoUUID(), asesor, asesor);

        // Act & Assert
        assertThatCode(() -> rule.validar(vigencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoElSolicitanteEsOtroAsesor() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();
        var vigencia = new VigenciaAsesorProyectoGrado(proyecto, solicitante, UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(vigencia))
                .isInstanceOfSatisfying(AsesorNoAsignadoProyectoGradoException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(EntregablesCodes.AsesorProyectoGrado.NO_ASIGNADO);
                    assertThat(ex.getError().getMessage()).doesNotContain(proyecto.toString(), solicitante.toString());
                    assertThat(ex.getMessage()).contains(proyecto.toString(), solicitante.toString());
                });
    }

    @Test
    void debeLanzar_cuandoElProyectoNoTieneAsesorVigente() {
        // Arrange
        var solicitante = UtilUUID.generarNuevoUUID();
        var vigencia = new VigenciaAsesorProyectoGrado(
                UtilUUID.generarNuevoUUID(), solicitante, UtilUUID.obtenerUUIDPorDefecto());

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(vigencia)).isInstanceOf(AsesorNoAsignadoProyectoGradoException.class);
    }

    @Test
    void debeLanzar_cuandoSolicitanteYVigenteSonElUuidPorDefecto() {
        // Arrange
        var vigencia = new VigenciaAsesorProyectoGrado(
                UtilUUID.generarNuevoUUID(), UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto());

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(vigencia)).isInstanceOf(AsesorNoAsignadoProyectoGradoException.class);
    }
}
