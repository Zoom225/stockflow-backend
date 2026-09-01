package com.stockflow.exception;

public class ProductDeletionNotAllowedException extends RuntimeException {

	public ProductDeletionNotAllowedException(String message) {
		super(message);
	}
}
