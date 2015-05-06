package org.gjt.sp.jedit.github;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;


public class GitHub implements IGitHub {
	
	private UsernamePasswordCredentialsProvider credential;
	private static IGitHub singleton;
	private StateGitHub state;
	
	private GitHub()
	{
		this.state = new StateGitHubNoAuthenticated(this);
	}
	
	private GitHub(String usr,String pass) {
		this.credential = new UsernamePasswordCredentialsProvider(usr,pass);
		this.state = new StateGitHubAuthenticated(this);
	}
	
	public static IGitHub getInstance()
	{
		if(singleton == null)
			singleton = new GitHub();
		return singleton;	
	}
	
	/*authenticate git hub account*/
	public static void auth(String usr,String pass)
	{
		singleton = new GitHub(usr,pass);
	}
	
	public void authenticate(String usr,String pass)
	{
		this.credential = new UsernamePasswordCredentialsProvider(usr,pass);
		this.state = new StateGitHubAuthenticated(this);
	}
	
	public void clone(String remote_url,String path) throws GitAPIException
	{
		state.clone(remote_url, path);
	}
	
	public void commit(String path, String comment) throws IOException, JGitInternalException, GitAPIException
	{
		state.commit(path, comment);
	}
	
	public void push(String path, String remote) throws JGitInternalException, IOException, GitAPIException
	{
		state.push(path, remote);
	}
	public void push(String path) throws JGitInternalException, IOException, GitAPIException
	{
		state.push(path,"origin");
	}

	public CredentialsProvider getCredential() {
		return credential;
	}
	
}
