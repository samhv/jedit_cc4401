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

public class StateGitHubNoAuthenticatedCloned extends StateGitHub {

	public StateGitHubNoAuthenticatedCloned(GitHub git)
	{
		super(git);
	}

	@Override
	public void push(String path, String remote) throws JGitInternalException,
			InvalidRemoteException, IOException {
		
	}

	@Override
	public void authenticate() {
		gitHub.setState(new StateGitHubAuthenticatedCloned(gitHub));
	}

}
