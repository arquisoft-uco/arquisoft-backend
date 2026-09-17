package com.arquisoft.fichas.domain.estadoobservacionrevision;

import com.arquisoft.fichas.domain.estadoobservacionrevision.exception.EstadoObservacionRevisionNoEncontradoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoObservacionRevisionTest {

    @Test
    void debeCoincidirIdConName_cuandoEnumEsConsultado() {
        assertThat(EstadoObservacionRevision.PENDIENTE.getId()).isEqualTo("PENDIENTE");
        assertThat(EstadoObservacionRevision.EN_PROGRESO.getId()).isEqualTo("EN_PROGRESO");
        assertThat(EstadoObservacionRevision.CERRADO.getId()).isEqualTo("CERRADO");
    }

    @Test
    void debeRetornarNombre_cuandoEnumEsConsultado() {
        assertThat(EstadoObservacionRevision.PENDIENTE.getNombre()).isEqualTo("Pendiente");
        assertThat(EstadoObservacionRevision.EN_PROGRESO.getNombre()).isEqualTo("En Progreso");
        assertThat(EstadoObservacionRevision.CERRADO.getNombre()).isEqualTo("Cerrado");
    }

    @Test
    void debeRetornarElEstado_cuandoElIdCoincideConElCatalogo() {
        // El id real del MER es CERRADO (concuerda con "estado"), no CERRADA
        assertThat(EstadoObservacionRevision.desde("PENDIENTE")).isEqualTo(EstadoObservacionRevision.PENDIENTE);
        assertThat(EstadoObservacionRevision.desde("CERRADO")).isEqualTo(EstadoObservacionRevision.CERRADO);
    }

    @Test
    void debeLanzarExcepcion_cuandoElIdEsNuloEnBlancoODesconocido() {
        assertThatThrownBy(() -> EstadoObservacionRevision.desde(null))
                .isInstanceOf(EstadoObservacionRevisionNoEncontradoException.class);
        assertThatThrownBy(() -> EstadoObservacionRevision.desde(""))
                .isInstanceOf(EstadoObservacionRevisionNoEncontradoException.class);
        // "CERRADA" (género de EstadoRevision) no es un id válido de este catálogo
        assertThatThrownBy(() -> EstadoObservacionRevision.desde("CERRADA"))
                .isInstanceOf(EstadoObservacionRevisionNoEncontradoException.class);
    }

    @Test
    void debeReportarValidez_sinLanzar_cuandoSeConsultaConEsValido() {
        assertThat(EstadoObservacionRevision.esValido("EN_PROGRESO")).isTrue();
        assertThat(EstadoObservacionRevision.esValido("CERRADA")).isFalse();
        assertThat(EstadoObservacionRevision.esValido(null)).isFalse();
    }
}
