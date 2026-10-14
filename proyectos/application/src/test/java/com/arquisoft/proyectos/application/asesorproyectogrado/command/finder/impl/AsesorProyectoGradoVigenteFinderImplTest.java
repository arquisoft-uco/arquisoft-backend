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
class AsesorProyectoGradoVigenteFinderImplTest {

    @Mock
    private AsesorProyectoGradoOutputPort asesorProyectoGradoOutputPort;

    @InjectMocks
    private AsesorProyectoGradoVigenteFinderImpl finder;

    private final CambioEstadoAsesorProyectoGradoDomain entrada = CambioEstadoAsesorProyectoGradoDomain.crear(
            UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "FINALIZADO");

    @Test
    void debeDevolverDomain_cuandoExisteFila() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var fecha = UtilFecha.generarInstanteActual();
        when(asesorProyectoGradoOutputPort.obtenerVigentePorAsesorYProyectoGrado(
                entrada.getAsesor(), entrada.getProyectoGrado()))
                .thenReturn(Optional.of(new AsesorProyectoGradoEntity(
                        id, entrada.getAsesor(), entrada.getProyectoGrado(), "ASIGNADO", fecha)));

        // Act
        var vigente = finder.obtener(entrada);

        // Assert
        assertThat(vigente.esVacio()).isFalse();
        assertThat(vigente.getId()).isEqualTo(id);
        assertThat(vigente.getAsesor()).isEqualTo(entrada.getAsesor());
        assertThat(vigente.getEstadoAsesor()).isEqualTo(EstadoAsesor.ASIGNADO);
        assertThat(vigente.getFechaAsignado()).isEqualTo(fecha);
    }

    @Test
    void debeDevolverVACIO_cuandoNoHayFilas() {
        // Arrange
        when(asesorProyectoGradoOutputPort.obtenerVigentePorAsesorYProyectoGrado(
                entrada.getAsesor(), entrada.getProyectoGrado()))
                .thenReturn(Optional.empty());

        // Act
        var vigente = finder.obtener(entrada);

        // Assert
        assertThat(vigente).isSameAs(AsesorProyectoGradoDomain.VACIO);
    }
}
