package com.ad;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.DefaultTlsDirContextAuthenticationStrategy;
import org.springframework.ldap.core.support.DirContextAuthenticationStrategy;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HomeResource {
	
	
	@Autowired
    private Environment env;
	
	LdapTemplate ldapTemplate = null;
	String Base = "DC=GAFISACT,DC=COM,DC=BR";

	
	public LdapTemplate setLdapTemplate() {
		
		System.out.println("Setting Certificate Trust Store");
		System.setProperty("javax.debug", "ssl");
        System.setProperty("javax.net.ssl.trustStore", "/app/cacerts.jks");
        System.setProperty("javax.net.ssl.trustStorePassword","changeit");
        System.out.println(System.getProperty("javax.net.ssl.trustStore"));
		LdapContextSource contextSource = new LdapContextSource();
		
        //contextSource.setUrls(new String[]{"ldaps://SSPDC-01.gafisact.com.br:636","ldaps://SSPDC-02.gafisact.com.br:636"});

		contextSource.setUrl("ldap://localhost:8389");
        contextSource.setBase("DC=GAFISACT,DC=COM,DC=BR");
        
        //contextSource.setUserDn(env.getProperty("spring.ldap.username") + ","+ env.getProperty("spring.ldap.base"));
        //contextSource.setPassword(env.getProperty("spring.ldap.password"));
        
        contextSource.afterPropertiesSet();
        LdapTemplate ldapTemplate = new LdapTemplate(contextSource);

        try {
        	System.out.println("Authentication");
        	System.out.println(env.getProperty("spring.ldap.username") + ","+ env.getProperty("spring.ldap.base"));

            contextSource.getContext(env.getProperty("spring.ldap.username") + ","+ env.getProperty("spring.ldap.base"), env.getProperty("spring.ldap.password"));
        	
            ldapTemplate.afterPropertiesSet();
                       
        } catch (Exception e) {
            System.out.print("Failed to initialize ldaptemplate "); e.printStackTrace();
            System.exit(-1);
        }
        
        return ldapTemplate;
	}
	
	public Name buildPersonDn(Person p) {
		System.out.println(p.getStructure());

		String[] struc = p.getStructure().split(",");
		LdapNameBuilder b = LdapNameBuilder.newInstance();
		System.out.println(struc.length);
		System.out.println(struc[0]);	
		for(int i = struc.length -1; i >=0; i--) {
			System.out.println(struc[i]);

			String[] t = struc[i].split("=");
			b.add(t[0],t[1]);
		}
		b.add("CN",p.getFullName());
		
		return (Name)b.build();
	}
	
	public Name buildPersonDn(String structure, String fullName) {
		System.out.println(structure);
		System.out.println(fullName);

		String[] struc = structure.split(",");
		LdapNameBuilder b = LdapNameBuilder.newInstance();
		System.out.println(struc.length);
		System.out.println(struc[0]);	
		for(int i = struc.length -1; i >=0; i--) {
			System.out.println(struc[i]);

			String[] t = struc[i].split("=");
			b.add(t[0],t[1]);
		}
		b.add("CN",fullName);
		
		return (Name)b.build();
	}
	
	public Name buildDisabledDn(Person p) {
		String[] struc = p.getStructure().split(",");
		LdapNameBuilder b = LdapNameBuilder.newInstance();
		
		b.add("OU","Desabilitado");
		b.add("CN",p.getFullName());
		
		return (Name)b.build();
	}

	@RequestMapping("/")
	public ResponseEntity test() {
		System.out.println("TEST");
		return new ResponseEntity(HttpStatus.OK);
	}

	@GetMapping("/employee")
	public ResponseEntity<List<Person>> queryall() {
		LdapQuery query = query().base("").attributes("sn","distinguishedName","sAMAccountName","userPassword","givenName","displayName","telephoneNumber","department","company","mail","manager","streetAddress","description","title","l","st","info","wWWHomePage","postOfficeBox","pager","physicalDeliveryOfficeName").where("objectclass").is("person");
		AttributesMapper<Person> mapper = new AttributesMapper<Person>() {
			
			public Person mapFromAttributes(Attributes attrs) throws javax.naming.NamingException {
				NamingEnumeration<? extends Attribute> attributes = attrs.getAll();
				//System.out.println(attrs.size());
				Person ret = new Person();
				
				ArrayList<String> s = new ArrayList<String> (Arrays.asList((null != attrs.get("distinguishedName") ? (String) attrs.get("distinguishedName").get() : "").split(",")));
				//System.out.println(s);
				if(s.size() > 1) {
					s.remove(0);
					s.remove(s.size()-1);
					s.remove(s.size()-1);
					s.remove(s.size()-1);
				}

				ret.setStructure(String.join(",",(String[])s.toArray(new String[0])));
				
				ret.setFirstName(null != attrs.get("givenName") ? (String) attrs.get("givenName").get() : "");
				ret.setLastName(null != attrs.get("sn") ? (String) attrs.get("sn").get() : "");
				ret.setDisplayName(null != attrs.get("displayName") ? (String) attrs.get("displayName").get() : "");

				ret.setTelephoneNumber(null != attrs.get("telephoneNumber") ? (String) attrs.get("telephoneNumber").get() : "");
				ret.setDepartment(null != attrs.get("department") ? (String) attrs.get("department").get() : "");
				ret.setCompany(null != attrs.get("company") ? (String) attrs.get("company").get() : "");
				
				ret.setUserName(null != attrs.get("sAMAccountName") ? (String) attrs.get("sAMAccountName").get() : "");
				ret.setEmail(null != attrs.get("mail") ? (String) attrs.get("mail").get() : "");
				ret.setManager(null != attrs.get("manager") ? (String) attrs.get("manager").get() : "");

				ret.setAddress(null != attrs.get("streetAddress") ? (String) attrs.get("streetAddress").get() : "");
				ret.setDescription(null != attrs.get("description") ? (String) attrs.get("description").get() : "");
				ret.setJob(null != attrs.get("info") ? (String) attrs.get("info").get() : "");
				
				ret.setOfficeCity(null != attrs.get("l") ? (String) attrs.get("l").get() : "");
				ret.setOfficeState(null != attrs.get("st") ? (String) attrs.get("st").get() : "");
				ret.setCPF(null != attrs.get("postOfficeBox") ? (String) attrs.get("postOfficeBox").get() : "");
				
				ret.setRG(null != attrs.get("wWWHomePage") ? (String) attrs.get("wWWHomePage").get() : "");
				ret.setBirthDate(null != attrs.get("pager") ? (String) attrs.get("pager").get() : "");
				ret.setFunction(null != attrs.get("title") ? (String) attrs.get("title").get() : "");
				
				ret.setLocationCode(null != attrs.get("physicalDeliveryOfficeName") ? (String) attrs.get("physicalDeliveryOfficeName").get() : "");
				
				return ret;
			}
		};
		
		if(ldapTemplate == null) {
			ldapTemplate = setLdapTemplate();
		}
		return new ResponseEntity<List<Person>>(ldapTemplate.search(query, mapper),HttpStatus.OK);
	}
	
	@GetMapping("/employee/{username}")
	public ResponseEntity<Person> queryone(@PathVariable(value="username") String username) {
		LdapQuery query = query().base("").attributes("sn","distinguishedName","sAMAccountName","userPassword","givenName","displayName","telephoneNumber","department","company","mail","manager","streetAddress","description","title","l","st","info","wWWHomePage","postOfficeBox","pager","physicalDeliveryOfficeName").where("objectclass")
				.is("person").and("sAMAccountName").like(username);
		AttributesMapper<Person> mapper = new AttributesMapper<Person>() {
			
			public Person mapFromAttributes(Attributes attrs) throws javax.naming.NamingException {
				NamingEnumeration<? extends Attribute> attributes = attrs.getAll();
				//System.out.println(attrs.size());
				Person ret = new Person();
				
				ArrayList<String> s = new ArrayList<String> (Arrays.asList((null != attrs.get("distinguishedName") ? (String) attrs.get("distinguishedName").get() : "").split(",")));
				System.out.println(s);
				if(s.size() > 1) {
					s.remove(0);
					s.remove(s.size()-1);
					s.remove(s.size()-1);
					s.remove(s.size()-1);
				}

				ret.setStructure(String.join(",",(String[])s.toArray(new String[0])));
				
				ret.setFirstName(null != attrs.get("givenName") ? (String) attrs.get("givenName").get() : "");
				ret.setLastName(null != attrs.get("sn") ? (String) attrs.get("sn").get() : "");
				ret.setDisplayName(null != attrs.get("displayName") ? (String) attrs.get("displayName").get() : "");

				ret.setTelephoneNumber(null != attrs.get("telephoneNumber") ? (String) attrs.get("telephoneNumber").get() : "");
				ret.setDepartment(null != attrs.get("department") ? (String) attrs.get("department").get() : "");
				ret.setCompany(null != attrs.get("company") ? (String) attrs.get("company").get() : "");
				
				ret.setUserName(null != attrs.get("sAMAccountName") ? (String) attrs.get("sAMAccountName").get() : "");
				ret.setEmail(null != attrs.get("mail") ? (String) attrs.get("mail").get() : "");
				ret.setManager(null != attrs.get("manager") ? (String) attrs.get("manager").get() : "");

				ret.setAddress(null != attrs.get("streetAddress") ? (String) attrs.get("streetAddress").get() : "");
				ret.setDescription(null != attrs.get("description") ? (String) attrs.get("description").get() : "");
				ret.setJob(null != attrs.get("info") ? (String) attrs.get("info").get() : "");
				
				ret.setOfficeCity(null != attrs.get("l") ? (String) attrs.get("l").get() : "");
				ret.setOfficeState(null != attrs.get("st") ? (String) attrs.get("st").get() : "");
				ret.setCPF(null != attrs.get("postOfficeBox") ? (String) attrs.get("postOfficeBox").get() : "");
				
				ret.setRG(null != attrs.get("wWWHomePage") ? (String) attrs.get("wWWHomePage").get() : "");
				ret.setBirthDate(null != attrs.get("pager") ? (String) attrs.get("pager").get() : "");
				ret.setFunction(null != attrs.get("title") ? (String) attrs.get("title").get() : "");
				
				ret.setLocationCode(null != attrs.get("physicalDeliveryOfficeName") ? (String) attrs.get("physicalDeliveryOfficeName").get() : "");
				
				return ret;
			}
		};
		
		if(ldapTemplate == null) {
			ldapTemplate = setLdapTemplate();
		}
		
		if(ldapTemplate.search(query, mapper).size() > 0) {
			return new ResponseEntity<Person>(ldapTemplate.search(query, mapper).get(0),HttpStatus.OK);
		} else {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
	}
	
	@PutMapping("/employee")
	public ResponseEntity add(@RequestBody Person data) {
		
		String[] usernames = data.getUserNames();
		String username = data.getUserName();
		for (int i = 0 ; i < usernames.length; i++) {
			if(!queryone(usernames[i]).hasBody()) {
				username = usernames[i];
				break;
			}
		}
		
		
		try {
			System.out.println(data.toString());
			
			Attributes attrs = new BasicAttributes();
			BasicAttribute ocattr = new BasicAttribute("objectclass");
			ocattr.add("top");
			ocattr.add("person");
			ocattr.add("organizationalPerson");
			ocattr.add("user");
			
			attrs.put(ocattr);
			
			attrs.put("cn", data.getFullName());
			attrs.put("givenName", data.getFirstName());
			attrs.put("sn", data.getLastName());
			attrs.put("name",data.getDisplayName());
	
			attrs.put("displayName",data.getDisplayName());
			attrs.put("telephoneNumber",data.getTelephoneNumber());
			attrs.put("department",data.getDepartment());
			
			attrs.put("sAMAccountType","805306368");
			
			attrs.put("company",data.getCompany());
			attrs.put("mail",username + "@gafisact.com.br");
			
			Person sup = queryone(data.getManager()).getBody();
			
			attrs.put("manager","CN=" + sup.getFullName() + "," + sup.getStructure() + "," + Base);
			
			attrs.put("streetAddress",data.getAddress());
			attrs.put("description",data.getDescription());
			attrs.put("info",data.getJob());
			
			attrs.put("userPrincipalName",username + "@gafisact.com.br");
			
			attrs.put("mailNickname",username);
			
			attrs.put("sAMAccountName", username);
			
			attrs.put("distinguishedName","CN=" + data.getFullName() + "," + data.getStructure() + "," + Base);
			
			attrs.put("pager",data.getBirthDate());
			attrs.put("wWWHomePage",data.getRG());
			attrs.put("postOfficeBox",data.getCPF());
			attrs.put("title",data.getFunction());
			
			attrs.put("l",data.getOfficeCity());
			attrs.put("st",data.getOfficeState());
			
			attrs.put("physicalDeliveryOfficeName",data.getLocationCode());
			
			Name dn = buildPersonDn(data);
			
			System.out.println(dn.toString());
			
			if(ldapTemplate == null) {
				ldapTemplate = setLdapTemplate();
			}
			
			ldapTemplate.bind(dn,null,attrs);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String[]>(new String[]{},HttpStatus.OK);
	}
	
	@PostMapping("/employee/{username}")
	public ResponseEntity modify(@PathVariable(value="username") String username,@RequestBody Person data) {

		
		Person original = queryone(username).getBody();
						
		if(original == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		
		try {
			Name odn = buildPersonDn(original);
			
			Attributes attrs = new BasicAttributes();
			BasicAttribute ocattr = new BasicAttribute("objectclass");
			ocattr.add("top");
			ocattr.add("person");
			ocattr.add("organizationalPerson");
			ocattr.add("user");
			
			attrs.put(ocattr);
			
			String fullName = "";
			String structure = "";
			
			
			if(data.getFirstName() != "") {
				fullName += data.getFirstName();
			} else {
				fullName += original.getFirstName();
			}
			
			if(fullName != "") {
				fullName += " ";
			}
	
			if(data.getLastName() != "") {
				fullName += data.getLastName();
			} else {
				fullName += original.getLastName();
			}
			
			attrs.put("cn", fullName);
			
			if(data.getFirstName() != "") {
				attrs.put("givenName", data.getFirstName());
			} else {
				attrs.put("givenName", original.getFirstName());
			}
			
			if(data.getLastName() != "") {
				attrs.put("sn", data.getLastName());
			} else {
				attrs.put("sn", original.getLastName());
			}
			
			if(data.getDisplayName() != "") {
				attrs.put("name", data.getDisplayName());
			} else {
				attrs.put("name", original.getDisplayName());
			}
			
			if(data.getDisplayName() != "") {
				attrs.put("displayName", data.getDisplayName());
			} else {
				attrs.put("displayName", original.getDisplayName());
			}
			
			if(data.getTelephoneNumber() != "") {
				attrs.put("telephoneNumber", data.getTelephoneNumber());
			} else {
				attrs.put("telephoneNumber", original.getTelephoneNumber());
			}
			
			if(data.getDepartment() != "") {
				attrs.put("department", data.getDepartment());
			} else {
				attrs.put("department", original.getDepartment());
			}
			
			if(data.getCompany() != "") {
				attrs.put("company", data.getCompany());
			} else {
				attrs.put("company", original.getCompany());
			}
			
			if(data.getEmail() != "") {
				attrs.put("mail", data.getEmail());
			} else {
				attrs.put("mail", original.getEmail());
			}
			
			if(data.getManager() != "") {
				Person sup = queryone(data.getManager()).getBody();
				attrs.put("manager", "CN=" + sup.getFullName() + "," + sup.getStructure() + "," + Base);
			} else {
				attrs.put("manager", original.getManager());
			}
			
			if(data.getAddress() != "") {
				attrs.put("streetAddress", data.getAddress());
			} else {
				attrs.put("streetAddress", original.getAddress());
			}
			
			if(data.getDescription() != "") {
				attrs.put("description", data.getDescription());
			} else {
				attrs.put("description", original.getDescription());
			}
			
			if(data.getJob() != "") {
				attrs.put("info", data.getJob());
			} else {
				attrs.put("info", original.getJob());
			}
			
			if(data.getUserName() != "") {
				attrs.put("sAMAccountName", data.getUserName());
			} else {
				attrs.put("sAMAccountName", original.getUserName());
			}
			
			if(data.getEmail() != "") {
				attrs.put("mailNickname", data.getEmail().split("@")[0]);
			} else {
				attrs.put("mailNickname", original.getEmail().split("@")[0]);
			}
			
			if(data.getEmail() != "") {
				attrs.put("userPrincipalName", data.getEmail());
			} else {
				attrs.put("userPrincipalName", original.getEmail());
			}
			
			if(data.getStructure() != "") {
				structure = data.getStructure();
			} else {
				structure = original.getStructure();
			}
			
			attrs.put("distinguishedName", "CN=" + fullName + "," + structure + "," + Base);
	
			attrs.put("sAMAccountType","805306368");
			
			if(data.getBirthDate() != "") {
				attrs.put("pager",data.getBirthDate());
			} else {
				attrs.put("pager",data.getBirthDate());
			}
			
			if(data.getRG() != "") {
				attrs.put("wWWHomePage",data.getRG());
			} else {
				attrs.put("wWWHomePage",original.getRG());
			}
			
			if(data.getCPF() != "") {
				attrs.put("postOfficeBox",data.getCPF());
			} else {
				attrs.put("postOfficeBox",original.getCPF());
			}
			
			if(data.getFunction() != "") {
				attrs.put("title",data.getFunction());
			} else {
				attrs.put("title",original.getFunction());
			}
			
			if(data.getOfficeState() != "") {
				attrs.put("st",data.getOfficeState());
			} else {
				attrs.put("st",original.getOfficeState());
			}
			
			if(data.getOfficeCity() != "") {
				attrs.put("l",data.getOfficeCity());
			} else {
				attrs.put("l",original.getOfficeCity());
			}
			
			if(data.getLocationCode() != "") {
				attrs.put("physicalDeliveryOfficeName",data.getLocationCode());
			} else {
				attrs.put("physicalDeliveryOfficeName",original.getLocationCode());
			}

			
			Name dn = buildPersonDn(structure,fullName);
			
			if(ldapTemplate == null) {
				ldapTemplate = setLdapTemplate();
			}
			
			ldapTemplate.unbind(odn);
			ldapTemplate.bind(dn, null, attrs);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@DeleteMapping("/employee/{username}")
	public ResponseEntity remove(@PathVariable(value="username") String username) {
		
		Person original = queryone(username).getBody();
		
		if(original == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		
		try {
			Name odn = buildPersonDn(original);
			
			Attributes attrs = new BasicAttributes();
			BasicAttribute ocattr = new BasicAttribute("objectclass");
			
			ocattr.add("top");
			ocattr.add("person");
			ocattr.add("organizationalPerson");
			ocattr.add("user");
			
			attrs.put(ocattr);
			
			attrs.put("cn", original.getFullName());
			attrs.put("givenName", original.getFirstName());
			attrs.put("sn", original.getLastName());
			attrs.put("name",original.getDisplayName());
	
			attrs.put("displayName",original.getDisplayName());
			attrs.put("telephoneNumber",original.getTelephoneNumber());
			attrs.put("department",original.getDepartment());
			
			attrs.put("sAMAccountType","805306368");
			
			attrs.put("company",original.getCompany());
			attrs.put("mail",original.getEmail());
			attrs.put("manager",original.getManager());
			
			attrs.put("streetAddress",original.getAddress());
			attrs.put("description",original.getDescription());
			attrs.put("info",original.getJob());
			
			attrs.put("userPrincipalName",original.getEmail());
			
			attrs.put("mailNickname",original.getEmail().split("@")[0]);
			
			attrs.put("sAMAccountName", original.getUserName());
			
			attrs.put("distinguishedName","CN=" + original.getFullName() + "," + "OU=Desabilitado" + "," + Base);
			
			attrs.put("pager",original.getBirthDate());
			attrs.put("wWWHomePage",original.getRG());
			attrs.put("postOfficeBox",original.getCPF());
			attrs.put("title",original.getFunction());
			
			attrs.put("l",original.getOfficeCity());
			attrs.put("st",original.getOfficeState());
			
			attrs.put("physicalDeliveryOfficeName",original.getLocationCode());
			
			
			if(ldapTemplate == null) {
				ldapTemplate = setLdapTemplate();
			}
			
			Name dn = buildDisabledDn(original);
			ldapTemplate.unbind(odn);
			ldapTemplate.bind(dn,null,attrs);
		} catch(Exception e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity(HttpStatus.OK);
	}
	
	/*
	public Name buildGroupDn(Group g) {
		return (Name)LdapNameBuilder.newInstance()
				.add("cn",g.getGroupName())
				.build();
	}
	
	@GetMapping("/group")
	public List<String> queryallgroup() {
		LdapQuery query = query().base("").attributes("entryDN","cn","uniqueMember").where("objectclass").is("groupOfUniqueNames");
		
		AttributesMapper<String> mapper = new AttributesMapper<String>() {
			
			public String mapFromAttributes(Attributes attrs) throws javax.naming.NamingException {
				NamingEnumeration<? extends Attribute> attributes = attrs.getAll();
				String ret = "{";
				while (attributes.hasMore()) {
					Attribute a = attributes.next();
					if(a.size() > 1) {
						for(int i = 0; i < a.size(); i++) {
							ret += " " + a.getID() + " : " + a.get(i) + ",";
						}
					} else {
						ret += " " + a.getID() + " : " + a.get() + ",";
					}
				}
				ret += "}";
				return ret;
			}
		};
		return ldapTemplate.search(query, mapper);
	}
	
	@PutMapping("/group")
	public String addgroup(@RequestBody Group data) {
		
		System.out.println(data.toString());
		
		Attributes attrs = new BasicAttributes();
		BasicAttribute ocattr = new BasicAttribute("objectclass");
		ocattr.add("top");
		ocattr.add("groupOfUniqueNames");
		attrs.put(ocattr);
		attrs.put("cn", data.getGroupName());

		
		Name dn = buildGroupDn(data);
		
		String[] members = data.getUniqueMembers();
		
		for (int i = 0; i < members.length; i++) {
			attrs.put("uniqueMember", members[i]);
		}
		
		ldapTemplate.bind(dn,null,attrs);
		return data.toString();
	}
	
	@PatchMapping("/group")
	public String usertogroup(@RequestAttribute String action, @RequestAttribute String PersonDn, @RequestAttribute Group data) {
		
		System.out.println(data.toString());
		
		Attribute attr = new BasicAttribute("uniqueMember",PersonDn);
		
		Name dn = buildGroupDn(data);
		ModificationItem item = null;
		if(action=="ADD") {
			item = new ModificationItem(DirContext.ADD_ATTRIBUTE, attr);
		} else if(action=="REMOVE"){
			item = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attr);
		}
		if(item != null) {
			ldapTemplate.modifyAttributes(dn,new ModificationItem[] {item});
		}

		return data.toString();
	}
	
	@PostMapping("/group")
	public String modifygroup(@RequestBody Group data) {
		Attributes attrs = new BasicAttributes();
		BasicAttribute ocattr = new BasicAttribute("objectclass");
		ocattr.add("top");
		ocattr.add("groupOfUniqueNames");
		attrs.put(ocattr);
		attrs.put("cn", data.getGroupName());

		
		Name dn = buildGroupDn(data);
		
		String[] members = data.getUniqueMembers();
		
		for (int i = 0; i < members.length; i++) {
			attrs.put("uniqueMember", members[i]);
		}
		
		ldapTemplate.rebind(dn,null,attrs);
		return data.toString();
	}
	
	@DeleteMapping("/group")
	public String removegroup(@RequestBody Group data) {
		Name dn = buildGroupDn(data);
		ldapTemplate.unbind(dn);
		return data.toString();

	}
	*/
}
