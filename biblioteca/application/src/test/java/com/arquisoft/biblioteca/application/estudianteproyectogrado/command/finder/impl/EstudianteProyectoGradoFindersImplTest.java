package com.arquisoft.biblioteca.application.estudianteproyectogrado.command.finder.impl;

import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.secondaryport.entity.AutorDeEntregableEntity;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.model.AutorDeEntregable;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudianteProyectoGradoFindersImplTest {

    @Mock
    private EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;

    private AutoresDeEntregableFinderImpl autoresFinder;
    private VinculosDeProyectoGradoFinderImpl vinculosFinder;
    private VinculoEstudianteProyectoGradoFinderImpl vinculoFinder;

    @BeforeEach
    void setUp() {
        autoresFinder = new AutoresDeEntregableFinderImpl(estudianteProyectoGradoOutputPort);
        vinculosFinder = new VinculosDeProyectoGradoFinderImpl(estudianteProyectoGradoOutputPort);
        vinculoFinder = new VinculoEstudianteProyectoGradoFinderImpl(estudianteProyectoGradoOutputPort);
    }

    @Test
    void debeMapearLosAutoresConSuVigencia_cuandoElPuertoLosDevuelve() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var vigente = UtilUUID.generarNuevoUUID();
        var ausente = UtilUUID.generarNuevoUUID();
        when(estudianteProyectoGradoOutputPort.consultarAutoresPorEntregable(entregable)).thenReturn(List.of(
                new AutorDeEntregableEntity(vigente, true), new AutorDeEntregableEntity(ausente, false)));

        // Act
        var resultado = autoresFinder.obtener(entregable);

        // Assert
        assertThat(resultado).containsExactly(new AutorDeEntregable(vigente, true), new AutorDeEntregable(ausente, false));
    }

    @Test
    void debeRetornarTodosLosVinculosDelProyecto_cuandoElPuertoLosDevuelve() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var autor = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-15T10:00:00Z");
        var retirado = Instant.parse("2026-11-16T10:00:00Z");
        when(estudianteProyectoGradoOutputPort.consultarPorProyectoGrado(proyecto)).thenReturn(List.of(
                new EstudianteProyectoGradoEntity(UtilUUID.generarNuevoUUID(), autor, proyecto, ocurridoEn, retirado)));

        // Act
        var resultado = vinculosFinder.obtener(proyecto);

        // Assert
        assertThat(resultado).singleElement().satisfies(vinculo -> {
            assertThat(vinculo.getAutor()).isEqualTo(autor);
            assertThat(vinculo.estaEliminado()).isTrue();
        });
    }

    @Test
    void debeRetornarElVinculoOVacio_segunExistaPorAutorYProyecto() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var autor = UtilUUID.generarNuevoUUID();
        var otro = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-15T10:00:00Z");
        when(estudianteProyectoGradoOutputPort.obtenerPorAutorYProyectoGrado(autor, proyecto)).thenReturn(Optional.of(
                new EstudianteProyectoGradoEntity(UtilUUID.generarNuevoUUID(), autor, proyecto, ocurridoEn, UtilFecha.VACIO)));
        when(estudianteProyectoGradoOutputPort.obtenerPorAutorYProyectoGrado(otro, proyecto))
                .thenReturn(Optional.empty());

        // Act
        var presente = vinculoFinder.obtener(EstudianteProyectoGradoDomain.crear(autor, proyecto, ocurridoEn));
        var ausente = vinculoFinder.obtener(EstudianteProyectoGradoDomain.crear(otro, proyecto, ocurridoEn));

        // Assert
        assertThat(presente.getAutor()).isEqualTo(autor);
        assertThat(ausente).isSameAs(EstudianteProyectoGradoDomain.VACIO);
    }
}
