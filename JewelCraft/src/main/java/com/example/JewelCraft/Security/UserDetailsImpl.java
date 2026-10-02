package com.example.JewelCraft.Security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.JewelCraft.Entites.User;
import com.example.JewelCraft.Enum.RoleName;

import lombok.Getter;

/** The logged-in user as Spring Security sees it. Available in controllers via @AuthenticationPrincipal. */
@Getter
public class UserDetailsImpl implements UserDetails {

	private final Long id;
	private final String email;
	private final String password;
	private final String fullName;
	private final RoleName role;
	private final boolean enabled;
	private final List<GrantedAuthority> authorities;

	public UserDetailsImpl(User user) {
		this.id = user.getId();
		this.email = user.getEmail();
		this.password = user.getPassword();
		this.fullName = user.getFullName();
		this.role = user.getRole().getName();
		this.enabled = user.isEnabled();
		this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
	}

	public boolean hasRole(RoleName... roles) {
		for (RoleName r : roles) {
			if (r == role) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}
