package com.arquisoft.shared.catalogoparametros.application.command.secondaryport;

import com.arquisoft.shared.catalogoparametros.application.command.secondaryport.entity.ParametrosCatalogoEntity;

public interface ParametrosCatalogoOutputPort {

    String valor(String nombre);

    void reemplazar(ParametrosCatalogoEntity parametros);

    boolean suspendido();

    void suspender();
}
