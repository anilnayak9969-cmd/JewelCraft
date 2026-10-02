package com.example.JewelCraft.Services.ServicesImpl;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

/** Small helpers shared by the service implementations. */
final class ServiceUtils {

	static final int MAX_PAGE_SIZE = 50;

	private ServiceUtils() {
	}

	/** "Ring " -> "%ring%"; blank -> null (meaning: no filter). */
	static String likePattern(String search) {
		return StringUtils.hasText(search) ? "%" + search.trim().toLowerCase() + "%" : null;
	}

	static Pageable page(int page, int size) {
		return PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), MAX_PAGE_SIZE));
	}

	static Pageable page(int page, int size, Sort sort) {
		return PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), MAX_PAGE_SIZE), sort);
	}

	static String trimToNull(String value) {
		return StringUtils.hasText(value) ? value.trim() : null;
	}
}
