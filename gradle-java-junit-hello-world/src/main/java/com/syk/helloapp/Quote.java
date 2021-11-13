package com.syk.helloapp;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Quote {
 private Long id;
 private String who;
 private String what;
 
 public void setId(Long id) {
 this.id = id;
 }
 public void setWho(String who) {
 this.who = who;
 }
 public void setWhat(String what) {
 this.what = what;
 }
 public Long getId() {
 return id;
 }
 public String getWho() {
 return who;
 }
 public String getWhat() {
 return what;
 }
public String toString() {
 return ToStringBuilder.reflectionToString(this);
 }
}