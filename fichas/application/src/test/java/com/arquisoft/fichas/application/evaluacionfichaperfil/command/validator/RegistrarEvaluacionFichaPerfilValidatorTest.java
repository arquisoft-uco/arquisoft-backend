package com.arquisoft.fichas.application.evaluacionfichaperfil.command.validator;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.validator.impl.RegistrarEvaluacionFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.aggregate.EvaluacionFichaPerfilDomain;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.EvaluacionRepresentanteFichaCriteria;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.EvaluacionNoDuplicadaRule;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.RepresentanteComiteExisteRule;
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
        validator.validar(evaluacion);

        // Assert
        InOrder inOrder = inOrder(fichaPerfilExisteRule, representanteComiteExisteRule, evaluacionNoDuplicadaRule);
        inOrder.verify(fichaPerfilExisteRule).validar(ficha);
        inOrder.verify(representanteComiteExisteRule).validar(representante);
        inOrder.verify(evaluacionNoDuplicadaRule)
                .validar(new EvaluacionRepresentanteFichaCriteria(representante, ficha));
    }
}
