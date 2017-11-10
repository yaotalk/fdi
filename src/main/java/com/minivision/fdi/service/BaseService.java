package com.minivision.fdi.service;

import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor={Exception.class})
public abstract class BaseService {

}
