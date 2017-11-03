package com.github.snaggen;

import java.io.File;
import java.io.FileInputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.text.Paragraph;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.snaggen.WpExport.Author;

@Parameters(separators = "=", commandDescription = "Deploy files/packages to Activiti")
public class CommandODF {
	@Parameter(names = { "-h", "--help"}, help = true)
	private boolean help;

	public boolean showHelp() {
		return help;
	}
	@Parameter(names = { "-v", "--verbose"}, description = "Verbose output", required = false)
	private final boolean verbose = false;

	@Parameter(description = "Export File", required = true)
	private List<String> files;

	public void perform() throws Exception {
		File file = new File(files.get(0));
		String outfile = "/tmp/output.odf";
		if (files.size() >= 2) {
			outfile = files.get(1);
		}
		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		fis.read(data);
		fis.close();

		String xml = new String(data, "UTF-8");
		
		WpExportMarshaller marshaller = new WpExportMarshaller();
		WpExport wpExport = marshaller.unmarshal(xml);
		
		System.out.println("Blogg name: " + wpExport.getBlogTitle());

        TextDocument doc = TextDocument.newTextDocument();
		for(WpExport.WpExportItem item: wpExport.getItems()) {
			if (item.getParentId() == 0) {

				Paragraph head = doc.addParagraph(item.getTitle());
				head.applyHeading(true, 1);
				
				Author author = wpExport.getAuthor(item.getAuthor());
				if (author != null) {
					Paragraph byline = doc.addParagraph("av " + author.getDisplayName() + " - " + WordUtils.capitalize(item.getPublished().format(DateTimeFormatter.ofPattern("cccc d MMMM YYYY HH.mm").withZone(ZoneId.systemDefault())))); // Söndag 3:e Juni 2017 12.30

					Font font = byline.getFont();
					font.setFontStyle(StyleTypeDefinitions.FontStyle.ITALIC);
					font.setSize(font.getSize()-2);
					byline.setFont(font);
//					System.out.println(byline.getTextContent());
				}
				doc.addParagraph("");
				
				String html = item.getContent();
				
				Document body = Jsoup.parseBodyFragment(html);
				formatParagraph(doc, body.getElementsByTag("body"));

				doc.addPageBreak();
			}
		}
		System.out.println("Writing output file " + outfile);
		doc.save(outfile);
	}

	private void formatParagraph(TextDocument doc, Elements elementsByTag) {
		if (doc == null || elementsByTag == null)
			return;
		WpNodeVisitor wpnv = new WpNodeVisitor(doc);
		for(Element elem: elementsByTag) {
			elem.traverse(wpnv);
		}
	}
	
	private static class WpNodeVisitor implements NodeVisitor {
		private TextDocument doc;

		public WpNodeVisitor(TextDocument doc) {
			this.doc = doc;
		}

		@Override
		public void head(Node node, int depth) {
			if (node instanceof TextNode) {
				addNodeTextToDoc((TextNode) node);
			}
		}

		private void addNodeTextToDoc(TextNode node) {
			if (doc == null || node == null)
				return;
			Paragraph para = doc.addParagraph(node.text());
			Node parent = node.parentNode();
			if (parent == null || parent.nodeName() == null) {
//				System.out.println(node.text());
				return;
			}
			if ("strong".equalsIgnoreCase(parent.nodeName())) {
				Font font = para.getFont();
				font.setFontStyle(StyleTypeDefinitions.FontStyle.BOLD);
				para.setFont(font);
//				System.out.println("*"+node.text()+"*");
			} else if ("em".equalsIgnoreCase(parent.nodeName())) {
				Font font = para.getFont();
				font.setFontStyle(StyleTypeDefinitions.FontStyle.ITALIC);
				para.setFont(font);
//				System.out.println("_"+node.text()+"_");
//			} else {
//				System.out.println(node.text());
			}
		}

		@Override
		public void tail(Node node, int depth) {
		}
		
	}

}
