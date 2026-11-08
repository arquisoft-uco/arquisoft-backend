package com.arquisoft.entregables.application.proyectogrado.command.finder.impl;

import com.arquisoft.entregables.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.entregables.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;
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
class ProyectoGradoDeEntregableFinderImplTest {

    @Mock
    private ProyectoGradoOutputPort proyectoGradoOutputPort;

    @InjectMocks
    private ProyectoGradoDeEntregableFinderImpl finder;

    @Test
    void debeMapearAlDominio_cuandoElEntregableTieneProyecto() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        when(proyectoGradoOutputPort.obtenerPorEntregable(entregable))
                .thenReturn(Optional.of(new ProyectoGradoEntity(proyectoGrado, "Sistema de gestion", "EN_PROCESO")));

        // Act
        var resultado = finder.obtener(entregable);

        // Assert
        assertThat(resultado.getId()).isEqualTo(proyectoGrado);
        assertThat(resultado.getEstadoProyecto()).isEqualTo(EstadoProyecto.EN_PROCESO);
    }

    @Test
    void debeRetornarVacio_cuandoElEntregableNoTieneProyecto() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        when(proyectoGradoOutputPort.obtenerPorEntregable(entregable)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(entregable);

        // Assert
        assertThat(resultado).isSameAs(ProyectoGradoDomain.VACIO);
    }
}
