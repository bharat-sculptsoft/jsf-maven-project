package com.ss.user.service;

import com.ss.common.exception.ServiceLayerException;

public interface UserService {

	boolean authenticate(String email,String password) throws ServiceLayerException;

	void logoutUser() throws ServiceLayerException;
}