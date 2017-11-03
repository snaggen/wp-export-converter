package com.github.snaggen;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	CommandMain cm = new CommandMain();
        JCommander jc = new JCommander(cm);    
        jc.setProgramName("wp-export-converter");
        CommandODF odf = new CommandODF();
        CommandFetchMedia fetchMedia = new CommandFetchMedia();
        
        jc.addCommand("toOdf", odf);
        jc.addCommand("fetchMedia", fetchMedia);
        
        /* TODO: Make nicer error on fail */ 
        jc.parse(args);

        try {
        	if(cm.help) {
        		jc.usage();
        		System.exit(0);
        	} else if ("toOdf".equals(jc.getParsedCommand())) {
        		if (odf.showHelp()) {
        			jc.usage("toOdf");
        			System.exit(0);
        		}
        		odf.perform();
        	} else if ("fetchMedia".equals(jc.getParsedCommand())) {
        		if (fetchMedia.showHelp()) {
        			jc.usage("fetchMedia");
        			System.exit(0);
        		}
        		fetchMedia.perform();
        	} else {
        		jc.usage();
        		System.exit(-1);
        	}
        } catch (Exception e) {
        	System.err.println("Failed to execute command!");
        	e.printStackTrace();
        }
    }
    
    
    private static class CommandMain {
    	@Parameter(names = { "-h", "--help"}, help = true)
    	private boolean help;
    }
}
