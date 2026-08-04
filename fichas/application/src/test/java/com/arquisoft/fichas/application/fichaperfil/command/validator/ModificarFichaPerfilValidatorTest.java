package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.application.fichaperfil.command.validator.impl.ModificarFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.model.PropietarioFichaCriteria;
import com.arquisoft.fichas.domain.fichaperfil.model.TituloFichaCriteria;
import com.arquisoft.fichas.domain.fichaperfil.rules.EstudiantePropietarioFichaRule;
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
    void debeDelegarEnLaReglaDePropiedad_cuandoValidaPropiedad() {
        // Arrange
        UUID ficha = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();

        // Act
        validator.validarPropiedad(ficha, estudiante);

        // Assert
        verify(estudiantePropietarioFichaRule).validar(new PropietarioFichaCriteria(ficha, estudiante));
    }

    @Test
    void debeDelegarEnLaReglaDeTitulo_cuandoValidaTitulo() {
        // Arrange
        FichaPerfilAggregate ficha = FichaPerfilAggregate.crear("Titulo original", UUID.randomUUID());

        // Act
        validator.validarTitulo(ficha, "Titulo nuevo");

        // Assert
        verify(fichaPerfilTituloDisponibleRule)
                .validar(new TituloFichaCriteria("Titulo original", "Titulo nuevo"));
    }
}
