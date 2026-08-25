package com.arquisoft.fichas.application.asesorficha.command.finder.impl;

import com.arquisoft.fichas.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsesorFichaFinderImplTest {

    @Mock
    private AsesorFichaOutputPort asesorFichaOutputPort;

    @InjectMocks
    private AsesorFichaFinderImpl finder;

    @Test
    void debeConvertirLaEntidadADominio_cuandoExiste() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        var entity = new AsesorFichaEntity(asesorId, "A001", "Ana Asesora", "ana@arquisoft.com");
        when(asesorFichaOutputPort.buscarContactoPorId(asesorId)).thenReturn(Optional.of(entity));

        // Act
        Optional<AsesorFichaDomain> resultado = finder.obtener(asesorId);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(asesorId);
        assertThat(resultado.get().getIdentificador()).isEqualTo("A001");
        assertThat(resultado.get().getNombre()).isEqualTo("Ana Asesora");
        assertThat(resultado.get().getEmail()).isEqualTo("ana@arquisoft.com");
    }

    @Test
    void debeRetornarVacio_cuandoNoExiste() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        when(asesorFichaOutputPort.buscarContactoPorId(asesorId)).thenReturn(Optional.empty());

        // Act
        Optional<AsesorFichaDomain> resultado = finder.obtener(asesorId);

        // Assert — el finder nunca lanza por "no encontrado"; eso lo decide la rule
        assertThat(resultado).isEmpty();
    }
}
