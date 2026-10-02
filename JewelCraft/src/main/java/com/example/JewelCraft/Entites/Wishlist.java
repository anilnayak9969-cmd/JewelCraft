package com.example.JewelCraft.Entites;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wishlist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wishlist {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Builder.Default
	@OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("addedAt DESC")
	private List<WishlistItem> items = new ArrayList<>();
}
