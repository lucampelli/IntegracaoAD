package com.ad;

public class Person {
	protected String FirstName;
	protected String LastName;
	protected String DisplayName;
	protected String Structure;
	protected String UserName;
	protected String Email;
	protected String Telefone;
	protected String PersonNumber;
	protected String Department;
	protected String Job;
	protected String Description;
	protected String Company;
	protected String Manager;
	protected String Address;
	
	public Person() {
		FirstName = "";
		LastName = "";
		DisplayName = "";
		Structure = "";
		UserName = "";
		Email = "";
		Telefone = "";
		PersonNumber = "";
		Department = "";
		Job = "";
		Description = "";
		Company = "";
		Manager = "";
		Address = "";
	}
	
	public Person(String firstName, String lastName, String displayName, String structure, String userName,
			String email, String telefone, String personNumber, String department, String titleCargo,
			String description, String company, String manager, String address) {
		FirstName = firstName;
		LastName = lastName;
		DisplayName = displayName;
		Structure = structure;
		UserName = userName;
		Email = email;
		Telefone = telefone;
		PersonNumber = personNumber;
		Department = department;
		Job = titleCargo;
		Description = description;
		Company = company;
		Manager = manager;
		Address = address;
	}
	
	public String getFullName() {
		return FirstName + " " + LastName;
	}
	
	public String getFirstName() {
		return FirstName;
	}
	public void setFirstName(String firstName) {
		FirstName = firstName;
	}
	public String getLastName() {
		return LastName;
	}
	public void setLastName(String lastName) {
		LastName = lastName;
	}
	public String getDisplayName() {
		return DisplayName;
	}
	public void setDisplayName(String displayName) {
		DisplayName = displayName;
	}
	public String getStructure() {
		return Structure;
	}
	public void setStructure(String structure) {
		Structure = structure;
	}
	@Override
	public String toString() {
		return "Person [FirstName=" + FirstName + ", LastName=" + LastName + ", DisplayName=" + DisplayName
				+ ", Structure=" + Structure + ", UserName=" + UserName + ", Email=" + Email + ", Telefone=" + Telefone
				+ ", PersonNumber=" + PersonNumber + ", Department=" + Department + ", Job=" + Job + ", Description="
				+ Description + ", Company=" + Company + ", Manager=" + Manager + ", Address=" + Address + "]";
	}

	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	public String getEmail() {
		return Email;
	}
	public void setEmail(String email) {
		Email = email;
	}
	public String getTelefone() {
		return Telefone;
	}
	public void setTelefone(String telefone) {
		Telefone = telefone;
	}
	public String getPersonNumber() {
		return PersonNumber;
	}
	public void setPersonNumber(String personNumber) {
		PersonNumber = personNumber;
	}
	public String getDepartment() {
		return Department;
	}
	public void setDepartment(String department) {
		Department = department;
	}
	public String getJob() {
		return Job;
	}
	public void setJob(String job) {
		Job = job;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public String getCompany() {
		return Company;
	}
	public void setCompany(String company) {
		Company = company;
	}
	public String getManager() {
		return Manager;
	}
	public void setManager(String manager) {
		Manager = manager;
	}
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	
}
