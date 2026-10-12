package com.arquisoft.usuarios.application.usuario.command.validator.impl;

import com.arquisoft.usuarios.application.usuario.command.validator.CambiarEstadoUsuarioValidator;
import com.arquisoft.usuarios.domain.usuario.CambioEstadoUsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.model.ExistenciaUsuario;
import com.arquisoft.usuarios.domain.usuario.model.TransicionEstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.rules.EstadoUsuarioCambiaRule;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioExisteRule;
import com.arquisoft.usuarios.domain.usuario.rules.impl.EstadoUsuarioCambiaRuleImpl;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class CambiarEstadoUsuarioValidatorImpl implements CambiarEstadoUsuarioValidator {

    private final UsuarioExisteRule usuarioExisteRule;
    private final EstadoUsuarioCambiaRule estadoUsuarioCambiaRule;

    public CambiarEstadoUsuarioValidatorImpl() {
        this.usuarioExisteRule = new UsuarioExisteRuleImpl();
        this.estadoUsuarioCambiaRule = new EstadoUsuarioCambiaRuleImpl();
    }

    @Override
    public void validar(CambioEstadoUsuarioDomain cambio, UsuarioDomain encontrado) {
        usuarioExisteRule.validar(new ExistenciaUsuario(cambio.getUsuario(), encontrado));
        estadoUsuarioCambiaRule.validar(
                new TransicionEstadoUsuario(cambio.getUsuario(), encontrado.getEstado(), cambio.getEstado()));
    }
}
