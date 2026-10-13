package com.arquisoft.fichas.domain.observacionitem.model;

import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContextoObservacionItemTest {

    @Test
    void debeDistinguirElCentinelaPorIdentidad_cuandoPreguntaEsVacio() {
        // Arrange
        var real = new ContextoObservacionItem(
                UtilUUID.generarNuevoUUID(), EstadoRevision.NUEVA, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var copiaDelCentinela = new ContextoObservacionItem(
                ContextoObservacionItem.VACIO.revisionItem(), ContextoObservacionItem.VACIO.estadoRevision(),
                ContextoObservacionItem.VACIO.fichaPerfil(), ContextoObservacionItem.VACIO.asesorFicha());

        // Act & Assert — la ausencia es el singleton, no un contexto con los mismos valores
        assertThat(ContextoObservacionItem.VACIO.esVacio()).isTrue();
        assertThat(real.esVacio()).isFalse();
        assertThat(copiaDelCentinela.esVacio()).isFalse();
    }
}
