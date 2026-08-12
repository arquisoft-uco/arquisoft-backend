package com.arquisoft.fichas.application.asesorficha.command.finder.impl;

import com.arquisoft.fichas.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.fichas.domain.asesorficha.model.ContactoAsesorFicha;
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
    void debeRetornarElContacto_cuandoExiste() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        var contacto = new ContactoAsesorFicha(asesorId, "Ana Asesora", "ana@arquisoft.com");
        when(asesorFichaOutputPort.buscarContactoPorId(asesorId)).thenReturn(Optional.of(contacto));

        // Act
        Optional<ContactoAsesorFicha> resultado = finder.obtener(asesorId);

        // Assert
        assertThat(resultado).contains(contacto);
    }

    @Test
    void debeRetornarVacio_cuandoNoExiste() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        when(asesorFichaOutputPort.buscarContactoPorId(asesorId)).thenReturn(Optional.empty());

        // Act
        Optional<ContactoAsesorFicha> resultado = finder.obtener(asesorId);

        // Assert — el finder nunca lanza por "no encontrado"; eso lo decide la rule
        assertThat(resultado).isEmpty();
    }
}
