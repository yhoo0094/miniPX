package com.mpx.minipx.dto.market;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

//이미지 정보를 담는 DTO
public class ItemImage {
	private final Resource resource;
	private final MediaType mediaType;
	private final String filename;

	public ItemImage(Resource resource, MediaType mediaType, String filename) {
		this.resource = resource;
		this.mediaType = mediaType;
		this.filename = filename;
	}

	public Resource getResource() {
		return resource;
	}

	public MediaType getMediaType() {
		return mediaType;
	}

	public String getFilename() {
		return filename;
	}
}