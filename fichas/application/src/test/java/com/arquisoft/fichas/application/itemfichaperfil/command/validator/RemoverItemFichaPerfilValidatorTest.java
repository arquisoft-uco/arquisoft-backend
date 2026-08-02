package com.arquisoft.fichas.application.itemfichaperfil.command.validator;

import com.arquisoft.fichas.domain.fichaperfil.model.PropietarioFichaCriteria;
import com.arquisoft.fichas.domain.fichaperfil.rules.EstudiantePropietarioFichaRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RemoverItemFichaPerfilValidatorTest {

    @Mock
    private EstudiantePropietarioFichaRule estudiantePropietarioFichaRule;

    @InjectMocks
    private RemoverItemFichaPerfilValidator validator;

    @Test
    void debeDelegarEnLaReglaDePropiedad_cuandoValida() {
        // Arrange
        UUID ficha = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();

        // Act
        validator.validar(ficha, estudiante);

        // Assert
        verify(estudiantePropietarioFichaRule).validar(new PropietarioFichaCriteria(ficha, estudiante));
    }
}
