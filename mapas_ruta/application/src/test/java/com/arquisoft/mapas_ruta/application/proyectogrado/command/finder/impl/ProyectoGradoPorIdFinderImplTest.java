package com.arquisoft.mapas_ruta.application.proyectogrado.command.finder.impl;

import com.arquisoft.mapas_ruta.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.mapas_ruta.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.EstadoProyectoGrado;
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
    void debeRetornarElDomainReconstruido_cuandoElProyectoExiste() {
        // Arrange
        var entity = new ProyectoGradoEntity(UtilUUID.generarNuevoUUID(), "EN_PROCESO",
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Titulo");
        when(proyectoGradoOutputPort.obtenerPorId(entity.id())).thenReturn(Optional.of(entity));

        // Act
        var resultado = finder.obtener(entity.id());

        // Assert
        assertThat(resultado.getId()).isEqualTo(entity.id());
        assertThat(resultado.getEstadoProyectoGrado()).isEqualTo(EstadoProyectoGrado.EN_PROCESO);
        assertThat(resultado.getCoordinador()).isEqualTo(entity.coordinador());
        assertThat(resultado.getFichaPerfil()).isEqualTo(entity.fichaPerfil());
        assertThat(resultado.getTituloProyecto()).isEqualTo("Titulo");
    }

    @Test
    void debeRetornarElCentinelaVacio_cuandoElProyectoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(proyectoGradoOutputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isEqualTo(ProyectoGradoDomain.VACIO);
    }
}
