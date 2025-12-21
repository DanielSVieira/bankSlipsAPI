package com.bankslips.service.interfaces;

import java.util.concurrent.CompletableFuture;

public interface ApiService<T> {
	
	public CompletableFuture<T> syncAsync(String input);
	public void saveIfNotExists(T data);
	

}
