package com.arquisoft.fichas.domain.estadorevision;

import com.arquisoft.fichas.domain.estadorevision.exception.EstadoRevisionNoEncontradoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoRevisionTest {

    @Test
    void debeCoincidirIdConName_cuandoEnumEsConsultado() {
        assertThat(EstadoRevision.NUEVA.getId()).isEqualTo("NUEVA");
        assertThat(EstadoRevision.VISUALIZADA.getId()).isEqualTo("VISUALIZADA");
        assertThat(EstadoRevision.EN_PROGRESO.getId()).isEqualTo("EN_PROGRESO");
        assertThat(EstadoRevision.CORRECCION_DISPONIBLE.getId()).isEqualTo("CORRECCION_DISPONIBLE");
        assertThat(EstadoRevision.CERRADA.getId()).isEqualTo("CERRADA");
    }

    @Test
    void debeRetornarNombre_cuandoEnumEsConsultado() {
        assertThat(EstadoRevision.NUEVA.getNombre()).isEqualTo("Nueva");
        assertThat(EstadoRevision.VISUALIZADA.getNombre()).isEqualTo("Visualizada");
        assertThat(EstadoRevision.EN_PROGRESO.getNombre()).isEqualTo("En Progreso");
        assertThat(EstadoRevision.CORRECCION_DISPONIBLE.getNombre()).isEqualTo("Correccion Disponible");
        assertThat(EstadoRevision.CERRADA.getNombre()).isEqualTo("Cerrada");
    }

    @Test
    void debeRetornarElEstado_cuandoElIdCoincideConElCatalogo() {
        assertThat(EstadoRevision.desde("NUEVA")).isEqualTo(EstadoRevision.NUEVA);
        assertThat(EstadoRevision.desde("CERRADA")).isEqualTo(EstadoRevision.CERRADA);
    }

    @Test
    void debeLanzarExcepcion_cuandoElIdEsNuloEnBlancoODesconocido() {
        assertThatThrownBy(() -> EstadoRevision.desde(null))
                .isInstanceOf(EstadoRevisionNoEncontradoException.class);
        assertThatThrownBy(() -> EstadoRevision.desde(""))
                .isInstanceOf(EstadoRevisionNoEncontradoException.class);
        assertThatThrownBy(() -> EstadoRevision.desde("NO_EXISTE"))
                .isInstanceOf(EstadoRevisionNoEncontradoException.class);
    }

    @Test
    void debeReportarValidez_sinLanzar_cuandoSeConsultaConEsValido() {
        assertThat(EstadoRevision.esValido("EN_PROGRESO")).isTrue();
        assertThat(EstadoRevision.esValido("NO_EXISTE")).isFalse();
        assertThat(EstadoRevision.esValido(null)).isFalse();
    }
}
