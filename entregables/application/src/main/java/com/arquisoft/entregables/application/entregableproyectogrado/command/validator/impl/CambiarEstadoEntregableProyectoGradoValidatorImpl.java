package com.arquisoft.entregables.application.entregableproyectogrado.command.validator.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.command.validator.CambiarEstadoEntregableProyectoGradoValidator;
import com.arquisoft.entregables.domain.entregableproyectogrado.CambioEstadoEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.model.ExistenciaEntregableProyectoGrado;
import com.arquisoft.entregables.domain.entregableproyectogrado.model.TransicionEstadoEntregable;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.CierreEntregablePermitidoRule;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.EntregableProyectoGradoExisteRule;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.EstadoDestinoEntregablePermitidoRule;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl.CierreEntregablePermitidoRuleImpl;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl.EntregableProyectoGradoExisteRuleImpl;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl.EstadoDestinoEntregablePermitidoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class CambiarEstadoEntregableProyectoGradoValidatorImpl
        implements CambiarEstadoEntregableProyectoGradoValidator {

    private final EntregableProyectoGradoExisteRule entregableProyectoGradoExisteRule;
    private final EstadoDestinoEntregablePermitidoRule estadoDestinoEntregablePermitidoRule;
    private final CierreEntregablePermitidoRule cierreEntregablePermitidoRule;

    public CambiarEstadoEntregableProyectoGradoValidatorImpl() {
        this.entregableProyectoGradoExisteRule = new EntregableProyectoGradoExisteRuleImpl();
        this.estadoDestinoEntregablePermitidoRule = new EstadoDestinoEntregablePermitidoRuleImpl();
        this.cierreEntregablePermitidoRule = new CierreEntregablePermitidoRuleImpl();
    }

    @Override
    public void validar(CambioEstadoEntregableProyectoGradoDomain cambio, EntregableProyectoGradoDomain entregable) {
        entregableProyectoGradoExisteRule.validar(
                new ExistenciaEntregableProyectoGrado(cambio.getEntregableProyectoGrado(), !entregable.esVacio()));
        var transicion = new TransicionEstadoEntregable(
                cambio.getEntregableProyectoGrado(), entregable.getEstadoEntregable(), cambio.getEstadoEntregable());
        estadoDestinoEntregablePermitidoRule.validar(transicion);
        cierreEntregablePermitidoRule.validar(transicion);
    }
}
