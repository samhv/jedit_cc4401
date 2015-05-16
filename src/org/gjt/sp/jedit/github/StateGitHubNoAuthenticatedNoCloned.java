package org.gjt.sp.jedit.github;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;

public class StateGitHubNoAuthenticatedNoCloned extends StateGitHub{

	public StateGitHubNoAuthenticatedNoCloned(GitHub git)
	{
		super(git);
	}
	
	@Override
	public void clone(String remote_url, String path) throws GitAPIException {
		super.clone(remote_url,path);
		gitHub.setState(new StateGitHubNoAuthenticatedCloned(gitHub));
	}

	@Override
	public void commit(String path, String comment) throws IOException,
			NoFilepatternException, NoHeadException, NoMessageException,
			ConcurrentRefUpdateException, JGitInternalException,
			WrongRepositoryStateException, GitAPIException {
		
	}

	@Override
	public void push(String path, String remote) throws JGitInternalException,
			InvalidRemoteException, IOException, GitAPIException {

	}

	@Override
	public void authenticate() {
		gitHub.setState(new StateGitHubAuthenticatedNoCloned(gitHub));
	}

}
