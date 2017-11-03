@XmlSchema(
    xmlns={
        @XmlNs(prefix="excerpt", namespaceURI="http://wordpress.org/export/1.2/excerpt/"),
        @XmlNs(prefix="content", namespaceURI="http://purl.org/rss/1.0/modules/content/"),
        @XmlNs(prefix="wfw", namespaceURI="http://wellformedweb.org/CommentAPI/"),
        @XmlNs(prefix="dc", namespaceURI="http://purl.org/dc/elements/1.1/"),
        @XmlNs(prefix="wp", namespaceURI="http://wordpress.org/export/1.2/"),
    },
    elementFormDefault=XmlNsForm.UNQUALIFIED)
package com.github.snaggen;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;

