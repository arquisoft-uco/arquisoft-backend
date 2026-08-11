package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.application.fichaperfil.command.validator.impl.ModificarFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropietarioFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantePropietarioFichaRule;
import com.arquisoft.fichas.domain.fichaperfil.ModificacionFichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilTituloDisponibleRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ModificarFichaPerfilValidatorTest {

    @Mock
    private EstudiantePropietarioFichaRule estudiantePropietarioFichaRule;

    @Mock
    private FichaPerfilTituloDisponibleRule fichaPerfilTituloDisponibleRule;

    @InjectMocks
    private ModificarFichaPerfilValidatorImpl validator;

    @Test
    void debeDelegarEnAmbasReglas_cuandoValida() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        var modificacion = ModificacionFichaPerfilDomain.crear(fichaId, "Titulo nuevo", estudianteId);

        // Act — una sola llamada agrupa todas las reglas de la transacción
        validator.validar(modificacion);

        // Assert
        verify(estudiantePropietarioFichaRule).validar(new PropietarioFicha(fichaId, estudianteId));
        verify(fichaPerfilTituloDisponibleRule).validar(modificacion);
    }
}
