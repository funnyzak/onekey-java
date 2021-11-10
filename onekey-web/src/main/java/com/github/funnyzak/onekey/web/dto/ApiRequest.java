package com.github.funnyzak.onekey.web.dto;

import lombok.Data;

/**
 * 
 * @author silenceace@gmail.com
 *
 * @param <T>
 */
@Data
public class ApiRequest<T> {

	private T data;

}
