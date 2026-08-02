package com.arquisoft.fichas.application.estudiantefichaperfil.command.validator;

import com.arquisoft.fichas.domain.estudiante.rules.EstudiantesExistenRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.VinculacionEstudianteCriteria;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.VinculoEstudianteFichaExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class RemoverEstudianteFichaPerfilValidatorTest {

    @Mock
    private FichaPerfilExisteRule fichaPerfilExisteRule;

    @Mock
    private EstudiantesExistenRule estudiantesExistenRule;

    @Mock
    private VinculoEstudianteFichaExisteRule vinculoEstudianteFichaExisteRule;

    @InjectMocks
    private RemoverEstudianteFichaPerfilValidator validator;

    @Test
    void debeAplicarLasReglasEnOrden_cuandoValida() {
        // Arrange
        UUID ficha = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();

        // Act
        validator.validar(ficha, estudiante);

        // Assert
        InOrder inOrder = inOrder(fichaPerfilExisteRule, estudiantesExistenRule,
                vinculoEstudianteFichaExisteRule);
        inOrder.verify(fichaPerfilExisteRule).validar(ficha);
        inOrder.verify(estudiantesExistenRule).validar(List.of(estudiante));
        inOrder.verify(vinculoEstudianteFichaExisteRule)
                .validar(new VinculacionEstudianteCriteria(ficha, estudiante));
    }
}
