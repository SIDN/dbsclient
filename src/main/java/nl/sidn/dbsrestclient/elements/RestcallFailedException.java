/*
 * $Id: RestcallFailedException.java $
 *
 * Copyright (c) 2015, SIDN
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *  3. Neither the name of the copyright holder nor the names of its contributors
 *     may be used to endorse or promote products derived from this software without
 *     specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * 
 * @author: $Author: $
 * @version: $Revision:  $
 *  
 */
package nl.sidn.dbsrestclient.elements;

/**
 * Exception that is throwed then a Rest call to DBS fails.
 * Holds a message which explains the problem. 
 */
public class RestcallFailedException extends Exception {
	private static final long serialVersionUID = 1L;
	private final String message;
	
	public RestcallFailedException(Exception parentException) {
		super(parentException);
		this.message = parentException.getMessage();
	}

	public RestcallFailedException(String message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}