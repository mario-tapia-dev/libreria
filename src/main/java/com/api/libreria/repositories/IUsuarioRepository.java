package com.api.libreria.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.libreria.models.UsuarioModel;

public interface IUsuarioRepository extends JpaRepository<UsuarioModel, Long>{
    
}
