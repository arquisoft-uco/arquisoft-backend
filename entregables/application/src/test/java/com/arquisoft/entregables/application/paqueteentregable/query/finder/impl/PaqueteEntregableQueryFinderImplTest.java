package com.arquisoft.entregables.application.paqueteentregable.query.finder.impl;

import com.arquisoft.entregables.application.paqueteentregable.query.secondaryport.PaqueteEntregableAccesoQueryOutputPort;
import com.arquisoft.entregables.application.paqueteentregable.query.secondaryport.entity.PaqueteEntregableAccesoEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaqueteEntregableQueryFinderImplTest {

    @Mock
    private PaqueteEntregableAccesoQueryOutputPort paqueteEntregableAccesoQueryOutputPort;

    @InjectMocks
    private PaqueteEntregableQueryFinderImpl finder;

    @Test
    void debeMapearAlDomain_cuandoElPuertoDevuelveElPaquete() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var entity = new PaqueteEntregableAccesoEntity(
                UtilUUID.generarNuevoUUID(), entregable, "bucket", "paquetes/x.zip", "p.zip");
        when(paqueteEntregableAccesoQueryOutputPort.obtenerPorEntregable(entregable)).thenReturn(Optional.of(entity));

        // Act
        var paquete = finder.obtener(entregable);

        // Assert
        assertThat(paquete.esVacio()).isFalse();
        assertThat(paquete.getBucket()).isEqualTo("bucket");
        assertThat(paquete.getClaveObjeto()).isEqualTo("paquetes/x.zip");
        assertThat(paquete.getNombreArchivo()).isEqualTo("p.zip");
    }

    @Test
    void debeDevolverVacio_cuandoElPuertoNoEncuentra() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        when(paqueteEntregableAccesoQueryOutputPort.obtenerPorEntregable(entregable)).thenReturn(Optional.empty());

        // Act
        var paquete = finder.obtener(entregable);

        // Assert
        assertThat(paquete.esVacio()).isTrue();
    }
}
