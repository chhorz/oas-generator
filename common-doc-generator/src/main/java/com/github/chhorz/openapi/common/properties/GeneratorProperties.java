package com.github.chhorz.openapi.common.properties;

import java.util.List;

public class GeneratorProperties {

	private InfoProperties info;
	private ContactProperties contact;
	private LicenseProperties license;
	private List<ServerProperties> servers;
	private ExternalDocsProperties externalDocs;
	private ParserProperties parser;

	public GeneratorProperties() {
		info = new InfoProperties();
		contact = new ContactProperties();
		license = new LicenseProperties();
		// servers =
		externalDocs = new ExternalDocsProperties();
		parser = new ParserProperties();
	}

	public InfoProperties getInfo() {
		return info;
	}

	public void setInfo(final InfoProperties info) {
		this.info = info;
	}

	public ContactProperties getContact() {
		return contact;
	}

	public void setContact(final ContactProperties contact) {
		this.contact = contact;
	}

	public LicenseProperties getLicense() {
		return license;
	}

	public void setLicense(final LicenseProperties license) {
		this.license = license;
	}

	public List<ServerProperties> getServers() {
		return servers;
	}

	public void setServers(final List<ServerProperties> servers) {
		this.servers = servers;
	}

	public ExternalDocsProperties getExternalDocs() {
		return externalDocs;
	}

	public void setExternalDocs(final ExternalDocsProperties externalDocs) {
		this.externalDocs = externalDocs;
	}

	public ParserProperties getParser() {
		return parser;
	}

	public void setParser(final ParserProperties parser) {
		this.parser = parser;
	}

}
