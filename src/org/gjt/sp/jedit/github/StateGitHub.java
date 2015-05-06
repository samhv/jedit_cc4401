package org.gjt.sp.jedit.github;

import java.io.IOException;

import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;

public abstract class StateGitHub{
	
	GitHub git;
	
	protected StateGitHub(GitHub git)
	{
		this.git = git;
	}
	
	public abstract void clone(String remote_url,String path) throws GitAPIException;
	public abstract void commit(String path, String comment) throws IOException, NoFilepatternException, NoHeadException, NoMessageException, ConcurrentRefUpdateException, JGitInternalException, WrongRepositoryStateException, GitAPIException;
	public abstract void push(String path, String remote) throws JGitInternalException, InvalidRemoteException, IOException, GitAPIException;
}
