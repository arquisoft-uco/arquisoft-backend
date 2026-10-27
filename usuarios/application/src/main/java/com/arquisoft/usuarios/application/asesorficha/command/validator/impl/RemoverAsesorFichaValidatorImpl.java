package com.arquisoft.usuarios.application.asesorficha.command.validator.impl;

import com.arquisoft.usuarios.application.asesorficha.command.validator.RemoverAsesorFichaValidator;
import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.usuarios.domain.asesorficha.model.ExistenciaAsesorFicha;
import com.arquisoft.usuarios.domain.asesorficha.rules.AsesorFichaVigenteRule;
import com.arquisoft.usuarios.domain.asesorficha.rules.impl.AsesorFichaVigenteRuleImpl;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.model.ExistenciaUsuario;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioExisteRule;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RemoverAsesorFichaValidatorImpl implements RemoverAsesorFichaValidator {

    private final UsuarioExisteRule usuarioExisteRule;
    private final AsesorFichaVigenteRule asesorFichaVigenteRule;

    public RemoverAsesorFichaValidatorImpl() {
        this.usuarioExisteRule = new UsuarioExisteRuleImpl();
        this.asesorFichaVigenteRule = new AsesorFichaVigenteRuleImpl();
    }

    @Override
    public void validar(UUID usuario, UsuarioDomain usuarioDomain, AsesorFichaDomain asesorFicha) {
        usuarioExisteRule.validar(new ExistenciaUsuario(usuario, usuarioDomain));
        asesorFichaVigenteRule.validar(new ExistenciaAsesorFicha(usuario, usuarioDomain.getNombre(), asesorFicha));
    }
}
