package com.arquisoft.entregables.application.paqueteentregable.query.validator.impl;

import com.arquisoft.entregables.application.paqueteentregable.query.criteria.DescargaPaqueteEntregableCriteria;
import com.arquisoft.entregables.application.paqueteentregable.query.validator.DescargarPaqueteEntregableValidator;
import com.arquisoft.entregables.domain.entregableproyectogrado.model.ExistenciaEntregableProyectoGrado;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.EntregableProyectoGradoExisteRule;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl.EntregableProyectoGradoExisteRuleImpl;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;
import com.arquisoft.entregables.domain.paqueteentregable.model.AccesoPaqueteEntregable;
import com.arquisoft.entregables.domain.paqueteentregable.model.DisponibilidadPaqueteEntregable;
import com.arquisoft.entregables.domain.paqueteentregable.rules.ActorAccedePaqueteEntregableRule;
import com.arquisoft.entregables.domain.paqueteentregable.rules.PaqueteEntregableDisponibleRule;
import com.arquisoft.entregables.domain.paqueteentregable.rules.impl.ActorAccedePaqueteEntregableRuleImpl;
import com.arquisoft.entregables.domain.paqueteentregable.rules.impl.PaqueteEntregableDisponibleRuleImpl;
import com.arquisoft.shared.util.UtilUUID;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DescargarPaqueteEntregableValidatorImpl implements DescargarPaqueteEntregableValidator {

    private final EntregableProyectoGradoExisteRule entregableProyectoGradoExisteRule;
    private final ActorAccedePaqueteEntregableRule actorAccedePaqueteEntregableRule;
    private final PaqueteEntregableDisponibleRule paqueteEntregableDisponibleRule;

    public DescargarPaqueteEntregableValidatorImpl() {
        this.entregableProyectoGradoExisteRule = new EntregableProyectoGradoExisteRuleImpl();
        this.actorAccedePaqueteEntregableRule = new ActorAccedePaqueteEntregableRuleImpl();
        this.paqueteEntregableDisponibleRule = new PaqueteEntregableDisponibleRuleImpl();
    }

    @Override
    public void validar(DescargaPaqueteEntregableCriteria criteria, UUID asesor, boolean participa,
                        PaqueteEntregableDomain paquete) {
        entregableProyectoGradoExisteRule.validar(new ExistenciaEntregableProyectoGrado(
                criteria.entregableProyectoGrado(), !UtilUUID.esPorDefecto(asesor)));
        actorAccedePaqueteEntregableRule.validar(new AccesoPaqueteEntregable(
                criteria.entregableProyectoGrado(), criteria.actor(), criteria.comoAsesor(),
                criteria.comoEstudiante(), asesor, participa));
        paqueteEntregableDisponibleRule.validar(new DisponibilidadPaqueteEntregable(
                criteria.entregableProyectoGrado(), !paquete.esVacio()));
    }
}
