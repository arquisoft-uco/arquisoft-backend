package com.arquisoft.shared.catalogoparametros.application.command.result.mapper;

import com.arquisoft.shared.catalogoparametros.application.command.result.RefrescoParametrosCatalogoResult;
import com.arquisoft.shared.catalogoparametros.application.command.secondaryport.entity.ParametrosCatalogoEntity;

public final class RefrescoParametrosCatalogoResultMapper {

    private RefrescoParametrosCatalogoResultMapper() {}

    public static RefrescoParametrosCatalogoResult.Refrescado toResultRefrescado(ParametrosCatalogoEntity parametros) {
        return new RefrescoParametrosCatalogoResult.Refrescado(parametros.valores().size());
    }

    public static RefrescoParametrosCatalogoResult toResultConservado(String motivo) {
        return new RefrescoParametrosCatalogoResult.Conservado(motivo);
    }

    public static RefrescoParametrosCatalogoResult toResultRechazado(int estado) {
        return new RefrescoParametrosCatalogoResult.Rechazado(estado);
    }

    public static RefrescoParametrosCatalogoResult toResultSuspendido() {
        return new RefrescoParametrosCatalogoResult.Suspendido();
    }

    public static RefrescoParametrosCatalogoResult toResultSinConfigurar() {
        return new RefrescoParametrosCatalogoResult.SinConfigurar();
    }
}
