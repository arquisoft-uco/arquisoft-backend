package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideRegistroModificacionUsuariosException;
import com.arquisoft.solicitudes.domain.solicitud.model.TipoSolicitudConcordante;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import com.arquisoft.shared.util.UtilUUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitudEsRegistroModificacionUsuariosRuleImplTest {

    private final SolicitudEsRegistroModificacionUsuariosRuleImpl regla = new SolicitudEsRegistroModificacionUsuariosRuleImpl();

    private static final String ESPERADO = TipoSolicitud.REGISTRO_Y_MODIFICACION_DE_USUARIOS.getId();

    @Test
    void noDebeLanzar_cuandoElTipoCoincide() {
        // Act & Assert
        assertThatCode(() -> regla.validar(
                new TipoSolicitudConcordante(UtilUUID.generarNuevoUUID(), ESPERADO, ESPERADO)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSolicitudTipoNoCoincideRegistroModificacionUsuarios_cuandoElTipoEsOtro() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new TipoSolicitudConcordante(
                solicitud, TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId(), ESPERADO)))
                .isInstanceOf(SolicitudTipoNoCoincideRegistroModificacionUsuariosException.class);
    }
}
