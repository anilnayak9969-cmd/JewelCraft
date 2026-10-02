package com.example.JewelCraft.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.JewelCraft.Entites.Role;
import com.example.JewelCraft.Enum.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long> {

	Optional<Role> findByName(RoleName name);
}
