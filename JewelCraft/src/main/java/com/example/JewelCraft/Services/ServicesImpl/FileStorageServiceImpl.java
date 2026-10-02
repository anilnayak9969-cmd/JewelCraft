package com.example.JewelCraft.Services.ServicesImpl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.JewelCraft.Exception.BadRequestException;
import com.example.JewelCraft.Services.FileStorageService;

/** Stores images on local disk. Only the URL is saved in MySQL, never the image bytes. */
@Service
public class FileStorageServiceImpl implements FileStorageService {

	private static final Logger log = LoggerFactory.getLogger(FileStorageServiceImpl.class);
	// SVG is excluded on purpose: it can carry scripts
	private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");
	private static final String URL_PREFIX = "/uploads/";

	private final Path root;

	public FileStorageServiceImpl(@Value("${app.upload.dir}") String uploadDir) {
		this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
	}

	@Override
	public String storeProductImage(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new BadRequestException("Please choose an image to upload");
		}
		String contentType = file.getContentType();
		String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
		ext = ext == null ? "" : ext.toLowerCase(Locale.ROOT);
		if (contentType == null || !contentType.startsWith("image/") || !ALLOWED_EXTENSIONS.contains(ext)) {
			throw new BadRequestException("Only JPG, PNG, WEBP or GIF images are allowed");
		}
		String filename = UUID.randomUUID() + "." + ext;
		try (InputStream in = file.getInputStream()) {
			Path dir = root.resolve("products");
			Files.createDirectories(dir);
			Files.copy(in, dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new IllegalStateException("Could not save image", e);
		}
		return URL_PREFIX + "products/" + filename;
	}

	@Override
	public void deleteIfLocal(String url) {
		if (url == null || !url.startsWith(URL_PREFIX)) {
			return;
		}
		Path target = root.resolve(url.substring(URL_PREFIX.length())).normalize();
		if (!target.startsWith(root)) {
			return; // never delete outside the upload folder
		}
		try {
			Files.deleteIfExists(target);
		} catch (IOException e) {
			log.warn("Could not delete {}: {}", target, e.getMessage());
		}
	}
}
