package com.atgenee

class User {

    String name
    Integer age

    User(String name, Integer age) {
        this.name = name
        this.age = age
    }

    static constraints = {
        name(blank: false)
        age()
    }
}
