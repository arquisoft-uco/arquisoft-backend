package com.arquisoft.mapas_ruta.application.tarea.command.validator.impl;

import com.arquisoft.mapas_ruta.application.tarea.command.validator.CambiarEstadoItemTareaValidator;
import com.arquisoft.mapas_ruta.domain.tarea.CambioEstadoItemTareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.model.ExistenciaTarea;
import com.arquisoft.mapas_ruta.domain.tarea.model.TransicionEstadoItem;
import com.arquisoft.mapas_ruta.domain.tarea.rules.TareaExisteRule;
import com.arquisoft.mapas_ruta.domain.tarea.rules.TransicionEstadoItemPermitidaRule;
import com.arquisoft.mapas_ruta.domain.tarea.rules.impl.TareaExisteRuleImpl;
import com.arquisoft.mapas_ruta.domain.tarea.rules.impl.TransicionEstadoItemPermitidaRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class CambiarEstadoItemTareaValidatorImpl implements CambiarEstadoItemTareaValidator {

    private final TareaExisteRule tareaExisteRule;
    private final TransicionEstadoItemPermitidaRule transicionEstadoItemPermitidaRule;

    public CambiarEstadoItemTareaValidatorImpl() {
        this.tareaExisteRule = new TareaExisteRuleImpl();
        this.transicionEstadoItemPermitidaRule = new TransicionEstadoItemPermitidaRuleImpl();
    }

    @Override
    public void validar(CambioEstadoItemTareaDomain cambio, TareaDomain tarea) {
        tareaExisteRule.validar(new ExistenciaTarea(cambio.getTarea(), !tarea.esVacio()));
        transicionEstadoItemPermitidaRule.validar(new TransicionEstadoItem(
                cambio.getTarea(), tarea.getEstadoItem(), cambio.getEstadoItem()));
    }
}
