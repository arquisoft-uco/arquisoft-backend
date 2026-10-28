package com.arquisoft.mapas_ruta.application.tarea.command.validator.impl;

import com.arquisoft.mapas_ruta.application.tarea.command.validator.RemoverInformacionTareaValidator;
import com.arquisoft.mapas_ruta.domain.tarea.RemocionTareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.model.ExistenciaTarea;
import com.arquisoft.mapas_ruta.domain.tarea.model.RemocionTarea;
import com.arquisoft.mapas_ruta.domain.tarea.rules.TareaExisteRule;
import com.arquisoft.mapas_ruta.domain.tarea.rules.TareaRemovibleRule;
import com.arquisoft.mapas_ruta.domain.tarea.rules.impl.TareaExisteRuleImpl;
import com.arquisoft.mapas_ruta.domain.tarea.rules.impl.TareaRemovibleRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class RemoverInformacionTareaValidatorImpl implements RemoverInformacionTareaValidator {

    private final TareaExisteRule tareaExisteRule;
    private final TareaRemovibleRule tareaRemovibleRule;

    public RemoverInformacionTareaValidatorImpl() {
        this.tareaExisteRule = new TareaExisteRuleImpl();
        this.tareaRemovibleRule = new TareaRemovibleRuleImpl();
    }

    @Override
    public void validar(RemocionTareaDomain remocion, TareaDomain tarea) {
        tareaExisteRule.validar(new ExistenciaTarea(remocion.getTarea(), !tarea.esVacio()));
        tareaRemovibleRule.validar(new RemocionTarea(remocion.getTarea(), tarea.getEstadoItem()));
    }
}
