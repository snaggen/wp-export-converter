package com.github.snaggen;

import java.io.OutputStream;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.xml.sax.InputSource;

public class WpExportMarshaller {
	final private JAXBContext context;

	public WpExportMarshaller() {
		try {
			context = JAXBContext.newInstance(WpExport.class);
		} catch (JAXBException e) {
			throw new RuntimeException("JAXBException, this shouldn't happen", e);
		}
	}

	public WpExport unmarshal(String xml) throws MarshallingException {
		try {
			// parse the XML and return an instance of the BxfMessage class
			final InputSource src = 
					new InputSource(new StringReader(xml)); 
			final WpExport message = (WpExport) context.createUnmarshaller().unmarshal(src);
			return message;
		} catch(JAXBException e) {
			throw new MarshallingException("Failed to unmarshal Wordpress export file", e); 
		}
	}

	public void marshal(WpExport wpExport, OutputStream os) throws MarshallingException {
		try {
			// produce XML
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty("jaxb.formatted.output", true);
			marshaller.marshal(wpExport, os);
		} catch (JAXBException e) {
			String cause = e.getCause().toString();
			throw new MarshallingException("Failed to marshal Msg; " + "cause="+cause , e); 
		}
	}
}
