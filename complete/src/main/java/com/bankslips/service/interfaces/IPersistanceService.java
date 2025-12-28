package com.bankslips.service.interfaces;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;


public interface IPersistanceService <T> {

	public T create(T record, BindingResult bindingResult);
	public Page<T> list(Pageable pageable) ;
	public T show(UUID recordID);
	public T edit(UUID id, Consumer<T> extraUpdates);
	public void saveAll(List<T> records);
	
}
