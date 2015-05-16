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

public class StateGitHubAuthenticatedCloned extends StateGitHub {

	public StateGitHubAuthenticatedCloned(GitHub git)
	{
		super(git);
	}
	
	@Override
	public void push(String path, String remote) throws JGitInternalException,
			InvalidRemoteException, IOException, GitAPIException {
		
		Git git = Git.open(new File(path));
		git.push().setRemote(remote).setCredentialsProvider(this.gitHub.getCredential()).call();
		
	}

	@Override
	public void authenticate() {}
	
}
