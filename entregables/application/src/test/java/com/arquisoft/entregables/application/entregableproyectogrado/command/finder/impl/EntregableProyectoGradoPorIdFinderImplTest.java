package com.arquisoft.entregables.application.entregableproyectogrado.command.finder.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
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
class EntregableProyectoGradoPorIdFinderImplTest {

    @Mock
    private EntregableProyectoGradoOutputPort entregableProyectoGradoOutputPort;

    @InjectMocks
    private EntregableProyectoGradoPorIdFinderImpl finder;

    @Test
    void debeRetornarElDomainReconstruido_cuandoElEntregableExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entity = new EntregableProyectoGradoEntity(id, UtilUUID.generarNuevoUUID(), 2,
                UtilUUID.generarNuevoUUID(), "APROBADO");
        when(entregableProyectoGradoOutputPort.obtenerPorId(id)).thenReturn(Optional.of(entity));

        // Act
        var entregable = finder.obtener(id);

        // Assert
        assertThat(entregable.esVacio()).isFalse();
        assertThat(entregable.getId()).isEqualTo(id);
        assertThat(entregable.getEstadoEntregable()).isEqualTo(EstadoEntregable.APROBADO);
    }

    @Test
    void debeRetornarVacio_cuandoElEntregableNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(entregableProyectoGradoOutputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act
        var entregable = finder.obtener(id);

        // Assert
        assertThat(entregable.esVacio()).isTrue();
    }
}
