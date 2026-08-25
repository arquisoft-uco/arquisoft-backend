package com.arquisoft.fichas.domain.tipoitem;

import com.arquisoft.fichas.domain.tipoitem.exception.TipoItemNoEncontradoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TipoItemTest {

    @Test
    void debeCoincidirIdConName_cuandoEnumEsConsultado() {
        assertThat(TipoItem.OBJETIVO_GENERAL.getId()).isEqualTo("OBJETIVO_GENERAL");
        assertThat(TipoItem.OBJETIVO_ESPECIFICO.getId()).isEqualTo("OBJETIVO_ESPECIFICO");
        assertThat(TipoItem.ESTADO_DEL_ARTE.getId()).isEqualTo("ESTADO_DEL_ARTE");
        assertThat(TipoItem.ANTECEDENTES.getId()).isEqualTo("ANTECEDENTES");
        assertThat(TipoItem.JUSTIFICACION.getId()).isEqualTo("JUSTIFICACION");
        assertThat(TipoItem.REFERENCIAS.getId()).isEqualTo("REFERENCIAS");
    }

    @Test
    void debeRetornarNombre_cuandoEnumEsConsultado() {
        assertThat(TipoItem.OBJETIVO_GENERAL.getNombre()).isEqualTo("Objetivo General");
        assertThat(TipoItem.OBJETIVO_ESPECIFICO.getNombre()).isEqualTo("Objetivo Especifico");
        assertThat(TipoItem.ESTADO_DEL_ARTE.getNombre()).isEqualTo("Estado Del Arte");
        assertThat(TipoItem.ANTECEDENTES.getNombre()).isEqualTo("Antecedentes");
        assertThat(TipoItem.JUSTIFICACION.getNombre()).isEqualTo("Justificacion");
        assertThat(TipoItem.REFERENCIAS.getNombre()).isEqualTo("Referencias");
    }

    @Test
    void debeRetornarElTipo_cuandoElIdCoincideConElCatalogo() {
        assertThat(TipoItem.desde("OBJETIVO_GENERAL")).isEqualTo(TipoItem.OBJETIVO_GENERAL);
    }

    @Test
    void debeLanzarExcepcion_cuandoElIdEsNuloEnBlancoODesconocido() {
        assertThatThrownBy(() -> TipoItem.desde(null))
                .isInstanceOf(TipoItemNoEncontradoException.class);
        assertThatThrownBy(() -> TipoItem.desde(""))
                .isInstanceOf(TipoItemNoEncontradoException.class);
        assertThatThrownBy(() -> TipoItem.desde("NO_EXISTE"))
                .isInstanceOf(TipoItemNoEncontradoException.class);
    }

    @Test
    void debeReportarValidez_sinLanzar_cuandoSeConsultaConEsValido() {
        assertThat(TipoItem.esValido("REFERENCIAS")).isTrue();
        assertThat(TipoItem.esValido("NO_EXISTE")).isFalse();
        assertThat(TipoItem.esValido(null)).isFalse();
    }
}
