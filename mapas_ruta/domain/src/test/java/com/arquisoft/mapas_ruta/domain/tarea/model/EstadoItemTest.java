package com.arquisoft.mapas_ruta.domain.tarea.model;

import com.arquisoft.mapas_ruta.domain.tarea.exception.EstadoItemNoEncontradoException;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoItemTest {

    @Test
    void debeResolverCadaConstante_conSuId() {
        // Act & Assert
        assertThat(EstadoItem.desde("NUEVA")).isEqualTo(EstadoItem.NUEVA);
        assertThat(EstadoItem.desde("EN_PROGRESO").getNombre()).isEqualTo("En progreso");
        assertThat(EstadoItem.desde("FINALIZADA").getId()).isEqualTo("FINALIZADA");
        assertThat(EstadoItem.NUEVA.getNombre()).isEqualTo("Nueva");
    }

    @Test
    void debeLanzar_cuandoIdNoExisteOEsElCentinela() {
        // Act & Assert
        assertThatThrownBy(() -> EstadoItem.desde("INEXISTENTE"))
                .isInstanceOf(EstadoItemNoEncontradoException.class)
                .hasFieldOrPropertyWithValue("codigoError", MapasRutaCodes.Tarea.ESTADO_ITEM_NO_ENCONTRADO);
        assertThatThrownBy(() -> EstadoItem.desde("VACIO"))
                .isInstanceOf(EstadoItemNoEncontradoException.class);
    }

    @Test
    void debeValidarElId_cuandoSeConsultaEsValido() {
        // Act & Assert
        assertThat(EstadoItem.esValido("EN_PROGRESO")).isTrue();
        assertThat(EstadoItem.esValido("VACIO")).isFalse();
        assertThat(EstadoItem.esValido("INEXISTENTE")).isFalse();
    }
}
