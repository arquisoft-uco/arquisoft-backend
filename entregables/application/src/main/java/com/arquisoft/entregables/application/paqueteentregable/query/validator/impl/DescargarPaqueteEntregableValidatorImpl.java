package com.arquisoft.entregables.application.paqueteentregable.query.validator.impl;

import com.arquisoft.entregables.application.paqueteentregable.query.criteria.DescargaPaqueteEntregableCriteria;
import com.arquisoft.entregables.application.paqueteentregable.query.validator.DescargarPaqueteEntregableValidator;
import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.model.ExistenciaEntregableProyectoGrado;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.EntregableProyectoGradoExisteRule;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl.EntregableProyectoGradoExisteRuleImpl;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.EstadoEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.model.ExistenciaEstadoEntregableProyectoGrado;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.rules.EstadoEntregableProyectoGradoExisteRule;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.rules.impl.EstadoEntregableProyectoGradoExisteRuleImpl;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;
import com.arquisoft.entregables.domain.paqueteentregable.model.AccesoPaqueteEntregable;
import com.arquisoft.entregables.domain.paqueteentregable.model.DisponibilidadPaqueteEntregable;
import com.arquisoft.entregables.domain.paqueteentregable.model.EstadoEntregableDelPaquete;
import com.arquisoft.entregables.domain.paqueteentregable.rules.ActorAccedePaqueteEntregableRule;
import com.arquisoft.entregables.domain.paqueteentregable.rules.PaqueteEntregableDisponibleRule;
import com.arquisoft.entregables.domain.paqueteentregable.rules.PaqueteEntregableNoCanceladoRule;
import com.arquisoft.entregables.domain.paqueteentregable.rules.impl.ActorAccedePaqueteEntregableRuleImpl;
import com.arquisoft.entregables.domain.paqueteentregable.rules.impl.PaqueteEntregableDisponibleRuleImpl;
import com.arquisoft.entregables.domain.paqueteentregable.rules.impl.PaqueteEntregableNoCanceladoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class DescargarPaqueteEntregableValidatorImpl implements DescargarPaqueteEntregableValidator {

    private final EntregableProyectoGradoExisteRule entregableProyectoGradoExisteRule;
    private final EstadoEntregableProyectoGradoExisteRule estadoEntregableProyectoGradoExisteRule;
    private final ActorAccedePaqueteEntregableRule actorAccedePaqueteEntregableRule;
    private final PaqueteEntregableNoCanceladoRule paqueteEntregableNoCanceladoRule;
    private final PaqueteEntregableDisponibleRule paqueteEntregableDisponibleRule;

    public DescargarPaqueteEntregableValidatorImpl() {
        this.entregableProyectoGradoExisteRule = new EntregableProyectoGradoExisteRuleImpl();
        this.estadoEntregableProyectoGradoExisteRule = new EstadoEntregableProyectoGradoExisteRuleImpl();
        this.actorAccedePaqueteEntregableRule = new ActorAccedePaqueteEntregableRuleImpl();
        this.paqueteEntregableNoCanceladoRule = new PaqueteEntregableNoCanceladoRuleImpl();
        this.paqueteEntregableDisponibleRule = new PaqueteEntregableDisponibleRuleImpl();
    }

    @Override
    public void validar(DescargaPaqueteEntregableCriteria criteria, EntregableProyectoGradoDomain entregable,
                        EstadoEntregableProyectoGradoDomain estadoActual, boolean participa,
                        PaqueteEntregableDomain paquete) {
        entregableProyectoGradoExisteRule.validar(new ExistenciaEntregableProyectoGrado(
                criteria.entregableProyectoGrado(), !entregable.esVacio()));
        actorAccedePaqueteEntregableRule.validar(new AccesoPaqueteEntregable(
                criteria.entregableProyectoGrado(), criteria.actor(), criteria.comoAsesor(),
                criteria.comoEstudiante(), entregable.getAsesor(), participa));
        estadoEntregableProyectoGradoExisteRule.validar(new ExistenciaEstadoEntregableProyectoGrado(
                criteria.entregableProyectoGrado(), !estadoActual.esVacio()));
        paqueteEntregableNoCanceladoRule.validar(new EstadoEntregableDelPaquete(
                criteria.entregableProyectoGrado(), entregable.getVersion(), estadoActual.getEstadoEntregable()));
        paqueteEntregableDisponibleRule.validar(new DisponibilidadPaqueteEntregable(
                criteria.entregableProyectoGrado(), !paquete.esVacio()));
    }
}
