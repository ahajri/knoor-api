package com.knoor.api.service;

import java.util.List;

import com.knoor.api.exception.BusinessException;
import com.knoor.api.model.db.UserModel;

public interface IDBService<T> {
	
	public T insert(T model) throws BusinessException;
		
	public void delete(T model) throws BusinessException;
	
	public List<T> searchByExample(T example) throws BusinessException;

	public UserModel createOrUpdate(UserModel model) throws BusinessException;
}
