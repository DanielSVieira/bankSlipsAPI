package com.bankslips.service.interfaces;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;


public interface PersistanceService <T> {

	public T create(T record, BindingResult bindingResult);
	public Page<T> list(Pageable pageable) ;
	public T show(String recordID);
	public void bulkSave(List<T> records);
	public T edit(String id, Consumer<T> extraUpdates);
	public CompletableFuture<Map<String, Object>> bulkSaveAsync(List<T> records);
	
}
