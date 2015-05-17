package core;

public class GitHubConfig {
	private String _repoUrl;
	private String _gitHubUser;	
	String _gitHubPassword;
	
	public GitHubConfig(String repoUrl){
		this(repoUrl, null, null);
	}
	public GitHubConfig(String repoUrl,String gitHubUser,String gitHubPass){
		setRepoUrl(repoUrl);
		setGitHubUser(gitHubUser);
		_gitHubPassword = gitHubPass;
	}
	public String getRepoUrl() {
		return _repoUrl;
	}
	public void setRepoUrl(String repoUrl) {
		this._repoUrl = repoUrl;
	}
	public String getGitHubUser() {
		return _gitHubUser;
	}
	public void setGitHubUser(String gitHubUser) {
		this._gitHubUser = gitHubUser;
	}
	public String getGitHubPass() {
		return _gitHubPassword;
	}
	public void setGitHubPass(String gitHubPass) {
		this._gitHubPassword = gitHubPass;
	}
	public String GetOwner(){
		if(_repoUrl==null)
			return null;
		String [] parts =  _repoUrl.split("/");
		return parts[parts.length-2];
	}
	public String getRepoName(){
		if(_repoUrl==null)
			return null;
		String [] parts =  _repoUrl.split("/");
		return parts[parts.length-1];
	} 
}