package org.whut.platform.business.user.entity;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 14-1-26
 * Time: 上午11:28
 * To change this template use File | Settings | File Templates.
 */
public class User {
    private long id;
    private String name;
    private String password;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
