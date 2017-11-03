package com.github.snaggen;

import java.net.URI;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement(name="rss")
@XmlAccessorType(XmlAccessType.FIELD)
public class WpExport {
    @XmlPath("channel/title/text()")
    private String blogTitle;

    @XmlPath("channel/link/text()")
    private URI blogUri;

    @XmlPath("channel/item")
    private List<WpExportItem> items;
    
    @XmlPath("channel/wp:author")
    private List<Author> authors;
    
	public String getBlogTitle() {
		return blogTitle;
	}

	public void setBlogTitle(String blogTitle) {
		this.blogTitle = blogTitle;
	}

	public URI getBlogUri() {
		return blogUri;
	}

	public void setBlogUri(URI blogUri) {
		this.blogUri = blogUri;
	}

	public List<WpExportItem> getItems() {
		return items;
	}

	public void setItems(List<WpExportItem> items) {
		this.items = items;
	}

	public Author getAuthor(String login) {
		if (login == null || getAuthors() == null)
			return null;
		
		for(Author author: getAuthors()) {
			if (login.equals(author.getLogin()))
				return author;
		}
		return null;
	}

	public List<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}

	public static class WpExportItem {
		@XmlPath("title/text()")
		private String title;

		@XmlPath("wp:post_id/text()")
		private Integer id;

		@XmlPath("wp:post_parent/text()")
		private Integer parentId;

		@XmlPath("content:encoded/text()")
		private String content;
		
		@XmlPath("wp:attachment_url/text()")
		private URL attachmentUri;
		
		@XmlPath("dc:creator/text()")
		private String author;
		
		@XmlPath("pubDate/text()")
		@XmlJavaTypeAdapter(OffsetDateTimeAdapter.class)
		private OffsetDateTime published;
		
		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public Integer getParentId() {
			return parentId;
		}

		public void setParentId(Integer parentId) {
			this.parentId = parentId;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public OffsetDateTime getPublished() {
			return published;
		}

		public void setPublished(OffsetDateTime published) {
			this.published = published;
		}

		public String getAuthor() {
			return author;
		}

		public void setAuthor(String author) {
			this.author = author;
		}

		public URL getAttachmentUri() {
			return attachmentUri;
		}

		public void setAttachmentUri(URL attachmentUri) {
			this.attachmentUri = attachmentUri;
		}
		
	}
	
	public static class Author {
		@XmlPath("wp:author_login/text()")
		private String login;
		
		@XmlPath("wp:author_email/text()")
		private String email;
		
		@XmlPath("wp:author_display_name/text()")
		private String displayName;

		public String getLogin() {
			return login;
		}

		public void setLogin(String login) {
			this.login = login;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}
	}
}
