package com.arquisoft.entregables.application.artefacto.query.validator.impl;

import com.arquisoft.entregables.domain.asesorproyectogrado.exception.AsesorNoAsignadoProyectoGradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarVistaPreviaPaqueteValidatorImplTest {

    private final ConsultarVistaPreviaPaqueteValidatorImpl validator = new ConsultarVistaPreviaPaqueteValidatorImpl();

    @Test
    void debePasar_cuandoElSolicitanteEsElAsesorVigenteDelProyecto() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(UtilUUID.generarNuevoUUID(), asesor, asesor))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarAsesorNoAsignado_cuandoElSolicitanteEsOtroOElProyectoNoTieneAsesor() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(proyecto, UtilUUID.generarNuevoUUID(), solicitante))
                .isInstanceOf(AsesorNoAsignadoProyectoGradoException.class);
        assertThatThrownBy(() -> validator.validar(proyecto, UtilUUID.obtenerUUIDPorDefecto(), solicitante))
                .isInstanceOf(AsesorNoAsignadoProyectoGradoException.class);
    }
}
