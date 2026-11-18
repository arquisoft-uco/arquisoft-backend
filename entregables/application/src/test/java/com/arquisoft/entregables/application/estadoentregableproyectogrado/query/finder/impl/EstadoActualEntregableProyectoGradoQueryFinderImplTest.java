package com.arquisoft.entregables.application.estadoentregableproyectogrado.query.finder.impl;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.secondaryport.EstadoEntregableProyectoGradoAccesoQueryOutputPort;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.secondaryport.entity.EstadoEntregableProyectoGradoAccesoEntity;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.EstadoEntregableProyectoGradoDomain;
import com.arquisoft.shared.util.UtilFecha;
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
class EstadoActualEntregableProyectoGradoQueryFinderImplTest {

    @Mock
    private EstadoEntregableProyectoGradoAccesoQueryOutputPort estadoAccesoQueryOutputPort;

    @InjectMocks
    private EstadoActualEntregableProyectoGradoQueryFinderImpl finder;

    @Test
    void debeRetornarDomain_cuandoHayEstado() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var fecha = UtilFecha.generarInstanteActual();
        var entity = new EstadoEntregableProyectoGradoAccesoEntity(
                UtilUUID.generarNuevoUUID(), entregable, "CANCELADO", fecha);
        when(estadoAccesoQueryOutputPort.obtenerEstadoActual(entregable)).thenReturn(Optional.of(entity));

        // Act
        var resultado = finder.obtener(entregable);

        // Assert
        assertThat(resultado.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(resultado.getEstadoEntregable()).isEqualTo(EstadoEntregable.CANCELADO);
        assertThat(resultado.getFechaActualizacion()).isEqualTo(fecha);
    }

    @Test
    void debeRetornarVacio_cuandoNoHay() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        when(estadoAccesoQueryOutputPort.obtenerEstadoActual(entregable)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(entregable);

        // Assert
        assertThat(resultado).isSameAs(EstadoEntregableProyectoGradoDomain.VACIO);
    }
}
