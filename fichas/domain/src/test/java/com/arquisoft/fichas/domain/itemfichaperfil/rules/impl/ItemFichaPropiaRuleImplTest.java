package com.arquisoft.fichas.domain.itemfichaperfil.rules.impl;

import com.arquisoft.fichas.domain.fichaperfil.model.PropietarioFichaCriteria;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaNoPropiaException;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemFichaPropiaRuleImplTest {

    @Mock
    private FichaPerfilOutputPort puerto;

    @InjectMocks
    private ItemFichaPropiaRuleImpl regla;

    @Test
    void debeLanzarExcepcion_cuandoLaReglaNoSeCumple() {
        // Arrange
        var entrada = new PropietarioFichaCriteria(UUID.randomUUID(), UUID.randomUUID());
        when(puerto.esEstudiantePropietario(entrada)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOf(ItemFichaNoPropiaException.class);
    }

    @Test
    void debePasar_cuandoLaReglaSeCumple() {
        // Arrange
        var entrada = new PropietarioFichaCriteria(UUID.randomUUID(), UUID.randomUUID());
        when(puerto.esEstudiantePropietario(entrada)).thenReturn(true);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }
}
