package com.arquisoft.fichas.application.itemfichaperfil.command.validator;

import com.arquisoft.fichas.application.itemfichaperfil.command.finder.FichaPerfilDelItemFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.impl.RemoverItemFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.fichaperfil.model.PropietarioFichaCriteria;
import com.arquisoft.fichas.domain.fichaperfil.rules.EstudiantePropietarioFichaRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverItemFichaPerfilValidatorTest {

    @Mock
    private FichaPerfilDelItemFinder fichaPerfilDelItemFinder;

    @Mock
    private EstudiantePropietarioFichaRule estudiantePropietarioFichaRule;

    @InjectMocks
    private RemoverItemFichaPerfilValidatorImpl validator;

    @Test
    void debeDelegarEnLaReglaDePropiedad_cuandoValida() {
        // Arrange
        UUID item = UUID.randomUUID();
        UUID ficha = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();

        when(fichaPerfilDelItemFinder.obtener(item)).thenReturn(ficha);

        // Act
        validator.validar(item, estudiante);

        // Assert
        verify(estudiantePropietarioFichaRule).validar(new PropietarioFichaCriteria(ficha, estudiante));
    }
}
