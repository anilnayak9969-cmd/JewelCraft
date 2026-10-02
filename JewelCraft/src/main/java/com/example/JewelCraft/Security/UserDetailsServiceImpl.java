package com.example.JewelCraft.Security;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.JewelCraft.Repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	@Transactional(readOnly = true)
	public UserDetailsImpl loadUserByUsername(String email) throws UsernameNotFoundException {
		return userRepository.findByEmailIgnoreCase(email)
				.map(UserDetailsImpl::new)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
	}
}
