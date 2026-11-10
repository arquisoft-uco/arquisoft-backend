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
class ProyectoGradoPorIdFinderImplTest {

    @Mock
    private ProyectoGradoOutputPort proyectoGradoOutputPort;

    @InjectMocks
    private ProyectoGradoPorIdFinderImpl finder;

    @Test
    void debeMapearAlDominioConSuEstado_cuandoElProyectoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(proyectoGradoOutputPort.obtenerPorId(id))
                .thenReturn(Optional.of(new ProyectoGradoEntity(id, "Sistema de gestion", "FINALIZADO")));

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getEstadoProyecto()).isEqualTo(EstadoProyecto.FINALIZADO);
    }

    @Test
    void debeRetornarVacio_cuandoElProyectoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(proyectoGradoOutputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isSameAs(ProyectoGradoDomain.VACIO);
    }
}
