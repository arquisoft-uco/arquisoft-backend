package com.arquisoft.solicitudes.application.remitente.command.validator.impl;

import com.arquisoft.solicitudes.application.remitente.command.validator.RegistrarRemitenteValidator;
import com.arquisoft.solicitudes.domain.remitente.RemitenteDomain;
import com.arquisoft.solicitudes.domain.remitente.model.ExistenciaRemitente;
import com.arquisoft.solicitudes.domain.remitente.rules.RemitenteExisteRule;
import com.arquisoft.solicitudes.domain.remitente.rules.impl.RemitenteExisteRuleImpl;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import org.springframework.stereotype.Component;

@Component
public class RegistrarRemitenteValidatorImpl implements RegistrarRemitenteValidator {

    private final RemitenteExisteRule remitenteExisteRule;

    public RegistrarRemitenteValidatorImpl() {
        this.remitenteExisteRule = new RemitenteExisteRuleImpl();
    }

    @Override
    public void validar(RemitenteDomain remitente, UsuarioDomain usuario) {
        remitenteExisteRule.validar(new ExistenciaRemitente(remitente.getUsuario(), usuario));
    }
}
