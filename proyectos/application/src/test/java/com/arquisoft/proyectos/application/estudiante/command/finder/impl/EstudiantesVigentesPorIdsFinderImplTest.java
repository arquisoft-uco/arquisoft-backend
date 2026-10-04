package com.arquisoft.proyectos.application.estudiante.command.finder.impl;

import com.arquisoft.proyectos.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.proyectos.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudiantesVigentesPorIdsFinderImplTest {

    @Mock
    private EstudianteOutputPort estudianteOutputPort;

    @InjectMocks
    private EstudiantesVigentesPorIdsFinderImpl finder;

    @Test
    void debeConsultarTodosLosIdsDeUnaVezYMapearlos_cuandoHayVigentes() {
        // Arrange
        var ana = UUID.randomUUID();
        var luis = UUID.randomUUID();
        var eva = UUID.randomUUID();
        var ids = List.of(ana, luis, eva);
        var ocurridoEn = Instant.parse("2026-09-01T10:00:00Z");
        when(estudianteOutputPort.obtenerVigentesPorIds(ids)).thenReturn(List.of(
                new EstudianteEntity(ana, "2020", "Ana Gomez", "ana.gomez@soyuco.edu.co", ocurridoEn, null),
                new EstudianteEntity(luis, "2021", "Luis Diaz", "luis.diaz@soyuco.edu.co", ocurridoEn, null)));

        // Act
        var vigentes = finder.obtener(ids);

        // Assert
        assertThat(vigentes).extracting(EstudianteDomain::getId).containsExactly(ana, luis);
        assertThat(vigentes).extracting(EstudianteDomain::getNombre).containsExactly("Ana Gomez", "Luis Diaz");
        verify(estudianteOutputPort).obtenerVigentesPorIds(ids);
    }
}
