package com.arquisoft.fichas.application.evaluacionfichaperfil.command.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.model.RegistrarEvaluacionFichaPerfilCommand;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;

/**
 * A diferencia del resto de transacciones de fichas, ésta no necesita un dominio propio: la
 * evaluación que se registra tiene exactamente los dos datos que la transacción recibe, y el
 * representante de comité es a la vez el actor y un campo de la evaluación. Introducir una clase
 * aparte sería una copia literal de {@link EvaluacionFichaPerfilDomain}.
 */
public final class RegistrarEvaluacionFichaPerfilMapper {

    private RegistrarEvaluacionFichaPerfilMapper() {}

    public static EvaluacionFichaPerfilDomain toDomain(RegistrarEvaluacionFichaPerfilCommand command) {
        return EvaluacionFichaPerfilDomain.crear(command.representanteComite(), command.fichaPerfil());
    }
}
