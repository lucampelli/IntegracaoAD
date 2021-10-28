package com.ad;

public class Group {
	protected String GroupName;
	protected String[] UniqueMembers;
	
	
	
	public Group(String GroupName, String[] UniqueMembers) {
		this.GroupName = GroupName;
		this.UniqueMembers = UniqueMembers;
	}

	public void addUniqueMember(String dn) {
		for (int i = 0; i < UniqueMembers.length; i++) {
			if(UniqueMembers[i] == dn) {
				return;
			}
		}
		String[] temp = new String[UniqueMembers.length+1];
		
		for (int i = 0; i < UniqueMembers.length; i++) {
				temp[i] = UniqueMembers[i];
		
		}
		temp[UniqueMembers.length] = dn;
		UniqueMembers = temp;
	}
	
	public void removeUniqueMember(String dn) {
		int index = -1;
		for (int i = 0; i < UniqueMembers.length; i++) {
			if(UniqueMembers[i] == dn) {
				index = i;
				break;
			}
		}
		String[] temp = new String[UniqueMembers.length-1];
		
		for (int i = 0; i < UniqueMembers.length; i++) {
			if(i < index) {
				temp[i] = UniqueMembers[i];
			} else if(i > index) {
				temp [i-1] = UniqueMembers[i];
			}
			
		}
		UniqueMembers = temp;
	}
	
	public String toString(){
		
		String ret = "{\"GroupName\":"+GroupName+",\"UniqueMembers\":[";
		for (int i = 0; i < UniqueMembers.length; i++) {
			ret += UniqueMembers[i]; 
		}
		
		return ret + "]}";
	}

	public String getGroupName() {
		return GroupName;
	}

	public void setGroupName(String groupName) {
		GroupName = groupName;
	}

	public String[] getUniqueMembers() {
		return UniqueMembers;
	}

	public void setUniqueMembers(String[] uniqueMembers) {
		UniqueMembers = uniqueMembers;
	}	
}
