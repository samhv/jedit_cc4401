package org.gjt.sp.jedit.github;

import java.io.IOException;

import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;

public class StateGitHubNoAuthenticated extends StateGitHub {

	public StateGitHubNoAuthenticated(GitHub git)
	{
		super(git);
	}
	
	@Override
	public void clone(String remote_url, String path) {
		System.out.println("authenticate pls");
	}

	@Override
	public void commit(String path, String comment) throws IOException,
			NoFilepatternException, NoHeadException, NoMessageException,
			ConcurrentRefUpdateException, JGitInternalException,
			WrongRepositoryStateException {
		System.out.println("authenticate pls");
	}

	@Override
	public void push(String path, String remote) throws JGitInternalException,
			InvalidRemoteException, IOException {
		System.out.println("authenticate pls");
	}

}
