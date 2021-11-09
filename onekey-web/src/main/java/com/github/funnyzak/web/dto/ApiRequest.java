package com.github.funnyzak.web.dto;

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
