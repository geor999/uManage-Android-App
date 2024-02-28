package com.example.uManage.object_classes;

public class Worker {

    private String imagename;
    private int id;
    private String name;
    private int age;
    private int salary;
    private String user;
    private byte[] image;

    /**
     * @param id
     * @param name το όνομα του εργαζόμενου
     * @param age η ηλικία του εργαζόμενου
     * @param salary ο μισθός του εργαζόμενου
     * @param imagename Το όνομα της φωτογραφίας που χρησιμοποιήσαμε
     * @param user η εταιρία που δημιούργησε αυτό το προιόν
     * @param image Η εικόνα που ανέβασε ο χρήστης για τον εργαζόμενο αυτό
     */
    public Worker(int id, String name, int age, int salary, String imagename, String user, byte[] image) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.salary = salary;
        this.user = user;
        this.image = image;
        this.imagename=imagename;
    }

    public int getId() {
        return id;
    }
    public int getAge() {
        return age;
    }

    public int getSalary() {
        return salary;
    }

    public String getUser() {
        return user;
    }

    public byte[] getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAge(int age) {
        this.age = age;
    }

    public void setPriority(int prio) {
        this.salary = prio;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }
}
