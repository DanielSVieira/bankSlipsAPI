package com.bankslips.service.interfaces;

import java.util.concurrent.CompletableFuture;

public interface IApiService<T> {
	
	public CompletableFuture<T> syncAsync(String input);
	public void saveIfNotExists(T data);
	

}
