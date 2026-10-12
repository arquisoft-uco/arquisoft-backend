package com.arquisoft.fichas.domain.revisionitem.model;

import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsesoriaRevisionItemTest {

    @Test
    void debeDistinguirElCentinelaDeUnDatoReal_cuandoPreguntaPorVacio() {
        // Arrange
        var real = new AsesoriaRevisionItem(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), EstadoRevision.NUEVA);

        // Act & Assert
        assertThat(AsesoriaRevisionItem.VACIO.esVacio()).isTrue();
        assertThat(AsesoriaRevisionItem.VACIO.fichaPerfil()).isEqualTo(UtilUUID.obtenerUUIDPorDefecto());
        assertThat(AsesoriaRevisionItem.VACIO.asesorFicha()).isEqualTo(UtilUUID.obtenerUUIDPorDefecto());
        assertThat(AsesoriaRevisionItem.VACIO.estadoRevision()).isSameAs(EstadoRevision.VACIO);
        assertThat(real.esVacio()).isFalse();
    }
}
