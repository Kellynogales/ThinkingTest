package model.user;

public class PostLoginUserRequest{
	private String email;
	private String password;

	public PostLoginUserRequest(){
	}

	public PostLoginUserRequest(String email, String password) {
		this.email = email;
		this.password = password;
	}

	public void setPassword(String password){
		this.password = password;
	}

	public String getPassword(){
		return password;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}
}
