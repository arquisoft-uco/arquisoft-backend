package com.arquisoft.fichas.application.itemfichaperfil.command.validator;

import com.arquisoft.fichas.application.itemfichaperfil.command.validator.impl.ModificarItemFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.fichaperfil.model.PropietarioFichaCriteria;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPropiaRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ModificarItemFichaPerfilValidatorTest {

    @Mock
    private ItemFichaPropiaRule itemFichaPropiaRule;

    @InjectMocks
    private ModificarItemFichaPerfilValidatorImpl validator;

    @Test
    void debeDelegarEnLaReglaDeFichaPropia_cuandoValida() {
        // Arrange
        UUID ficha = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();

        // Act
        validator.validar(ficha, estudiante);

        // Assert
        verify(itemFichaPropiaRule).validar(new PropietarioFichaCriteria(ficha, estudiante));
    }
}
