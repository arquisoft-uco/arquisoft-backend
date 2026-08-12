package com.arquisoft.fichas.application.evaluacionfichaperfil.command.validator;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.validator.impl.RegistrarEvaluacionFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.DisponibilidadEvaluacionFicha;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ExistenciaRepresentanteComite;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.EvaluacionNoDuplicadaRule;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.RepresentanteComiteExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaFichaPerfil;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class RegistrarEvaluacionFichaPerfilValidatorTest {

    @Mock
    private FichaPerfilExisteRule fichaPerfilExisteRule;

    @Mock
    private RepresentanteComiteExisteRule representanteComiteExisteRule;

    @Mock
    private EvaluacionNoDuplicadaRule evaluacionNoDuplicadaRule;

    @InjectMocks
    private RegistrarEvaluacionFichaPerfilValidatorImpl validator;

    @Test
    void debeAplicarLasReglasEnOrden_cuandoValida() {
        // Arrange
        UUID representante = UUID.randomUUID();
        UUID ficha = UUID.randomUUID();
        var evaluacion = EvaluacionFichaPerfilDomain.crear(representante, ficha);

        // Act
        validator.validar(evaluacion, true, true, false);

        // Assert
        InOrder inOrder = inOrder(fichaPerfilExisteRule, representanteComiteExisteRule,
                evaluacionNoDuplicadaRule);
        inOrder.verify(fichaPerfilExisteRule).validar(new ExistenciaFichaPerfil(ficha, true));
        inOrder.verify(representanteComiteExisteRule)
                .validar(new ExistenciaRepresentanteComite(representante, true));
        inOrder.verify(evaluacionNoDuplicadaRule)
                .validar(new DisponibilidadEvaluacionFicha(representante, ficha, false));
    }

    @Test
    void debeTrasladarLosDatosConsultados_cuandoLaEvaluacionYaExiste() {
        // Arrange
        UUID representante = UUID.randomUUID();
        UUID ficha = UUID.randomUUID();
        var evaluacion = EvaluacionFichaPerfilDomain.crear(representante, ficha);

        // Act
        validator.validar(evaluacion, true, true, true);

        // Assert
        inOrder(evaluacionNoDuplicadaRule).verify(evaluacionNoDuplicadaRule)
                .validar(new DisponibilidadEvaluacionFicha(representante, ficha, true));
    }
}
