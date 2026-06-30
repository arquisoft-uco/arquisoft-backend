package com.arquisoft.fichas.domain.tipoitem;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TipoItemTest {

    @Test
    void debeCoincidirIdConName_cuandoEnumEsConsultado() {
        // Arrange / Act / Assert — ADR-012: id = this.name()
        assertThat(TipoItem.OBJETIVO_GENERAL.getId()).isEqualTo("OBJETIVO_GENERAL");
        assertThat(TipoItem.OBJETIVO_ESPECIFICO.getId()).isEqualTo("OBJETIVO_ESPECIFICO");
        assertThat(TipoItem.ESTADO_DEL_ARTE.getId()).isEqualTo("ESTADO_DEL_ARTE");
        assertThat(TipoItem.ANTECEDENTES.getId()).isEqualTo("ANTECEDENTES");
        assertThat(TipoItem.JUSTIFICACION.getId()).isEqualTo("JUSTIFICACION");
        assertThat(TipoItem.REFERENCIAS.getId()).isEqualTo("REFERENCIAS");
    }

    @Test
    void debeRetornarNombre_cuandoEnumEsConsultado() {
        // Arrange / Act / Assert
        assertThat(TipoItem.OBJETIVO_GENERAL.getNombre()).isEqualTo("Objetivo General");
        assertThat(TipoItem.OBJETIVO_ESPECIFICO.getNombre()).isEqualTo("Objetivo Especifico");
        assertThat(TipoItem.ESTADO_DEL_ARTE.getNombre()).isEqualTo("Estado Del Arte");
        assertThat(TipoItem.ANTECEDENTES.getNombre()).isEqualTo("Antecedentes");
        assertThat(TipoItem.JUSTIFICACION.getNombre()).isEqualTo("Justificacion");
        assertThat(TipoItem.REFERENCIAS.getNombre()).isEqualTo("Referencias");
    }
}
