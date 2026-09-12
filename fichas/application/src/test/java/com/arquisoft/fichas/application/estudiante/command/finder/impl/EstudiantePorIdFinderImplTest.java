package com.arquisoft.fichas.application.estudiante.command.finder.impl;

import com.arquisoft.fichas.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.fichas.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudiantePorIdFinderImplTest {

    @Mock
    private EstudianteOutputPort estudianteOutputPort;

    @InjectMocks
    private EstudiantePorIdFinderImpl finder;

    @Test
    void debeDelegarEnElOutputPort_cuandoElEstudianteExiste() {
        // Arrange
        var id = UUID.randomUUID();
        var entity = new EstudianteEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());
        when(estudianteOutputPort.obtenerPorId(id)).thenReturn(Optional.of(entity));

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).contains(entity);
    }

    @Test
    void debeDevolverVacio_cuandoElEstudianteNoExiste() {
        // Arrange
        var id = UUID.randomUUID();
        when(estudianteOutputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
