package org.gjt.sp.jedit.github;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;


public class GitHub implements IGitHub {
	
	private static IGitHub singleton;
	private IStateGitHub state;
	
	private UsernamePasswordCredentialsProvider credential;
	private String localPath;
	
	private GitHub()
	{
		this.state = new StateGitHubNoAuthenticatedNoCloned(this);
	}
	
	public static IGitHub getInstance()
	{
		if(singleton == null)
			singleton = new GitHub();
		return singleton;	
	}
	
	@Override
	public void authenticate(String usr,String pass)
	{
		this.credential = new UsernamePasswordCredentialsProvider(usr,pass);
		state.authenticate();
	}
	
	@Override
	public void clone(String remote_url,String path) throws GitAPIException
	{
		this.localPath = path;
		state.clone(remote_url, path);
	}
	
	@Override
	public void commit(String path, String comment) throws IOException, JGitInternalException, GitAPIException
	{
		state.commit(path, comment);
	}
	
	@Override
	public void push(String path, String remote) throws JGitInternalException, IOException, GitAPIException
	{
		state.push(path, remote);
	}
	
	@Override
	public void commit(String comment) throws IOException, JGitInternalException, GitAPIException
	{
		this.commit(this.localPath, comment);
	}
	
	@Override
	public void push(String path) throws JGitInternalException, IOException, GitAPIException
	{
		this.push(path,"origin");
	}
	
	@Override
	public void push() throws JGitInternalException, IOException, GitAPIException
	{
		this.push(this.localPath, "origin");
	}
	
	public CredentialsProvider getCredential() {
		return credential;
	}

	public void setState(IStateGitHub state) {
		this.state=state;
	}
	
}
