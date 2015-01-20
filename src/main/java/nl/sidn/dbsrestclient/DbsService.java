/*
 * $Id: DbsService.java $
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
package nl.sidn.dbsrestclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

import nl.sidn.dbsrestclient.elements.FeedResponse;
import nl.sidn.dbsrestclient.elements.RestcallFailedException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import com.sun.jersey.core.util.Base64;

/**
 * DBS Service 
 * This service class connects to the REST interface of DBS.
 * The response will be marshalled to a FeedResponse object.
 */
public class DbsService {
	private static final String UTF8 = "UTF-8";
	private static final String BASIC_AUTH = "Basic";
	private static final Logger LOGGER = LoggerFactory.getLogger(DbsService.class);
	private static final int HTTP200 = 200;
	
	//The username for DBS
	private String username;
	//The password for DBS
	private String password;
	//The URL to DBS
	private String url;
	//In the communication to DBS, use XML or JSON?
	private MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
	
	/**
	 * Calls DBS REST interface
	 * @param fromdate Only return results that have been detected after this date. If null, returns all results.
	 * @return FeedResponse object, containing the DBS response.
	 * @throws RestcallFailedException If anything goes wrong, an RestcallFailedException will be thrown.
	 */
	public FeedResponse callDBSService(Date fromdate) throws RestcallFailedException 
	{
		//Only Json or Xml media type is allowed.
		assert MediaType.APPLICATION_JSON_TYPE.equals(mediaType) || MediaType.APPLICATION_XML_TYPE.equals(mediaType);
		
		// create closable HTTP Client
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();

		try {
			// Create new getRequest with below mentioned URL
			HttpGet getRequest = new HttpGet(url + getFromDateUrl(fromdate));
			 
			// Add additional header to request which accepts application/json or application/xml data
			getRequest.addHeader("accept", mediaType.toString());
			
			// Add authorization header to request
			getRequest.addHeader("Authorization", getAuthorisation());
			 
			// Execute your request and catch response
			HttpResponse response = httpClient.execute(getRequest);
			 
			// Check for HTTP response code: 200 = success
			if (response.getStatusLine().getStatusCode() != HTTP200) {
				throw new RestcallFailedException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			}
			 
			// Get the response
			return unmarshal(response.getEntity().getContent());
		} catch (ClientProtocolException exception) {
			LOGGER.error(exception.getMessage());
			throw new RestcallFailedException(exception);
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage());
			throw new RestcallFailedException(ioException);	
		} finally {
			try {
				httpClient.close();
			} catch (IOException ioException) {
				LOGGER.error(ioException.getMessage());
				throw new RestcallFailedException(ioException);
			}
		}		
	}
	
	/**
	 * Converts a Date object to a String formatted date which can be appended to the URL as parameter 'fromDate'.
	 * @param fromDate The date which has to be converted
	 * @return String in the format that can be appended to the URL.
	 */
	private String getFromDateUrl(Date fromDate) {
		if (fromDate == null) {
			return "";
		}
		else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(fromDate);
			
			String dateUrl = String.valueOf(cal.get(Calendar.YEAR)) + String.format("%02d", cal.get(Calendar.MONTH) + 1) + String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
			
			return "/" + dateUrl;
		}
	}

	/**
	 * Encodes username and password in Base64 encoding so it can be added to header as the Authorization parameter
	 * @return Encoded authorization string
	 * @throws UnsupportedEncodingException
	 */
	private String getAuthorisation() throws UnsupportedEncodingException {
		return BASIC_AUTH + " " + new String(Base64.encode(username + ":" + password), UTF8);
	}
	
	/**
	 * Unmarshals an input stream (json or xml format) to a FeedResponse object.
	 * @param inputStream
	 * @return
	 * @throws RestcallFailedException
	 */
	private FeedResponse unmarshal(InputStream inputStream) throws RestcallFailedException {
		try {
			JAXBContext jc = JAXBContext.newInstance(FeedResponse.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			unmarshaller.setProperty("eclipselink.media-type", mediaType.toString());
	        unmarshaller.setProperty("eclipselink.json.include-root", false);
	        
	        StreamSource source = new StreamSource(inputStream);
	        FeedResponse feedResponse = unmarshaller.unmarshal(source, FeedResponse.class).getValue();
			
	        return feedResponse;
		} catch (JAXBException e) {
			LOGGER.error(e.getMessage());
			throw new RestcallFailedException(e);		
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public MediaType getMediaType() {
		return mediaType;
	}

	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}
}
