package com.example.JewelCraft.Entites;

import com.example.JewelCraft.Enum.RoleName;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, unique = true, length = 30)
	private RoleName name;

	public Role(RoleName name) {
		this.name = name;
	}
}
