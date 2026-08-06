package com.arquisoft.fichas.application.itemfichaperfil.command.validator;

import com.arquisoft.fichas.application.itemfichaperfil.command.finder.FichaPerfilDelItemFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.impl.ModificarItemFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilEnTerminalRule;
import com.arquisoft.fichas.domain.fichaperfil.model.PropietarioFichaCriteria;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPropiaRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModificarItemFichaPerfilValidatorTest {

    @Mock
    private FichaPerfilDelItemFinder fichaPerfilDelItemFinder;

    @Mock
    private ItemFichaPropiaRule itemFichaPropiaRule;

    @Mock
    private EstadoFichaPerfilEnTerminalRule estadoFichaPerfilEnTerminalRule;

    @InjectMocks
    private ModificarItemFichaPerfilValidatorImpl validator;

    @Test
    void debeDelegarEnAmbasReglas_cuandoValida() {
        // Arrange
        UUID item = UUID.randomUUID();
        UUID ficha = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();

        when(fichaPerfilDelItemFinder.obtener(item)).thenReturn(ficha);

        // Act
        validator.validar(item, estudiante);

        // Assert
        verify(itemFichaPropiaRule).validar(new PropietarioFichaCriteria(ficha, estudiante));
        verify(estadoFichaPerfilEnTerminalRule).validar(ficha);
    }
}
