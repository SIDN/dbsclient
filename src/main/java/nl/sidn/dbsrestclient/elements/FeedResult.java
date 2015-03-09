/*
 * $Id: FeedResult.java $
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

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * A class that holds a single Feed result. Consisting of a domain name and its properties.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class FeedResult {
	//The detected Domain name
	@XmlElement(name="domainname")
	private String domainName;

	//The registration date of this domain name
	@XmlElement(name="registrationdate")
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date registrationDate;

	//The modification date of this domain name
	@XmlElement(name="modificationdate")
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date modificationDate;
	
	//The date that this result was detected as a potential thread by DBS
	@XmlElement(name="detectiondate")
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date detectionDate;

	//The status of the domain name
	@XmlElement(name="status")
	private DomainStateEnum domainState;
	
	//The keyword which triggered this result.
	@XmlElement(name="keyword")
	private String keyword;
	
	//The name of the registrant of this domain name
	@XmlElement(name="registrant")
	private String registrant;

	//The email address of the administrative contact of this domain name. 
	@XmlElement(name="admin-c")
	private String adminc;

	public String getDomainName() {
		return domainName;
	}

	public Date getRegistrationDate() {
		return (Date) registrationDate.clone();
	}

	public Date getModificationDate() {
		return (Date) modificationDate.clone();
	}

	public Date getDetectionDate() {
		return (Date) detectionDate.clone();
	}

	public DomainStateEnum getDomainState() {
		return domainState;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getRegistrant() {
		return registrant;
	}

	public String getAdminc() {
		return adminc;
	}
}
