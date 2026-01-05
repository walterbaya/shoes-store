package com.shoesstore.shoesstore.dto;

import java.util.Objects;

public class ScanRequest {
	private Long id;

	public ScanRequest() {
		
	}
	
	public ScanRequest(Long id) {
		super();
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScanRequest other = (ScanRequest) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "ScanRequest [id=" + id + "]";
	}
	
}
