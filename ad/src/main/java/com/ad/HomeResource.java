package com.ad;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.util.List;

import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HomeResource {
	
	LdapTemplate ldapTemplate = setLdapTemplate();
	String Base = "DC=GAFISACT,DC=COM,DC=BR";

	
	public LdapTemplate setLdapTemplate() {
		LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl("ldap://localhost:8389");
        contextSource.setBase("DC=GAFISACT,DC=COM,DC=BR");
        contextSource.afterPropertiesSet();
        LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
        try {
            ldapTemplate.afterPropertiesSet();
        } catch (Exception e) {
            System.out.print("Failed to initialize ldaptemplate "); e.printStackTrace();
            return null;
        }
        return ldapTemplate;
	}
	
	public Name buildPersonDn(Person p) {
		
		String[] struc = p.getStructureAD().split(",");
		LdapNameBuilder b = LdapNameBuilder.newInstance("DC=GAFISACT,DC=COM,DC=BR");
		
		for(int i = struc.length -1; i >=0; i--) {
			String[] t = struc[i].split("=");
			b.add(t[0],t[1]);
		}
		b.add("CN",p.getFullName());
		
		return (Name)b.build();
	}
	
public Name buildDisabledDn(Person p) {
		
		String[] struc = p.getStructureAD().split(",");
		LdapNameBuilder b = LdapNameBuilder.newInstance("DC=GAFISACT,DC=COM,DC=BR");
		
		b.add("OU","Desabilitado");
		b.add("CN",p.getFullName());
		
		return (Name)b.build();
	}


	@GetMapping("/employee")
	public ResponseEntity<List<Person>> queryall() {
		LdapQuery query = query().base("").attributes("sn","distinguishedName","sAMAccountName","userPassword","givenName","displayName","telephoneNumber","department","company","mail","manager","streetAddress","description","title").where("objectclass").is("person");
		AttributesMapper<Person> mapper = new AttributesMapper<Person>() {
			
			public Person mapFromAttributes(Attributes attrs) throws javax.naming.NamingException {
				NamingEnumeration<? extends Attribute> attributes = attrs.getAll();
				System.out.println(attrs.size());
				Person ret = new Person();
				
				List<String> s = List.of((null != attrs.get("distinguishedName") ? (String) attrs.get("distinguishedName").get() : "").split(","));
				s.remove(0);
				s.remove(s.size()-1);
				s.remove(s.size()-1);
				s.remove(s.size()-1);
				ret.setStructureAD(String.join(",",(String[])s.toArray()));
				
				ret.setFirstName(null != attrs.get("givenName") ? (String) attrs.get("givenName").get() : "");
				ret.setLastName(null != attrs.get("sn") ? (String) attrs.get("sn").get() : "");
				ret.setDisplayName(null != attrs.get("displayName") ? (String) attrs.get("displayName").get() : "");

				ret.setTelefone(null != attrs.get("telephoneNumber") ? (String) attrs.get("telephoneNumber").get() : "");
				ret.setDepartment(null != attrs.get("department") ? (String) attrs.get("department").get() : "");
				ret.setCompany(null != attrs.get("company") ? (String) attrs.get("company").get() : "");
				
				ret.setUserName(null != attrs.get("sAMAccountName") ? (String) attrs.get("sAMAccountName").get() : "");
				ret.setEmail(null != attrs.get("mail") ? (String) attrs.get("mail").get() : "");
				ret.setManager(null != attrs.get("manager") ? (String) attrs.get("manager").get() : "");

				ret.setAddress(null != attrs.get("streetAddress") ? (String) attrs.get("streetAddress").get() : "");
				ret.setDescription(null != attrs.get("description") ? (String) attrs.get("description").get() : "");
				ret.setJob(null != attrs.get("title") ? (String) attrs.get("title").get() : "");
				
				return ret;
			}
		};
		return new ResponseEntity<List<Person>>(ldapTemplate.search(query, mapper),HttpStatus.OK);
	}
	
	@PutMapping("/employee")
	public String add(@RequestBody Person data) {
		
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
		attrs.put("telephoneNumber",data.getTelefone());
		attrs.put("department",data.getDepartment());
		
		attrs.put("sAMAccountType","805306368");
		
		attrs.put("company",data.getCompany());
		attrs.put("mail",data.getEmail());
		attrs.put("manager",data.getManager());
		
		attrs.put("streetAddress",data.getAddress());
		attrs.put("description",data.getDescription());
		attrs.put("title",data.getJob());
		
		attrs.put("userPrincipalName",data.getEmail());
		
		attrs.put("mailNickname",data.getEmail().split("@")[0]);
		
		attrs.put("sAMAccountName", data.getUserName());
		
		attrs.put("distinguishedName","CN=" + data.getFullName() + "," + data.getStructureAD() + "," + Base);
		
		Name dn = buildPersonDn(data);
		
		System.out.println(dn.toString());
		
		ldapTemplate.bind(dn,null,attrs);
		return data.toString();
	}
	
	@PostMapping("/employee")
	public String modify(@RequestBody Person data) {
		
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
		attrs.put("telephoneNumber",data.getTelefone());
		attrs.put("department",data.getDepartment());
		
		attrs.put("sAMAccountType","805306368");
		
		attrs.put("company",data.getCompany());
		attrs.put("mail",data.getEmail());
		attrs.put("manager",data.getManager());
		
		attrs.put("streetAddress",data.getAddress());
		attrs.put("description",data.getDescription());
		attrs.put("title",data.getJob());
		
		attrs.put("userPrincipalName",data.getEmail());
		
		attrs.put("mailNickname",data.getEmail().split("@")[0]);
		
		attrs.put("sAMAccountName", data.getUserName());
		
		attrs.put("distinguishedName","CN=" + data.getFullName() + "," + data.getStructureAD() + "," + Base);
		
		Name dn = buildPersonDn(data);
		
		ldapTemplate.rebind(dn, null, attrs);
		
		return data.toString();
	}
	
	@DeleteMapping("/employee")
	public String remove(@RequestBody Person data) {
		Name dn = buildPersonDn(data);
		
		ldapTemplate.unbind(dn);
		
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
		attrs.put("telephoneNumber",data.getTelefone());
		attrs.put("department",data.getDepartment());
		
		attrs.put("sAMAccountType","805306368");
		
		attrs.put("company",data.getCompany());
		attrs.put("mail",data.getEmail());
		attrs.put("manager",data.getManager());
		
		attrs.put("streetAddress",data.getAddress());
		attrs.put("description",data.getDescription());
		attrs.put("title",data.getJob());
		
		attrs.put("userPrincipalName",data.getEmail());
		
		attrs.put("mailNickname",data.getEmail().split("@")[0]);
		
		attrs.put("sAMAccountName", data.getUserName());
		
		attrs.put("distinguishedName","CN=" + data.getFullName() + "," + data.getStructureAD() + "," + Base);
		
		dn = buildDisabledDn(data);
		
		ldapTemplate.bind(dn,null,attrs);
		
		return data.toString();
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
