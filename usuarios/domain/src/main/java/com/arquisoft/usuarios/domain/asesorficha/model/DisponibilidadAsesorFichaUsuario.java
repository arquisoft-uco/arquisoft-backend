package com.arquisoft.usuarios.domain.asesorficha.model;

import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;

import java.util.UUID;

public record DisponibilidadAsesorFichaUsuario(UUID usuario, AsesorFichaDomain asesorFicha) {}
