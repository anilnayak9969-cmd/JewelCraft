package com.example.JewelCraft.Entites;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 80)
	private String name;

	@Column(length = 500)
	private String description;

	private String imageUrl;

	@Builder.Default
	private boolean active = true;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;
}
