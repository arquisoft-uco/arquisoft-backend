package com.arquisoft.usuarios.domain.usuario.model;

import com.arquisoft.usuarios.domain.administrador.AdministradorDomain;
import com.arquisoft.usuarios.domain.asesor.AsesorDomain;
import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.usuarios.domain.bibliotecario.BibliotecarioDomain;
import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;
import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;
import com.arquisoft.usuarios.domain.representantecomite.RepresentanteComiteDomain;

import java.util.UUID;

public record RolesUsuario(UUID usuario, EstudianteDomain estudiante, AsesorDomain asesor,
                           AsesorFichaDomain asesorFicha, CoordinadorDomain coordinador,
                           RepresentanteComiteDomain representanteComite, AdministradorDomain administrador,
                           BibliotecarioDomain bibliotecario) {}
