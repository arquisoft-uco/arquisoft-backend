package com.arquisoft.entregables.application.entregableproyectogrado.query.finder.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.query.secondaryport.EntregableProyectoGradoAccesoQueryOutputPort;
import com.arquisoft.entregables.application.entregableproyectogrado.query.secondaryport.entity.EntregableProyectoGradoAccesoEntity;
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
class EntregableProyectoGradoAccesoQueryFinderImplTest {

    @Mock
    private EntregableProyectoGradoAccesoQueryOutputPort entregableProyectoGradoAccesoQueryOutputPort;

    @InjectMocks
    private EntregableProyectoGradoAccesoQueryFinderImpl finder;

    @Test
    void debeRetornarElDomainReconstruido_cuandoElEntregableExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var entity = new EntregableProyectoGradoAccesoEntity(id, proyecto, 3, asesor, true);
        when(entregableProyectoGradoAccesoQueryOutputPort.obtenerAccesoPorId(id)).thenReturn(Optional.of(entity));

        // Act
        var entregable = finder.obtener(id);

        // Assert
        assertThat(entregable.esVacio()).isFalse();
        assertThat(entregable.getId()).isEqualTo(id);
        assertThat(entregable.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(entregable.getVersion()).isEqualTo(3);
        assertThat(UtilUUID.esPorDefecto(entregable.getAsesor())).isTrue();
        assertThat(entregable.isProyectoCancelado()).isTrue();
    }

    @Test
    void debeRetornarVacio_cuandoElEntregableNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(entregableProyectoGradoAccesoQueryOutputPort.obtenerAccesoPorId(id)).thenReturn(Optional.empty());

        // Act
        var entregable = finder.obtener(id);

        // Assert
        assertThat(entregable.esVacio()).isTrue();
    }
}
