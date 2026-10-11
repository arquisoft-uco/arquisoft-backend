package com.arquisoft.solicitudes.application.destinatario.command.validator.impl;

import com.arquisoft.solicitudes.application.destinatario.command.validator.RegistrarDestinatarioValidator;
import com.arquisoft.solicitudes.domain.destinatario.DestinatarioDomain;
import com.arquisoft.solicitudes.domain.destinatario.model.ExistenciaDestinatario;
import com.arquisoft.solicitudes.domain.destinatario.rules.DestinatarioExisteRule;
import com.arquisoft.solicitudes.domain.destinatario.rules.impl.DestinatarioExisteRuleImpl;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import org.springframework.stereotype.Component;

@Component
public class RegistrarDestinatarioValidatorImpl implements RegistrarDestinatarioValidator {

    private final DestinatarioExisteRule destinatarioExisteRule;

    public RegistrarDestinatarioValidatorImpl() {
        this.destinatarioExisteRule = new DestinatarioExisteRuleImpl();
    }

    @Override
    public void validar(DestinatarioDomain destinatario, UsuarioDomain usuario) {
        destinatarioExisteRule.validar(new ExistenciaDestinatario(destinatario.getUsuario(), usuario));
    }
}
