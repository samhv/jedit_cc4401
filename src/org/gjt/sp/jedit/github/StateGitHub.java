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

public abstract class StateGitHub implements IStateGitHub{
	
	protected GitHub gitHub;
	
	protected StateGitHub(GitHub gitHub)
	{
		this.gitHub = gitHub;
	}
	
	@Override
	public void clone(String remote_url, String path) throws GitAPIException {
		File localPath = new File(path);
		Git result = Git.cloneRepository()
                .setURI(remote_url)
                .setDirectory(localPath)
                .call();
	}
	public void commit(String path, String comment) throws IOException, NoFilepatternException, NoHeadException, NoMessageException, ConcurrentRefUpdateException, JGitInternalException, WrongRepositoryStateException, GitAPIException
	{
        Git git = Git.open(new File(path));
        git.add().addFilepattern(".").call();
        git.commit().setMessage(comment).call();
	}
	public abstract void push(String path, String remote) throws JGitInternalException, InvalidRemoteException, IOException, GitAPIException;

	public abstract void authenticate();

}
