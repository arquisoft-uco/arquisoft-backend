package com.arquisoft.fichas.application.asesorficha.command.finder.impl;

import com.arquisoft.fichas.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
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
class AsesorFichaVigenteFinderImplTest {

    @Mock
    private AsesorFichaOutputPort asesorFichaOutputPort;

    @InjectMocks
    private AsesorFichaVigenteFinderImpl finder;

    @Test
    void debeConvertirLaEntidadADominio_cuandoExiste() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        var entity = new AsesorFichaEntity(asesorId, "A001", "Ana Asesora", "ana@arquisoft.com", Instant.now(), java.time.Instant.EPOCH);
        when(asesorFichaOutputPort.obtenerVigentePorId(asesorId)).thenReturn(Optional.of(entity));

        // Act
        var resultado = finder.obtener(asesorId);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getId()).isEqualTo(asesorId);
        assertThat(resultado.getIdentificador()).isEqualTo("A001");
        assertThat(resultado.getNombre()).isEqualTo("Ana Asesora");
        assertThat(resultado.getEmail()).isEqualTo("ana@arquisoft.com");
    }

    @Test
    void debeRetornarVacio_cuandoNoExiste() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        when(asesorFichaOutputPort.obtenerVigentePorId(asesorId)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(asesorId);

        // Assert — el finder nunca lanza por "no encontrado"; eso lo decide la rule
        assertThat(resultado).isEqualTo(AsesorFichaDomain.VACIO);
    }
}
