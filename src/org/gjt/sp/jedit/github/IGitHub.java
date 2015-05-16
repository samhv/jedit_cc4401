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

interface IGitHub {
	/*authenticate git hub account*/
	public void authenticate(String user,String password);
	/*clone a repository from remote_url to path*/
	public void clone(String remote_url,String path) throws GitAPIException;
	/*commit on path with a comment*/
	public void commit(String path, String comment) throws IOException, NoFilepatternException, NoHeadException, NoMessageException, ConcurrentRefUpdateException, JGitInternalException, WrongRepositoryStateException, GitAPIException;
	/*push from path to remote*/
	public void push(String path, String remote) throws JGitInternalException, InvalidRemoteException, IOException, GitAPIException;
	void commit(String comment) throws IOException, JGitInternalException,
			GitAPIException;
	void push(String path) throws JGitInternalException, IOException,
			GitAPIException;
	void push() throws JGitInternalException, IOException, GitAPIException;
}
