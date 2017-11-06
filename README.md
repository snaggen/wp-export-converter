# Wordpress Export Converter

This tool will let you extract the posts and media from you wordpress blog, by using a Wordpress Export File. It extracts the posts and creates an ODF file with the result, so you then can open and edit the texts in LibreOffice. When you use it to fetch media files, it will download and store that in a simple directory structure. 

# Background

I created this tool for my own use, since I wanted to try to convert my existing blog in to a book using Blurb. Unfortunately most blog to book tools, will not let you format things as you like it. So using this tool it is possible to import the blog to LibreOffice, then edit and adjust the text and export it to RTF which then can be imported to BookWright (a tool used by Blurb). The media files could also be processed, and made in to photo collages to be used in the book. 

# Limitations

The actual parsing of the blog post content, is a little limited. Only simple strong and em tags are currently handelded. 

# Building

To build this just use maven.

```sh
mvn clean package
```

# Usage

This could be made prettier by using a simple wrapper script, but remember that I did this just for myself... and I'm lazy.

## Extracting texts
```sh
java -jar target/wp-export-converter-0.0.1-SNAPSHOT-jar-with-dependencies.jar toOdf -f ~/blog-export-file.xml /tmp/output.odf
```

## Fetching media
It seems that wordpress exports doesn't include links to the files for gallery with ids explicitly specified, like mosaik collages. ie [gallery ids="111,112,113" type="type="rectangular"]. However, they are included in the media export file. So to handle this you can send in an additional media xml file parameter. 
```sh
java -jar target/wp-export-converter-0.0.1-SNAPSHOT-jar-with-dependencies.jar fetchMedia -f ~/blog-export-file.xml -m ~/media-export.xml ~/BlogMediaFiles
```

## License

This project is released as open source software under the [GPL v3](https://www.gnu.org/licenses/gpl-3.0.html) license, see the [LICENSE](./LICENSE) file in the project root for the full license text.
