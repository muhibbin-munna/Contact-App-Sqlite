package com.app.contactappsp.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {
    private int id;
    private String fullName,phone,gender,age,address1,address2,city, postCode,remark,image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Contact() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(fullName);
        dest.writeString(phone);
        dest.writeString(gender);
        dest.writeString(age);
        dest.writeString(address1);
        dest.writeString(address2);
        dest.writeString(city);
        dest.writeString(postCode);
        dest.writeString(remark);
        dest.writeString(image);
    }

    protected Contact(Parcel in) {
        this.id = in.readInt();
        this.fullName = in.readString();
        this.phone = in.readString();
        this.gender = in.readString();
        this.age = in.readString();
        this.address1 = in.readString();
        this.address2 = in.readString();
        this.city = in.readString();
        this.postCode = in.readString();
        this.remark = in.readString();
        this.image = in.readString();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}
