package com.ad;

public class Person {
	protected String FirstName;
	protected String LastName;
	protected String DisplayName;
	protected String Structure;
	protected String[] UserName;
	protected String Email;
	protected String TelephoneNumber;
	protected String PersonNumber;
	protected String Department;
	protected String Job;
	protected String Description;
	protected String Company;
	protected String Manager;
	protected String Address;
	protected String RG;
	protected String CPF;
	protected String OfficeState;
	protected String OfficeCity;
	protected String Function;
	protected String BirthDate;
	protected String LocationCode;

	public Person(String firstName, String lastName, String displayName, String structure, String[] userName,
			String email, String telephoneNumber, String personNumber, String department, String job, String description,
			String company, String manager, String address, String rg, String cpf, String officeState,
			String officeCity, String function, String birthDate, String locationCode) {
		super();
		FirstName = firstName;
		LastName = lastName;
		DisplayName = displayName;
		Structure = structure;
		UserName = userName;
		Email = email;
		TelephoneNumber = telephoneNumber;
		PersonNumber = personNumber;
		Department = department;
		Job = job;
		Description = description;
		Company = company;
		Manager = manager;
		Address = address;
		RG = rg;
		CPF = cpf;
		OfficeState = officeState;
		OfficeCity = officeCity;
		Function = function;
		BirthDate = birthDate;
		LocationCode = locationCode;
	}
	
	public Person(String firstName, String lastName, String displayName, String structure, String userName,
			String email, String telephoneNumber, String personNumber, String department, String job, String description,
			String company, String manager, String address, String rg, String cpf, String officeState,
			String officeCity, String function, String birthDate, String locationCode) {
		super();
		FirstName = firstName;
		LastName = lastName;
		DisplayName = displayName;
		Structure = structure;
		UserName = new String[]{userName};
		Email = email;
		TelephoneNumber = telephoneNumber;
		PersonNumber = personNumber;
		Department = department;
		Job = job;
		Description = description;
		Company = company;
		Manager = manager;
		Address = address;
		RG = rg;
		CPF = cpf;
		OfficeState = officeState;
		OfficeCity = officeCity;
		Function = function;
		BirthDate = birthDate;
		LocationCode = locationCode;
	}
	
	public Person() {
		super();
		FirstName = "";
		LastName = "";
		DisplayName = "";
		Structure = "";
		UserName = new String[]{""};
		Email = "";
		TelephoneNumber = "";
		PersonNumber = "";
		Department = "";
		Job = "";
		Description = "";
		Company = "";
		Manager = "";
		Address = "";
		RG = "";
		CPF = "";
		OfficeState = "";
		OfficeCity = "";
		Function = "";
	}	

	@Override
	public String toString() {
		return "Person [FirstName=" + FirstName + ", LastName=" + LastName + ", DisplayName=" + DisplayName
				+ ", Structure=" + Structure + ", UserName=" + UserName[0] + ", Email=" + Email + ", Telefone=" + TelephoneNumber
				+ ", PersonNumber=" + PersonNumber + ", Department=" + Department + ", Job=" + Job + ", Description="
				+ Description + ", Company=" + Company + ", Manager=" + Manager + ", Address=" + Address + ", RG=" + RG
				+ ", CPF=" + CPF + ", OfficeState=" + OfficeState + ", OfficeCity=" + OfficeCity + ", Function="
				+ Function + ", BirthDate=" + BirthDate + ", LocationCode=" + LocationCode + "]";
	}
	
	

	public String getLocationCode() {
		return LocationCode;
	}

	public void setLocationCode(String locationCode) {
		LocationCode = locationCode;
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
	public String[] getUserNames() {
		return UserName;
	}
	public String getUserName() {
		return UserName[0];
	}
	public void setUserName(String userName) {
		UserName = new String[]{userName};
	}
	public void setUserName(String[] userName) {
		UserName = userName;
	}
	public String getEmail() {
		return Email;
	}
	public void setEmail(String email) {
		Email = email;
	}
	public String getTelephoneNumber() {
		return TelephoneNumber;
	}
	public void setTelephoneNumber(String telephoneNumber) {
		TelephoneNumber = telephoneNumber;
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

	public String getRG() {
		return RG;
	}

	public void setRG(String rG) {
		RG = rG;
	}

	public String getCPF() {
		return CPF;
	}

	public void setCPF(String cPF) {
		CPF = cPF;
	}

	public String getOfficeState() {
		return OfficeState;
	}

	public void setOfficeState(String officeState) {
		OfficeState = officeState;
	}

	public String getOfficeCity() {
		return OfficeCity;
	}

	public void setOfficeCity(String officeCity) {
		OfficeCity = officeCity;
	}

	public String getFunction() {
		return Function;
	}

	public void setFunction(String function) {
		Function = function;
	}

	public String getBirthDate() {
		return BirthDate;
	}

	public void setBirthDate(String birthDate) {
		BirthDate = birthDate;
	}	
	
}
