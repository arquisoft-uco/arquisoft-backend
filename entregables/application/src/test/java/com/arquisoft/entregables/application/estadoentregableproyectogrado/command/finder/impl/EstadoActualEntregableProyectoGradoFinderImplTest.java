package com.arquisoft.entregables.application.estadoentregableproyectogrado.command.finder.impl;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.EstadoEntregableProyectoGradoOutputPort;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.entity.EstadoEntregableProyectoGradoEntity;
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
class EstadoActualEntregableProyectoGradoFinderImplTest {

    @Mock
    private EstadoEntregableProyectoGradoOutputPort estadoEntregableProyectoGradoOutputPort;

    @InjectMocks
    private EstadoActualEntregableProyectoGradoFinderImpl finder;

    @Test
    void debeRetornarDomain_cuandoHayEstado() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var fecha = UtilFecha.generarInstanteActual();
        var entity = new EstadoEntregableProyectoGradoEntity(UtilUUID.generarNuevoUUID(), entregable, "APROBADO", fecha);
        when(estadoEntregableProyectoGradoOutputPort.obtenerEstadoActual(entregable)).thenReturn(Optional.of(entity));

        // Act
        var resultado = finder.obtener(entregable);

        // Assert
        assertThat(resultado.getId()).isEqualTo(entity.id());
        assertThat(resultado.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(resultado.getEstadoEntregable()).isEqualTo(EstadoEntregable.APROBADO);
        assertThat(resultado.getFechaActualizacion()).isEqualTo(fecha);
    }

    @Test
    void debeRetornarVacio_cuandoNoHay() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        when(estadoEntregableProyectoGradoOutputPort.obtenerEstadoActual(entregable)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(entregable);

        // Assert
        assertThat(resultado).isSameAs(EstadoEntregableProyectoGradoDomain.VACIO);
    }
}
