package com.arquisoft.proyectos.application.coordinador.command.finder.impl;

import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
import com.arquisoft.proyectos.application.coordinador.command.secondaryport.CoordinadorOutputPort;
import com.arquisoft.proyectos.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
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
class CoordinadorPorIdFinderImplTest {

    @Mock
    private CoordinadorOutputPort coordinadorOutputPort;

    @InjectMocks
    private CoordinadorPorIdFinderImpl finder;

    @Test
    void debeDelegarEnElOutputPort_enCoordinadorPorIdFinder() {
        // Arrange
        var id = UUID.randomUUID();
        var entity = new CoordinadorEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());
        when(coordinadorOutputPort.obtenerPorId(id)).thenReturn(Optional.of(entity));

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado.getId()).isEqualTo(id);
    }

    @Test
    void debeRetornarVacio_cuandoNoExiste() {
        // Arrange
        var id = UUID.randomUUID();
        when(coordinadorOutputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isEqualTo(CoordinadorDomain.VACIO);
    }
}
