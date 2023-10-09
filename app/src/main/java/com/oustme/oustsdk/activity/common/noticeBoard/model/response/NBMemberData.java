package com.oustme.oustsdk.activity.common.noticeBoard.model.response;

import androidx.annotation.Keep;

import com.oustme.oustsdk.activity.common.leaderboard.model.GroupDataResponse;

import java.util.Comparator;

/**
 * Created by oust on 3/6/19.
 */

@Keep
public class NBMemberData {
    private String fname,lname,department,city,state,country,role,email, phone,assigneeRole,avatar, studentid;

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAssigneeRole() {
        return assigneeRole;
    }

    public void setAssigneeRole(String assigneeRole) {
        this.assigneeRole = assigneeRole;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public boolean hasAvatar() {
        return this.avatar != null && !this.avatar.isEmpty();
    }

    public String getUserLocation(){
        String location ="";
        if(this.city!=null){
            location=this.city;
        }
        if(this.state!=null){
            if(!location.isEmpty())
                location=location+","+this.state;
            else
                location=this.state;
        }
        if(this.country!=null){
            if(!location.isEmpty())
                location=location+","+this.country;
            else
                location=this.country.toUpperCase();
        }
        return location;
    }

    public static Comparator<NBMemberData> user = new Comparator<NBMemberData>() {

        public int compare(NBMemberData s1, NBMemberData s2)
        {
            if(s1.getFname()!=null && s2.getFname()!=null)
            {
                String StudentName1 = s1.getFname().toUpperCase();
                String StudentName2 = s2.getFname().toUpperCase();
                //ascending order
                return StudentName1.compareTo(StudentName2);
            }
            return 0;
            //descending order
            //return StudentName2.compareTo(StudentName1);
        }
    };

}
