package com.arquisoft.usuarios.application.usuario.command.validator.impl;

import com.arquisoft.usuarios.application.usuario.command.validator.RegistrarUsuarioValidator;
import com.arquisoft.usuarios.domain.usuario.RegistroUsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.model.DisponibilidadContactoUsuario;
import com.arquisoft.usuarios.domain.usuario.model.DisponibilidadEmailUsuario;
import com.arquisoft.usuarios.domain.usuario.model.DisponibilidadIdentificadorUsuario;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioContactoUnicoRule;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioEmailUnicoRule;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioIdentificadorUnicoRule;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioContactoUnicoRuleImpl;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioEmailUnicoRuleImpl;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioIdentificadorUnicoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class RegistrarUsuarioValidatorImpl implements RegistrarUsuarioValidator {

    private final UsuarioIdentificadorUnicoRule usuarioIdentificadorUnicoRule;
    private final UsuarioEmailUnicoRule usuarioEmailUnicoRule;
    private final UsuarioContactoUnicoRule usuarioContactoUnicoRule;

    public RegistrarUsuarioValidatorImpl() {
        this.usuarioIdentificadorUnicoRule = new UsuarioIdentificadorUnicoRuleImpl();
        this.usuarioEmailUnicoRule = new UsuarioEmailUnicoRuleImpl();
        this.usuarioContactoUnicoRule = new UsuarioContactoUnicoRuleImpl();
    }

    @Override
    public void validar(RegistroUsuarioDomain registro, boolean identificadorYaExiste, boolean emailYaExiste,
                        boolean contactoYaExiste) {
        usuarioIdentificadorUnicoRule.validar(
                new DisponibilidadIdentificadorUsuario(registro.getIdentificador(), identificadorYaExiste));
        usuarioEmailUnicoRule.validar(
                new DisponibilidadEmailUsuario(registro.getEmail(), emailYaExiste));
        usuarioContactoUnicoRule.validar(
                new DisponibilidadContactoUsuario(registro.getContacto(), contactoYaExiste));
    }
}
