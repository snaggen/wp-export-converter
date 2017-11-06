package com.github.snaggen;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.snaggen.WpExport.WpExportItem;
import com.google.common.base.Splitter;

@Parameters(separators = "=", commandDescription = "Deploy files/packages to Activiti")
public class CommandFetchMedia {
	private final Comparator<WpExportItem> byPublishedTime = (c1,c2)->c1.getPublished().compareTo(c2.getPublished());

	@Parameter(names = { "-h", "--help"}, help = true)
	private boolean help;

	public boolean showHelp() {
		return help;
	}
	@Parameter(names = { "-v", "--verbose"}, description = "Verbose output", required = false)
	private final boolean verbose = false;

	@Parameter(names = { "-m", "--media-xml"}, description = "Additional media export xml file", required = false)
	private final String mediaExportXml = null;

	@Parameter(names = { "-f", "--export-xml"}, description = "Export xml file", required = true)
	private final String exportXmlFile = null;
	
	
	@Parameter(description = "Base output folder, defaults to current working directory", required = false)
	private List<String> files;

	public void perform() throws Exception {
		Path baseDir = Paths.get(".");
		if (files != null && files.size() >= 1) {
			baseDir = Paths.get(files.get(0));
		}
		WpExport wpExport = WpExport.fromFile(exportXmlFile);
		
		System.out.println("Blogg name: " + wpExport.getBlogTitle());
		
        Map<Integer, WpExportItem> itemMap = wpExport.getItems().stream()
        		.collect(Collectors.toMap(WpExportItem::getId, p->p));
        
        WpExport mediaExport = null;
        if (mediaExportXml != null) {
        	mediaExport = WpExport.fromFile(mediaExportXml);
        	mediaExport.getItems().stream()
        	.forEach(item->{ 
        		itemMap.putIfAbsent(item.getId(), item);
        	});
        }
		
		List<WpExportItem> items = new ArrayList<>(itemMap.values());
		
		List<Integer> keys = new ArrayList<>(itemMap.keySet());
		Collections.sort(keys);
		Integer largestId = keys.get(keys.size()-1);
		/* Fix item references for gallery with ids */
		for(WpExport.WpExportItem item: items) {
			String content = item.getContent();
			String pattern = "\\[\\s*gallery\\s*ids\\s*=\\s*\"\\s*([0-9,]*)\\s*\"[^\\[\\]]*\\]";
			Pattern r = Pattern.compile(pattern, Pattern.MULTILINE);

			Matcher m = r.matcher(content);
			while (m.find()) {
				List<String> ids = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(m.group(1));
				for(String idStr : ids) {
					try {
						Integer id = Integer.valueOf(idStr);
						WpExportItem mediaItem = itemMap.get(id);
						if (mediaItem != null) {
							if (mediaItem.getParentId() == 0) {
								mediaItem.setParentId(item.getId());
							} else if (mediaItem.getParentId() != item.getId()) {
								WpExportItem copy = mediaItem.copy();
								copy.setId(++largestId);
								mediaItem.setParentId(item.getId());
								itemMap.put(copy.getId(), copy);
							}
						}
					} catch (NumberFormatException e) {
						System.err.println("Invalid item id " + idStr);
						e.printStackTrace();
					}
				}
			}
		}

		items = new ArrayList<>(itemMap.values());
		items.sort(byPublishedTime);

		for(WpExport.WpExportItem item: items) {
			String title = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			if (item.getAttachmentUri() != null) {
				if (item.getParentId() == 0 && "post".equals(item.getType())) {
					title = item.getPublished().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "_" + item.getTitle().replaceAll("\\s", "_");
				} else if (item.getParentId() != 0) {
					WpExportItem parent = itemMap.get(item.getParentId());
					if (parent != null && "post".equals(parent.getType())) {
						title = parent.getPublished().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "_" + parent.getTitle().replaceAll("\\s", "_");
					} else {
						title = "Attachments";
					}
				} else {
						title = "Attachments";
				}
				title = title.replaceAll("[^A-Za-zÅÄÖåäö0-9_-]", "_");
				Path outFolder = baseDir.resolve(title);
				if (!outFolder.toFile().exists()) {
					outFolder.toFile().mkdirs();
				}
				String filename = Paths.get(item.getAttachmentUri().getPath()).getFileName().toString();
				Path outFile = outFolder.resolve(filename);
				if (outFile.toFile().exists())
					continue;
				try {
					System.out.println("Fetching " + item.getAttachmentUri());
					System.out.println("to file " + outFile);
					InputStream in = item.getAttachmentUri().openStream();
					Files.copy(in, outFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
