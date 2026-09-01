 package com.dentalclinic.servlet; 
 import org.mindrot.jbcrypt.BCrypt; 
 
 public class GenerateHash { 
	 public static void main(String[] args) { 
		 
		 String hash = BCrypt.hashpw("superadmin", BCrypt.gensalt()); 
		 System.out.println(hash); 
		 } 
	 
 }