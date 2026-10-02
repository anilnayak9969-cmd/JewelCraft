package com.example.JewelCraft.Services;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

	/** Saves an image and returns its public URL path, e.g. /uploads/products/abc.jpg */
	String storeProductImage(MultipartFile file);

	/** Deletes the file behind a URL returned by storeProductImage; ignores external URLs. */
	void deleteIfLocal(String url);
}
