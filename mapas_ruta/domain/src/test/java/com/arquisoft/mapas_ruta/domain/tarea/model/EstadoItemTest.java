package com.arquisoft.mapas_ruta.domain.tarea.model;

import com.arquisoft.mapas_ruta.domain.tarea.exception.EstadoItemNoEncontradoException;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

    @ParameterizedTest
    @CsvSource({"NUEVA,EN_PROGRESO", "NUEVA,FINALIZADA", "EN_PROGRESO,FINALIZADA"})
    void debePermitir_cuandoTransicionEsHaciaAdelante(EstadoItem origen, EstadoItem destino) {
        // Act & Assert
        assertThat(origen.puedePasarA(destino)).isTrue();
    }

    @ParameterizedTest
    @CsvSource({"EN_PROGRESO,NUEVA", "FINALIZADA,NUEVA", "FINALIZADA,EN_PROGRESO", "NUEVA,NUEVA",
            "EN_PROGRESO,EN_PROGRESO", "FINALIZADA,FINALIZADA", "VACIO,NUEVA", "NUEVA,VACIO"})
    void debeRechazar_cuandoVuelveSaleDeFinalizadaORepite(EstadoItem origen, EstadoItem destino) {
        // Act & Assert
        assertThat(origen.puedePasarA(destino)).isFalse();
    }

    @Test
    void debePermitirRemocionSoloEnNuevaYEnProgreso_yNegarEnFinalizadaOVacio() {
        // Act & Assert
        assertThat(EstadoItem.NUEVA.permiteRemocion()).isTrue();
        assertThat(EstadoItem.EN_PROGRESO.permiteRemocion()).isTrue();
        assertThat(EstadoItem.FINALIZADA.permiteRemocion()).isFalse();
        assertThat(EstadoItem.VACIO.permiteRemocion()).isFalse();
    }

    @Test
    void debeValidarElId_cuandoSeConsultaEsValido() {
        // Act & Assert
        assertThat(EstadoItem.esValido("EN_PROGRESO")).isTrue();
        assertThat(EstadoItem.esValido("VACIO")).isFalse();
        assertThat(EstadoItem.esValido("INEXISTENTE")).isFalse();
    }
}
