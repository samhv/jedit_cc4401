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

public interface IStateGitHub {
	public void clone(String remote_url,String path) throws GitAPIException;
	public void commit(String path, String comment) throws IOException, NoFilepatternException, NoHeadException, NoMessageException, ConcurrentRefUpdateException, JGitInternalException, WrongRepositoryStateException, GitAPIException;
	public void push(String path, String remote) throws JGitInternalException, InvalidRemoteException, IOException, GitAPIException;

	public void authenticate();
}
