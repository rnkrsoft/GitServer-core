package com.rnkrsoft.gitserver.command;

import java.util.regex.Pattern;


import com.rnkrsoft.gitserver.GitServer;
import com.rnkrsoft.gitserver.exception.InvalidGitParameterException;
import com.rnkrsoft.gitserver.log.Logger;
import com.rnkrsoft.gitserver.log.LoggerFactory;

import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;


public class GitCommandFactory implements CommandFactory {
	Logger logger = LoggerFactory.getInstance();
	private final static Pattern pathPattern = Pattern.compile("^[\\w-\\.]+$");
	
	private final static String MSG_COMMAND_NOT_FOUND = "Command not found.\r\n";
	private GitServer gitServer;
	public GitCommandFactory(GitServer gitServer) {
		this.gitServer = gitServer;
	}

	@Override
	public Command createCommand(String command) {
		logger.debug("Process command: " + command);
		try {
			return processCommand(command);
		} catch (InvalidGitParameterException e) {
			logger.error("Invalid git parameter.", e);
			return new SendMessageCommand(MSG_COMMAND_NOT_FOUND, CommandConstants.CODE_ERROR);
		}
	}
	
	private Command processCommand(String command) throws InvalidGitParameterException {
		if(command == null || "".equals(command.trim())) {
			return null;
		}
		
		command = command.trim();
		
		String[] words = command.split(" ");
		if(words.length != 2) {
			throw new InvalidGitParameterException("Command not found: " + command);
		}
		
		if("git-upload-pack".equals(words[0]) || "upload-pack".equals(words[0])) {
			return new PullCommand(parseRepoPath(words[1]), gitServer);
		} else if ("git-receive-pack".equals(words[0]) || "receive-pack".equals(words[0])) {
			return new PushCommand(parseRepoPath(words[1]),gitServer);
		} else {
			throw new InvalidGitParameterException("Command not found: " + command);
		}
	}
	
	private String parseRepoPath(String path) throws InvalidGitParameterException {
		path = path.replace("'", "").replace("/", "");
		if(pathPattern.matcher(path).find()) {
			logger.debug("Parsed repoPath: " + path);
			return path;
		}
		throw new InvalidGitParameterException("Git parameter is invalid: " + path);
	}

}
