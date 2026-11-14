package com.arquisoft.entregables.application.entregableproyectogrado.command.validator.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.command.validator.ConfirmarGeneracionEntregableProyectoGradoValidator;
import com.arquisoft.entregables.domain.paqueteentregable.model.VigenciaPaqueteArmado;
import com.arquisoft.entregables.domain.paqueteentregable.rules.PaqueteArmadoVigenteRule;
import com.arquisoft.entregables.domain.paqueteentregable.rules.impl.PaqueteArmadoVigenteRuleImpl;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
public class ConfirmarGeneracionEntregableProyectoGradoValidatorImpl
        implements ConfirmarGeneracionEntregableProyectoGradoValidator {

    private final PaqueteArmadoVigenteRule paqueteArmadoVigenteRule;

    public ConfirmarGeneracionEntregableProyectoGradoValidatorImpl() {
        this.paqueteArmadoVigenteRule = new PaqueteArmadoVigenteRuleImpl();
    }

    @Override
    public void validar(ProyectoGradoDomain proyecto, int versionEmpaquetada, int versionActual,
                        Set<UUID> versionesEmpaquetadas, Set<UUID> versionesActuales) {
        paqueteArmadoVigenteRule.validar(new VigenciaPaqueteArmado(
                proyecto.getId(), proyecto.getNombre(), versionEmpaquetada, versionActual,
                versionesEmpaquetadas, versionesActuales));
    }
}
