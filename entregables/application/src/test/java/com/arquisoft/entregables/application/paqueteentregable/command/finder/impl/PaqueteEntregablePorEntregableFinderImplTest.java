package com.arquisoft.entregables.application.paqueteentregable.command.finder.impl;

import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.PaqueteEntregableOutputPort;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.entity.PaqueteEntregableEntity;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaqueteEntregablePorEntregableFinderImplTest {

    @Mock
    private PaqueteEntregableOutputPort paqueteEntregableOutputPort;

    @InjectMocks
    private PaqueteEntregablePorEntregableFinderImpl finder;

    @Test
    void debeDevolverElPaqueteMapeado_cuandoElEntregableTienePaquete() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var entity = new PaqueteEntregableEntity(UtilUUID.generarNuevoUUID(), entregable, "entregables",
                "paquetes/a.zip", "a.zip", 10L, Instant.parse("2026-11-19T10:00:00Z"));
        when(paqueteEntregableOutputPort.obtenerPorEntregable(entregable)).thenReturn(Optional.of(entity));

        // Act
        var paquete = finder.obtener(entregable);

        // Assert
        assertThat(paquete.esVacio()).isFalse();
        assertThat(paquete.getId()).isEqualTo(entity.id());
        assertThat(paquete.getBucket()).isEqualTo("entregables");
        assertThat(paquete.getClaveObjeto()).isEqualTo("paquetes/a.zip");
        assertThat(paquete.getNombreArchivo()).isEqualTo("a.zip");
    }

    @Test
    void debeDevolverVacio_cuandoElEntregableNoTienePaquete() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        when(paqueteEntregableOutputPort.obtenerPorEntregable(entregable)).thenReturn(Optional.empty());

        // Act
        var paquete = finder.obtener(entregable);

        // Assert
        assertThat(paquete).isSameAs(PaqueteEntregableDomain.VACIO);
    }
}
