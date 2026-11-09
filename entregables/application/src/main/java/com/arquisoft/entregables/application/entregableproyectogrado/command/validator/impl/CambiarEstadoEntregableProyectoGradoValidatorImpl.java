package com.arquisoft.entregables.application.entregableproyectogrado.command.validator.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.command.validator.CambiarEstadoEntregableProyectoGradoValidator;
import com.arquisoft.entregables.domain.entregableproyectogrado.CambioEstadoEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.model.ExistenciaEntregableProyectoGrado;
import com.arquisoft.entregables.domain.entregableproyectogrado.model.TransicionEstadoEntregable;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.CancelacionEntregablePermitidaRule;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.CierreEntregablePermitidoRule;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.EntregableProyectoGradoExisteRule;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.EstadoDestinoEntregablePermitidoRule;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.EstadoEntregableNoTerminalRule;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl.CancelacionEntregablePermitidaRuleImpl;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl.CierreEntregablePermitidoRuleImpl;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl.EntregableProyectoGradoExisteRuleImpl;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl.EstadoDestinoEntregablePermitidoRuleImpl;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl.EstadoEntregableNoTerminalRuleImpl;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.entregables.domain.proyectogrado.model.EstadoActualProyectoGrado;
import com.arquisoft.entregables.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.entregables.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.entregables.domain.proyectogrado.rules.ProyectoGradoNoFinalizadoRule;
import com.arquisoft.entregables.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import com.arquisoft.entregables.domain.proyectogrado.rules.impl.ProyectoGradoNoFinalizadoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class CambiarEstadoEntregableProyectoGradoValidatorImpl
        implements CambiarEstadoEntregableProyectoGradoValidator {

    private final EntregableProyectoGradoExisteRule entregableProyectoGradoExisteRule;
    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final ProyectoGradoNoFinalizadoRule proyectoGradoNoFinalizadoRule;
    private final EstadoEntregableNoTerminalRule estadoEntregableNoTerminalRule;
    private final EstadoDestinoEntregablePermitidoRule estadoDestinoEntregablePermitidoRule;
    private final CierreEntregablePermitidoRule cierreEntregablePermitidoRule;
    private final CancelacionEntregablePermitidaRule cancelacionEntregablePermitidaRule;

    public CambiarEstadoEntregableProyectoGradoValidatorImpl() {
        this.entregableProyectoGradoExisteRule = new EntregableProyectoGradoExisteRuleImpl();
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.proyectoGradoNoFinalizadoRule = new ProyectoGradoNoFinalizadoRuleImpl();
        this.estadoEntregableNoTerminalRule = new EstadoEntregableNoTerminalRuleImpl();
        this.estadoDestinoEntregablePermitidoRule = new EstadoDestinoEntregablePermitidoRuleImpl();
        this.cierreEntregablePermitidoRule = new CierreEntregablePermitidoRuleImpl();
        this.cancelacionEntregablePermitidaRule = new CancelacionEntregablePermitidaRuleImpl();
    }

    @Override
    public void validar(CambioEstadoEntregableProyectoGradoDomain cambio, EntregableProyectoGradoDomain entregable,
                        ProyectoGradoDomain proyecto) {
        entregableProyectoGradoExisteRule.validar(
                new ExistenciaEntregableProyectoGrado(cambio.getEntregableProyectoGrado(), !entregable.esVacio()));
        proyectoGradoExisteRule.validar(
                new ExistenciaProyectoGrado(entregable.getProyectoGrado(), !proyecto.esVacio()));
        proyectoGradoNoFinalizadoRule.validar(new EstadoActualProyectoGrado(
                entregable.getProyectoGrado(), proyecto.getNombre(), proyecto.getEstadoProyecto()));
        var transicion = new TransicionEstadoEntregable(
                cambio.getEntregableProyectoGrado(), entregable.getEstadoEntregable(), cambio.getEstadoEntregable());
        estadoEntregableNoTerminalRule.validar(transicion);
        estadoDestinoEntregablePermitidoRule.validar(transicion);
        cierreEntregablePermitidoRule.validar(transicion);
        cancelacionEntregablePermitidaRule.validar(transicion);
    }
}
