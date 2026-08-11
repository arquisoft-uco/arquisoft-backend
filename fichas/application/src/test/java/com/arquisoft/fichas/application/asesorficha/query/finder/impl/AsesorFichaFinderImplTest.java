package com.arquisoft.fichas.application.asesorficha.query.finder.impl;

import com.arquisoft.fichas.application.asesorficha.query.secondaryport.AsesorFichaQueryOutputPort;
import com.arquisoft.fichas.application.asesorficha.query.readmodel.AsesorContactoReadModel;
import com.arquisoft.fichas.domain.fichaperfil.exception.AsesorFichaNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsesorFichaFinderImplTest {

    @Mock
    private AsesorFichaQueryOutputPort asesorFichaQueryOutputPort;

    @InjectMocks
    private AsesorFichaFinderImpl finder;

    @Test
    void debeRetornarElContacto_cuandoExiste() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        var contacto = new AsesorContactoReadModel(asesorId, "Ana Gomez", "ana.gomez@soyuco.edu.co");
        when(asesorFichaQueryOutputPort.buscarContactoPorId(asesorId)).thenReturn(Optional.of(contacto));

        // Act
        var resultado = finder.obtener(asesorId);

        // Assert
        assertThat(resultado).isEqualTo(contacto);
    }

    @Test
    void debeLanzarExcepcion_cuandoNoExiste() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        when(asesorFichaQueryOutputPort.buscarContactoPorId(asesorId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> finder.obtener(asesorId))
                .isInstanceOf(AsesorFichaNoEncontradoException.class)
                .hasMessageContaining(asesorId.toString());
    }
}
