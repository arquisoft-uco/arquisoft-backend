package com.arquisoft.fichas.application.estudiantefichaperfil.command.validator;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.impl.RemoverEstudianteFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.estudiante.model.ExistenciaEstudiantes;
import com.arquisoft.fichas.domain.estudiante.rules.EstudiantesExistenRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.RemocionEstudianteFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ExistenciaVinculoEstudianteFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.VinculoEstudianteFichaExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaFichaPerfil;
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
    private RemoverEstudianteFichaPerfilValidatorImpl validator;

    @Test
    void debeAplicarLasReglasEnOrden_cuandoValida() {
        // Arrange
        UUID fichaPerfil = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        var entrada = RemocionEstudianteFichaPerfilDomain.crear(fichaPerfil, estudiante);

        // Act
        validator.validar(entrada, true, List.of(estudiante), true);

        // Assert
        InOrder inOrder = inOrder(fichaPerfilExisteRule, estudiantesExistenRule,
                vinculoEstudianteFichaExisteRule);
        inOrder.verify(fichaPerfilExisteRule).validar(new ExistenciaFichaPerfil(fichaPerfil, true));
        inOrder.verify(estudiantesExistenRule)
                .validar(new ExistenciaEstudiantes(List.of(estudiante), List.of(estudiante)));
        inOrder.verify(vinculoEstudianteFichaExisteRule)
                .validar(new ExistenciaVinculoEstudianteFicha(fichaPerfil, estudiante, true));
    }

    @Test
    void debeTrasladarLosDatosConsultados_cuandoNoHayVinculo() {
        // Arrange
        UUID fichaPerfil = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        var entrada = RemocionEstudianteFichaPerfilDomain.crear(fichaPerfil, estudiante);

        // Act
        validator.validar(entrada, true, List.of(estudiante), false);

        // Assert
        inOrder(vinculoEstudianteFichaExisteRule).verify(vinculoEstudianteFichaExisteRule)
                .validar(new ExistenciaVinculoEstudianteFicha(fichaPerfil, estudiante, false));
    }
}
