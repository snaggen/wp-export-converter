package com.github.snaggen;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.snaggen.WpExport.WpExportItem;

@Parameters(separators = "=", commandDescription = "Deploy files/packages to Activiti")
public class CommandFetchMedia {
	@Parameter(names = { "-h", "--help"}, help = true)
	private boolean help;

	public boolean showHelp() {
		return help;
	}
	@Parameter(names = { "-v", "--verbose"}, description = "Verbose output", required = false)
	private final boolean verbose = false;

	@Parameter(description = "Export File and base output folder", required = true)
	private List<String> files;

	public void perform() throws Exception {
		File file = new File(files.get(0));
		Path baseDir = Paths.get(".");
		if (files.size() >= 2) {
			baseDir = Paths.get(files.get(1));
		}
		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		fis.read(data);
		fis.close();

		String xml = new String(data, "UTF-8");
		
		WpExportMarshaller marshaller = new WpExportMarshaller();
		WpExport wpExport = marshaller.unmarshal(xml);
		
		System.out.println("Blogg name: " + wpExport.getBlogTitle());

        Map<Integer, WpExportItem> itemMap = wpExport.getItems().stream()
        		.collect(Collectors.toMap(WpExportItem::getId, p->p));
		for(WpExport.WpExportItem item: wpExport.getItems()) {
			String title = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			if (item.getAttachmentUri() != null) {
				if (item.getParentId() == 0) {
					title = item.getPublished().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "_" + item.getTitle().replaceAll("\\s", "_");
				} else {
					WpExportItem parent = itemMap.get(item.getParentId());
					if (parent != null) {
						title = parent.getPublished().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "_" + parent.getTitle().replaceAll("\\s", "_");
					}
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
					System.out.println("title " + title);
					System.out.println("baseDir " + baseDir);
					System.out.println("outFolder " + outFolder);
					System.out.println("filename " + filename);
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
