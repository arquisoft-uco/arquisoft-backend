package com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.finder.impl;

import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VinculosDeProyectoGradoFinderImplTest {

    @Mock
    private EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;

    @InjectMocks
    private VinculosDeProyectoGradoFinderImpl finder;

    @Test
    void debeMapearLosVinculosAlDominio_cuandoElProyectoTieneVinculos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var vigente = new EstudianteProyectoGradoEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                proyectoGrado, Instant.parse("2026-10-20T10:00:00Z"), UtilFecha.VACIO);
        var eliminado = new EstudianteProyectoGradoEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                proyectoGrado, Instant.parse("2026-10-20T10:00:00Z"), Instant.parse("2026-10-21T10:00:00Z"));
        when(estudianteProyectoGradoOutputPort.consultarPorProyectoGrado(proyectoGrado))
                .thenReturn(List.of(vigente, eliminado));

        // Act
        var vinculos = finder.obtener(proyectoGrado);

        // Assert
        assertThat(vinculos).hasSize(2);
        assertThat(vinculos.get(0).getId()).isEqualTo(vigente.id());
        assertThat(vinculos.get(0).estaEliminado()).isFalse();
        assertThat(vinculos.get(1).estaEliminado()).isTrue();
        verify(estudianteProyectoGradoOutputPort).consultarPorProyectoGrado(proyectoGrado);
    }

    @Test
    void debeDevolverListaVacia_cuandoElProyectoNoTieneVinculos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        when(estudianteProyectoGradoOutputPort.consultarPorProyectoGrado(proyectoGrado)).thenReturn(List.of());

        // Act
        var vinculos = finder.obtener(proyectoGrado);

        // Assert
        assertThat(vinculos).isEmpty();
    }
}
