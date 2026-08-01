package com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.EstudianteFichaPerfilAggregate;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudianteFichaPerfilCupoDisponibleRule;
import com.arquisoft.shared.message.FichasMessages;
import com.arquisoft.shared.validation.ValidationResult;

import java.util.List;

public class EstudianteFichaPerfilCupoDisponibleRuleImpl implements EstudianteFichaPerfilCupoDisponibleRule {

    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    public EstudianteFichaPerfilCupoDisponibleRuleImpl(
            EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort) {
        this.estudianteFichaPerfilOutputPort = estudianteFichaPerfilOutputPort;
    }

    @Override
    public void validar(List<EstudianteFichaPerfilAggregate> nuevasRelaciones) {
        var result = new ValidationResult();
        long existentes = estudianteFichaPerfilOutputPort
                .contarPorFichaPerfilId(nuevasRelaciones.getFirst().getFichaPerfilId());

        if (existentes + nuevasRelaciones.size() > FichasMessages.FichaPerfil.ESTUDIANTES_MAX) {
            result.addError(
                    FichasMessages.EstudianteFichaPerfil.CAMPO_ESTUDIANTES,
                    FichasMessages.EstudianteFichaPerfil.LIMITE_ESTUDIANTES_EXCEDIDO,
                    FichasMessages.EstudianteFichaPerfil.LIMITE_EXCEDIDO_MSG.formatted(
                            FichasMessages.FichaPerfil.ESTUDIANTES_MAX
                    )
            );
        }

        result.throwIfHasErrors();
    }
}
