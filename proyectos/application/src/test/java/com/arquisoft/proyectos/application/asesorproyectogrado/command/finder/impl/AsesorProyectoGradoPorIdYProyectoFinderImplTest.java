package com.arquisoft.proyectos.application.asesorproyectogrado.command.finder.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;
import com.arquisoft.proyectos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.CambioEstadoAsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;
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
class AsesorProyectoGradoPorIdYProyectoFinderImplTest {

    @Mock
    private AsesorProyectoGradoOutputPort asesorProyectoGradoOutputPort;

    @InjectMocks
    private AsesorProyectoGradoPorIdYProyectoFinderImpl finder;

    private final CambioEstadoAsesorProyectoGradoDomain entrada = CambioEstadoAsesorProyectoGradoDomain.crear(
            UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "FINALIZADO");

    @Test
    void debeDevolverDomain_cuandoElVinculoExiste() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var fecha = UtilFecha.generarInstanteActual();
        when(asesorProyectoGradoOutputPort.obtenerPorIdYProyectoGrado(
                entrada.getAsesorProyectoGrado(), entrada.getProyectoGrado()))
                .thenReturn(Optional.of(new AsesorProyectoGradoEntity(
                        entrada.getAsesorProyectoGrado(), asesor, entrada.getProyectoGrado(), "ASIGNADO", fecha)));

        // Act
        var vinculo = finder.obtener(entrada);

        // Assert
        assertThat(vinculo.esVacio()).isFalse();
        assertThat(vinculo.getId()).isEqualTo(entrada.getAsesorProyectoGrado());
        assertThat(vinculo.getAsesor()).isEqualTo(asesor);
        assertThat(vinculo.getEstadoAsesor()).isEqualTo(EstadoAsesor.ASIGNADO);
        assertThat(vinculo.getFechaAsignado()).isEqualTo(fecha);
    }

    @Test
    void debeDevolverVacio_cuandoElVinculoNoExiste() {
        // Arrange
        when(asesorProyectoGradoOutputPort.obtenerPorIdYProyectoGrado(
                entrada.getAsesorProyectoGrado(), entrada.getProyectoGrado()))
                .thenReturn(Optional.empty());

        // Act
        var vinculo = finder.obtener(entrada);

        // Assert
        assertThat(vinculo).isSameAs(AsesorProyectoGradoDomain.VACIO);
    }
}
